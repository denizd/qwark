package com.denizd.qwark.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.denizd.qwark.R
import com.denizd.qwark.adapter.CourseAdapter
import com.denizd.qwark.sheet.ConfirmDeletionSheet
import com.denizd.qwark.sheet.CourseCreateSheet
import com.denizd.qwark.sheet.CourseOptionsSheet
import com.denizd.qwark.databinding.*
import com.denizd.qwark.sheet.HistoricalAverageSheet
import com.denizd.qwark.model.Course
import com.denizd.qwark.model.CourseExam
import com.denizd.qwark.util.calculateTotalAverage
import com.denizd.qwark.util.getSorted
import com.denizd.qwark.viewmodel.CourseViewModel
import com.google.android.material.snackbar.Snackbar
import java.lang.NumberFormatException

class CourseFragment : QwarkFragment(), CourseAdapter.CourseClickListener {

    private var _binding: PaddedRecyclerViewBinding? = null
    private val binding: PaddedRecyclerViewBinding get() = _binding!!

    private val viewModel: CourseViewModel by viewModels()
    private lateinit var recyclerViewAdapter: CourseAdapter

    private var scrollPosition: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerViewAdapter = CourseAdapter(ArrayList(), this, viewModel.getGradeType())
        viewModel.allCourses.observe(this, Observer { courses ->
            recyclerViewAdapter.setCourses(courses.getSorted(viewModel.getCourseSortType()))
            if (scrollPosition == null) {
                binding.recyclerView.apply {
                    scrollToPosition(0)
                    scheduleLayoutAnimation()
                }
            } else {
                binding.recyclerView.layoutManager?.onRestoreInstanceState(scrollPosition)
            }
            superTitle = try {
                courses.calculateTotalAverage(
                    context,
                    viewModel.getGradeType(),
                    viewModel.getSchoolYearName(),
                    viewModel.getShowGradeAverage()
                )
            } catch (e: NumberFormatException) {
                getString(R.string.grade_format_error)
            }
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(
            getGridColumnCount(newConfig),
            StaggeredGridLayoutManager.VERTICAL
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = PaddedRecyclerViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appBarTitle = getString(R.string.your_courses)

        binding.recyclerView.apply {
            adapter = recyclerViewAdapter
            layoutManager = StaggeredGridLayoutManager(
                getGridColumnCount(resources.configuration),
                StaggeredGridLayoutManager.VERTICAL
            )
            addFabScrollListener()
        }

        fab.apply {
            show()
            text = getString(R.string.add_course)
            setOnClickListener {
                if (viewModel.getSchoolYearName().isBlank()) {
                    presentErrorSnackBar(getString(R.string.no_school_year_selected))
                } else {
                    createCourseDialog()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.recyclerView.apply {
            applyPadding(horizontalPadding = 4)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCourseClick(course: CourseExam) {
        appBar.setExpanded(true)
        val f = GradeFragment()
        f.arguments = Bundle().apply {
            putString("schoolYear", viewModel.getSchoolYearName())
            putInt("courseId", course.courseId)
        }
        (context as FragmentActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, f)
            .addToBackStack("GradeFragment")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun onCourseLongClick(course: CourseExam) {
        createOptionsDialog(course)
    }

    private fun createOptionsDialog(course: CourseExam) {
        openBottomSheet(CourseOptionsSheet().also { sheet ->
            sheet.arguments = Bundle().apply { putInt("courseId", course.courseId) }
        })
    }

    private fun presentErrorSnackBar(message: String) {
        Snackbar
            .make(snackBarContainer, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(context.getColor(R.color.colorWarning))
            .show()
    }

    // functions for use by bottom sheets
    fun insert(course: Course) { viewModel.insert(course) }
    fun update(course: Course) { viewModel.update(course) }
    fun getSchoolYear() = viewModel.getSchoolYear()
    fun getGradeType() = viewModel.getGradeType()
    fun getAveragesForCourse(courseId: Int) = viewModel.getAveragesForCourses(courseId)

    fun createHistoryDialog(courseId: Int, course: String) {
        openBottomSheet(HistoricalAverageSheet().also { sheet ->
            sheet.arguments = Bundle(2).apply {
                putString("courseName", course)
                putInt("courseId", courseId)
            }
        })
    }

    fun createCourseDialog(course: CourseExam? = null) {
        openBottomSheet(CourseCreateSheet().also { sheet ->
            if (course != null) sheet.arguments = Bundle().apply { putInt("courseId", course.courseId) }
        })
    }

    fun deleteCourse(course: Course) {
        openBottomSheet(
            ConfirmDeletionSheet(
                getString(R.string.delete_course_and_data_confirmation, course.name)
            ) {
                viewModel.delete(course)
            }
        )
    }

    fun getCourse(courseId: Int): CourseExam = viewModel.getCourse(courseId)
}
