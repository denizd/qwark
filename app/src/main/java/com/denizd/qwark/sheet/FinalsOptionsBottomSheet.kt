package com.denizd.qwark.sheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.denizd.qwark.R
import com.denizd.qwark.databinding.FinalGradeOptionsDialogBinding
import com.denizd.qwark.fragment.FinalsFragment
import com.denizd.qwark.model.FinalGrade
import com.denizd.qwark.util.QwarkUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

internal class FinalsOptionsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FinalGradeOptionsDialogBinding? = null
    private val binding: FinalGradeOptionsDialogBinding get() = _binding!!
    private lateinit var mContext: Context
    private lateinit var finalGrade: FinalGrade

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finalGrade = QwarkUtil.getFinalGradeFromBundle(arguments)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FinalGradeOptionsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val targetFragment = targetFragment as FinalsFragment

        binding.title.text = getString(R.string.options_for_final_grade, if (finalGrade.name.isEmpty()) getString(R.string.final_grade) else finalGrade.name)

        if (finalGrade.type == "exam") binding.linkGradeButton.visibility = View.GONE
        if (finalGrade.courseId == -1) binding.viewLinkedCourseButton.visibility = View.GONE
        binding.editGradeButton.setOnClickListener {
            targetFragment.createEditGradeSheet(finalGrade)
            dismiss()
        }
        binding.linkGradeButton.setOnClickListener {
            targetFragment.createLinkGradeSheet(finalGrade)
            dismiss()
        }
        binding.viewLinkedCourseButton.setOnClickListener {
            targetFragment.viewLinkedCourse(finalGrade.courseId)
            dismiss()
        }
        binding.clearGradeButton.setOnClickListener {
            if (finalGrade.grade == "-1") {
                targetFragment.clearFinalGradeError()
            } else {
                targetFragment.clearFinalGrade(finalGrade)
            }
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}