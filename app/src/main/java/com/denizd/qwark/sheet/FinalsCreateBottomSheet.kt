package com.denizd.qwark.sheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.denizd.qwark.R
import com.denizd.qwark.util.QwarkUtil
import com.denizd.qwark.databinding.FinalGradeEditDialogBinding
import com.denizd.qwark.fragment.FinalsFragment
import com.denizd.qwark.model.FinalGrade
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

internal class FinalsCreateBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FinalGradeEditDialogBinding? = null
    private val binding: FinalGradeEditDialogBinding get() = _binding!!
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
        _binding = FinalGradeEditDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val targetFragment = targetFragment as FinalsFragment

        binding.courseEdittext.setText(finalGrade.name)

//        val gradeArray = ArrayAdapter.createFromResource(mContext, R.array.grades_points, R.layout.dropdown_item)
//        binding.gradePicker.setAdapter(gradeArray)
//        binding.gradePicker.setText(
//            if (finalGrade.grade == "-1") {
//                binding.gradePicker.adapter.getItem(0).toString()
//            } else {
//                QwarkUtil.getGradeWithLeadingZero(finalGrade.grade.toInt())
//            },
//            false
//        )
        binding.gradePicker.value = if (finalGrade.grade == "-1") 0 else finalGrade.grade.toInt()

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
        binding.confirmButton.setOnClickListener {
            when {
                binding.courseEdittext.text.isNullOrBlank() -> binding.courseEdittext.error = getString(
                    R.string.field_empty)
                else -> {
                    val newFinalGrade = FinalGrade(
                        name = binding.courseEdittext.text.toString(),
                        grade = binding.gradePicker.value.toString(), // binding.gradePicker.text.toString().toInt().toString(), // ugly workaround to remove the leading zero
                        type = finalGrade.type,
                        note = finalGrade.note,
                        courseId = -1,
                        finalGradeId = finalGrade.finalGradeId,
                        scoreProfileId = finalGrade.scoreProfileId
                    )
                    targetFragment.update(newFinalGrade)
                    dismiss()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}