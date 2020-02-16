package com.denizd.qwark.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.denizd.qwark.R
import com.denizd.qwark.adapter.CourseAdapter
import com.denizd.qwark.databinding.RecyclerDialogBinding
import com.denizd.qwark.fragment.ParticipationFragment
import com.denizd.qwark.model.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

internal class ParticipationLinkBottomSheet : BottomSheetDialogFragment(), CourseAdapter.CourseClickListener {

    private lateinit var finalGrade: FinalGrade
    private lateinit var binding: RecyclerDialogBinding
    private lateinit var participationFragment: ParticipationFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = RecyclerDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        participationFragment = targetFragment as ParticipationFragment
        fillWithCourses()
    }

    override fun onCourseClick(course: CourseExam) {
        participationFragment.link(course.courseId)
        dismiss()
    }

    override fun onCourseLongClick(course: CourseExam) {}

    private fun fillWithCourses() {
        binding.title.text = getString(R.string.pick_a_course)
        binding.recyclerView.apply {
            adapter =
                CourseAdapter(
                    participationFragment.getCourses().map { c ->
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
                    },
                    this@ParticipationLinkBottomSheet,
                    null
                )
            layoutManager = LinearLayoutManager(context)
            scheduleLayoutAnimation()
        }
    }
}