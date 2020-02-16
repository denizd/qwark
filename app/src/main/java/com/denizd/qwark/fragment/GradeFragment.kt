package com.denizd.qwark.fragment

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.denizd.qwark.R
import com.denizd.qwark.adapter.GradeAdapter
import com.denizd.qwark.sheet.ConfirmDeletionBottomSheet
import com.denizd.qwark.sheet.GradeCreateBottomSheet
import com.denizd.qwark.databinding.GradeFragmentBinding
import com.denizd.qwark.model.CourseExam
import com.denizd.qwark.model.Grade
import com.denizd.qwark.model.HistoricalAvg
import com.denizd.qwark.util.QwarkUtil
import com.denizd.qwark.util.calculateAverage
import com.denizd.qwark.util.getUserDefinedAverage
import com.denizd.qwark.viewmodel.GradeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class GradeFragment : QwarkFragment(), GradeAdapter.GradeClickListener {

    private lateinit var currentCourse: CourseExam
    private var average = ""
    private var schoolYear = ""
    private var openedFirstTime = false
    private lateinit var weightingString: String
    private lateinit var adapter: GradeAdapter

    private lateinit var viewModel: GradeViewModel
    private lateinit var inflater: LayoutInflater

    private var _binding: GradeFragmentBinding? = null
    private val binding: GradeFragmentBinding get() = _binding!!

    private var scrollPosition: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[GradeViewModel::class.java]

//        currentCourse = QwarkUtil.getCourseFromBundle(arguments)
        schoolYear = arguments?.getString("schoolYear") ?: ""
        currentCourse = viewModel.getCourse(arguments?.getInt("courseId") ?: -1)
        average = currentCourse.average
        weightingString = "${currentCourse.oralWeighting} / ${100 - currentCourse.oralWeighting}"

        viewModel.getGradesByForeignKey(currentCourse.courseId).observe(this, Observer { grades ->

            adapter.setGrades(grades)

            if (scrollPosition == null) {
                binding.recyclerView.scheduleLayoutAnimation()
//            } else {
//                binding.recyclerView.layoutManager?.onRestoreInstanceState(scrollPosition)
            }
//            average = viewModel.calculateAverage(grades.filter { it.grade != -1 }, currentCourse.oralWeighting)
            grades.filter { it.grade != -1 }.also { filteredGrades ->
                average = filteredGrades.calculateAverage(
                    currentCourse.oralWeighting
                )
                binding.average.text = if (filteredGrades.isEmpty()) "X" else average.getUserDefinedAverage(getGradeType())
                viewModel.updateAverage(average, filteredGrades.size, currentCourse.courseId)
            }

            if (openedFirstTime) {
                insertNewHistoricalAverage()
            } else {
                openedFirstTime = true
            }
        })
        adapter = GradeAdapter(ArrayList(), this, getGradeType())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = GradeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        superTitle = schoolYear
        appBarTitle = String.format(
            (if (currentCourse.advanced) getString(R.string.advanced_abbreviated_placeholder) else "%s"),
            currentCourse.name
        )
        fab.hide()
        with(view.rootView) {
            findViewById<BottomNavigationView>(R.id.bottom_nav).visibility = View.GONE
            findViewById<View>(R.id.view).visibility = View.GONE
        }

        binding.addGradeButton.setOnClickListener {
            presentGradeDialog()
        }

        binding.average.text = currentCourse.average
        binding.weighting.text = weightingString

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(context, 1)

        binding.openParticipationFragmentButton.setOnClickListener {
            openParticipationCourseFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.paddingView.applyPadding()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun insertNewHistoricalAverage() {
        viewModel.insert(
            HistoricalAvg(
                courseId = currentCourse.courseId,
                average = average,
                time = System.currentTimeMillis()
            )
        )
    }

    private fun presentGradeDialog(grade: Grade? = null) {
        openBottomSheet(GradeCreateBottomSheet().also { sheet ->
            if (grade == null) {
                sheet.arguments = Bundle(1).apply { putInt("courseId", currentCourse.courseId) }
            } else {
                QwarkUtil.putGradeInBundle(sheet, grade)
            }
        })
    }

    override fun onGradeClick(grade: Grade) {
        presentGradeDialog(grade)
    }

    override fun onGradeLongClick(gradeId: Int) {
        val deletionSheet = ConfirmDeletionBottomSheet(getString(R.string.confirm_grade_deletion)) {
            viewModel.delete(gradeId)
        }
        openBottomSheet(deletionSheet)
    }

    private fun openParticipationCourseFragment() {
        appBar.setExpanded(true)
        val f = ParticipationCourseFragment()
        f.arguments = Bundle().apply {
            putString("course", currentCourse.name)
            putInt("courseId", currentCourse.courseId)
            putBoolean("advanced", currentCourse.advanced)
        }
        // for targetFragment to work, ParticipationFragment needs to be active
        // TODO fix the dependency on targetFragment (maybe introduce a ParticipationCourseViewModel)
//        f.setTargetFragment((context as FragmentActivity).supportFragmentManager.findFragmentByTag("ParticipationFragment"), 42)
        (context as FragmentActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, f)
            .addToBackStack(f.name)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    fun insert(grade: Grade) { viewModel.insert(grade) }
    fun update(grade: Grade) { viewModel.update(grade) }
    fun getGradeType(): Int = viewModel.getGradeType()
}