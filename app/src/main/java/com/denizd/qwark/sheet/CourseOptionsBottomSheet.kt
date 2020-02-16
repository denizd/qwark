package com.denizd.qwark.sheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.denizd.qwark.R
import com.denizd.qwark.databinding.CourseOptionsDialogBinding
import com.denizd.qwark.fragment.CourseFragment
import com.denizd.qwark.model.Course
import com.denizd.qwark.model.CourseExam
import com.denizd.qwark.util.QwarkUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

internal class CourseOptionsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: CourseOptionsDialogBinding? = null
    private val binding: CourseOptionsDialogBinding get() = _binding!!
    private lateinit var mContext: Context
    private lateinit var course: CourseExam

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        course = QwarkUtil.getCourseFromBundle(arguments)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = CourseOptionsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val targetFragment = targetFragment as CourseFragment

        binding.title.text = getString(R.string.options_for_course, course.name)

        binding.viewHistoryButton.setOnClickListener {
            targetFragment.createHistoryDialog(course.courseId, course.name)
            dismiss()
        }
        binding.editCourseButton.setOnClickListener {
            targetFragment.createCourseDialog(course)
            dismiss()
        }
        binding.deleteCourseButton.setOnClickListener {
            targetFragment.deleteCourse(QwarkUtil.getCourseExamAsCourse(course))
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}