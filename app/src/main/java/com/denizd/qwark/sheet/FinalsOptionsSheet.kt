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

class FinalsOptionsSheet : BottomSheetDialogFragment() {

    private var _binding: FinalGradeOptionsDialogBinding? = null
    private val binding: FinalGradeOptionsDialogBinding get() = _binding!!
    private lateinit var mContext: Context
    private lateinit var finalGrade: FinalGrade
    private lateinit var finalsFragment: FinalsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finalsFragment = targetFragment as FinalsFragment
        finalGrade = finalsFragment.getFinalGrade(arguments?.getInt("finalGradeId") ?: -1)
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

        binding.title.text = getString(R.string.options_for_final_grade, if (finalGrade.name.isEmpty()) getString(R.string.final_grade) else finalGrade.name)

        if (finalGrade.type == "exam") binding.linkGradeButton.visibility = View.GONE
        if (finalGrade.courseId == -1) binding.viewLinkedCourseButton.visibility = View.GONE
        binding.editGradeButton.setOnClickListener {
            finalsFragment.createEditGradeSheet(finalGrade)
            dismiss()
        }
        binding.linkGradeButton.setOnClickListener {
            finalsFragment.createLinkGradeSheet(finalGrade)
            dismiss()
        }
        binding.viewLinkedCourseButton.setOnClickListener {
            finalsFragment.viewLinkedCourse(finalGrade.courseId)
            dismiss()
        }
        binding.clearGradeButton.setOnClickListener {
            if (finalGrade.grade == "-1") {
                finalsFragment.clearFinalGradeError()
            } else {
                finalsFragment.clearFinalGrade(finalGrade)
            }
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}