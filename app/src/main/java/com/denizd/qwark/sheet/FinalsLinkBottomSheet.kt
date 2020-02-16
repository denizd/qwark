package com.denizd.qwark.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.denizd.qwark.R
import com.denizd.qwark.adapter.CourseAdapter
import com.denizd.qwark.adapter.YearAdapter
import com.denizd.qwark.util.QwarkUtil
import com.denizd.qwark.databinding.RecyclerDialogBinding
import com.denizd.qwark.fragment.FinalsFragment
import com.denizd.qwark.model.Course
import com.denizd.qwark.model.CourseExam
import com.denizd.qwark.model.FinalGrade
import com.denizd.qwark.model.SchoolYear
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.math.BigDecimal
import java.math.RoundingMode

internal class FinalsLinkBottomSheet : BottomSheetDialogFragment(), YearAdapter.YearClickListener, CourseAdapter.CourseClickListener {

    private lateinit var finalGrade: FinalGrade
    private lateinit var binding: RecyclerDialogBinding
    private var advanced = false
    private var isSelectingCourse = false
    private lateinit var finalsTargetFragment: FinalsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finalGrade = QwarkUtil.getFinalGradeFromBundle(arguments)
        advanced = finalGrade.type == "adv"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = RecyclerDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        finalsTargetFragment = targetFragment as FinalsFragment
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
        finalsTargetFragment.update(newFinalGrade)
        dismiss()
    }

    override fun onCourseLongClick(course: CourseExam) {}

    private fun fillWithYears() {
        binding.title.text = getString(R.string.pick_a_year)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = YearAdapter(finalsTargetFragment.getAllYears(), this@FinalsLinkBottomSheet)
            scheduleLayoutAnimation()
        }
        isSelectingCourse = false
    }

    private fun fillWithCourses(schoolYear: SchoolYear) {
        binding.title.text = getString(R.string.pick_a_course)
        binding.recyclerView.adapter =
            CourseAdapter(
                QwarkUtil.getCoursesSorted(
                    finalsTargetFragment.getAllCourses().filter { c -> c.yearId == schoolYear.yearId && c.advanced == advanced },
                    finalsTargetFragment.getCourseSortType()
                ),
                this,
                finalsTargetFragment.getGradeType()
        )
        binding.recyclerView.scheduleLayoutAnimation()
        isSelectingCourse = true
    }
}