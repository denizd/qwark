package com.denizd.qwark.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.denizd.qwark.R
import com.denizd.qwark.adapter.CourseAdapter
import com.denizd.qwark.databinding.PaddedRecyclerViewBinding
import com.denizd.qwark.model.CourseExam
import com.denizd.qwark.model.Participation
import com.denizd.qwark.sheet.ConfirmDeletionBottomSheet
import com.denizd.qwark.sheet.ParticipationLinkBottomSheet
import com.denizd.qwark.viewmodel.ParticipationViewModel

internal class ParticipationFragment : QwarkFragment(), CourseAdapter.CourseClickListener {

    private var _binding: PaddedRecyclerViewBinding? = null
    private val binding: PaddedRecyclerViewBinding get() = _binding!!
    private lateinit var viewModel: ParticipationViewModel
    private val recyclerViewAdapter = CourseAdapter(ArrayList(), this, null, true)

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(getGridColumnCount(newConfig), StaggeredGridLayoutManager.VERTICAL)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ParticipationViewModel::class.java]
        viewModel.allCoursesForToday.observe(this, Observer { courses ->
            recyclerViewAdapter.setCourses(
                // TODO
                courses.map { c ->
                    CourseExam(
                        c.name,
                        c.advanced,
                        c.icon,
                        c.colour,
                        c.average,
                        c.oralWeighting,
                        c.gradeCount,
                        c.participation,
                        c.time,
                        c.courseId,
                        c.yearId,
                        0
                    )
                }
            )
            binding.recyclerView.scheduleLayoutAnimation()
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = PaddedRecyclerViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        superTitle = getDay()
        appBarTitle = getString(R.string.your_participation)
        fab.apply {
            show()
            text = getString(R.string.link_course)
            setOnClickListener {
                openLinkSheet()
            }
        }

        binding.recyclerView.apply {
            adapter = recyclerViewAdapter
            layoutManager = StaggeredGridLayoutManager(
                getGridColumnCount(resources.configuration), StaggeredGridLayoutManager.VERTICAL
            )
        }
    }

    override fun onResume() {
        super.onResume()
        binding.recyclerView.applyPadding(verticalPadding = 4)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCourseClick(course: CourseExam) {
        appBar.setExpanded(true)
        (context as FragmentActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ParticipationCourseFragment().also { fragment ->
                fragment.arguments = Bundle().apply {
                    putString("course", course.name)
                    putInt("courseId", course.courseId)
                    putBoolean("advanced", course.advanced)
                }
                fragment.setTargetFragment(this, 42)
            })
            .addToBackStack("ParticipationCourseFragment")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun onCourseLongClick(course: CourseExam) {
        // in this context, Course#oralWeighting is used as CourseDayRelation#relationId
        openRemoveLinkSheet(course.name, course.oralWeighting)
    }

    private fun openLinkSheet() {
        openBottomSheet(ParticipationLinkBottomSheet())
    }

    private fun openRemoveLinkSheet(courseName: String, relationId: Int) {
        openBottomSheet(ConfirmDeletionBottomSheet(
            getString(R.string.confirm_remove_participation_link, courseName, getDay())
        ) {
            viewModel.removeLink(relationId)
        })
    }

    private fun getDay(): String = resources.getStringArray(R.array.days)[viewModel.dayId - 1]

    // target fragment functions
    fun getCourses(): List<CourseExam> = viewModel.getCourses()
    fun link(courseId: Int) { viewModel.link(courseId) }
    fun delete(participation: Participation) { viewModel.delete(participation) }
    fun getSchoolYearName() = viewModel.getSchoolYearName()
}