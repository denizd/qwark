package com.denizd.qwark.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.denizd.qwark.R
import com.denizd.qwark.adapter.CourseAdapter
import com.denizd.qwark.adapter.YearAdapter
import com.denizd.qwark.databinding.RecyclerDialogBinding
import com.denizd.qwark.fragment.FinalsFragment
import com.denizd.qwark.model.CourseExam
import com.denizd.qwark.model.FinalGrade
import com.denizd.qwark.model.SchoolYear
import com.denizd.qwark.util.getSorted
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.math.BigDecimal
import java.math.RoundingMode

class FinalsLinkSheet : BottomSheetDialogFragment(), YearAdapter.YearClickListener, CourseAdapter.CourseClickListener {

    private lateinit var finalGrade: FinalGrade
    private lateinit var binding: RecyclerDialogBinding
    private var advanced = false
    private var isSelectingCourse = false
    private lateinit var finalsFragment: FinalsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finalsFragment = targetFragment as FinalsFragment
        finalGrade = finalsFragment.getFinalGrade(arguments?.getInt("finalGradeId") ?: -1)
        advanced = finalGrade.type == "adv"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = RecyclerDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        finalsFragment = targetFragment as FinalsFragment
        fillWithYears()
    }

    override fun onYearClick(schoolYear: SchoolYear) {
        fillWithCourses(schoolYear)
    }

    override fun onCourseClick(course: CourseExam) {
        val newFinalGrade = FinalGrade(
            name = course.name,
            grade = BigDecimal(course.average.toDouble()).setScale(0, RoundingMode.HALF_UP).toString(),
            type = finalGrade.type,
            note = finalGrade.note,
            courseId = course.courseId,
            finalGradeId = finalGrade.finalGradeId,
            scoreProfileId = finalGrade.scoreProfileId
        )
        finalsFragment.update(newFinalGrade)
        dismiss()
    }

    override fun onCourseLongClick(course: CourseExam) {}

    private fun fillWithYears() {
        binding.title.text = getString(R.string.pick_a_year)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = YearAdapter(finalsFragment.getAllYears(), this@FinalsLinkSheet)
            scheduleLayoutAnimation()
        }
        isSelectingCourse = false
    }

    private fun fillWithCourses(schoolYear: SchoolYear) {
        binding.title.text = getString(R.string.pick_a_course)
        binding.recyclerView.adapter = CourseAdapter(
            finalsFragment.getAllCourses().filter { c ->
                c.yearId == schoolYear.yearId && c.advanced == advanced
            }.getSorted(
                finalsFragment.getCourseSortType()
            ),
            this,
            finalsFragment.getGradeType()
        )
        binding.recyclerView.scheduleLayoutAnimation()
        isSelectingCourse = true
    }
}