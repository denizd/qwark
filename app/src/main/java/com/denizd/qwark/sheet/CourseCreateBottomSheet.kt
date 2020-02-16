package com.denizd.qwark.sheet

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.denizd.qwark.R
import com.denizd.qwark.util.QwarkUtil
import com.denizd.qwark.databinding.CourseCreateDialogBinding
import com.denizd.qwark.fragment.CourseFragment
import com.denizd.qwark.model.Course
import com.denizd.qwark.model.CourseExam
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

internal class CourseCreateBottomSheet : BottomSheetDialogFragment() {
    
    private var _binding: CourseCreateDialogBinding? = null
    private val binding: CourseCreateDialogBinding get() = _binding!!
    private lateinit var mContext: Context
    private var icon = ""
    private lateinit var course: CourseExam

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments?.size() ?: 0 != 0) course = QwarkUtil.getCourseFromBundle(arguments)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = CourseCreateDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val targetFragment = targetFragment as CourseFragment

        binding.oralWeightingEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.writtenWeightingEditText.setText(if (binding.oralWeightingEditText.text.isNullOrBlank()) {
                    "100%"
                } else {
                    "${100 - binding.oralWeightingEditText.text.toString().toInt()}%"
                })
            }
        })
        if (::course.isInitialized) {
            binding.contentEditText.setText(course.name)
            binding.advancedCheckBox.isChecked = course.advanced
            binding.oralWeightingEditText.setText("${course.oralWeighting}")
            icon = if (course.icon == "") "circle" else course.icon
            binding.title.text = getString(R.string.edit_course)
            binding.createButton.text = getString(R.string.confirm)
        } else {
            icon = "circle"
            binding.oralWeightingEditText.setText(60.toString())
        }
        binding.imageView.setImageResource(QwarkUtil.getDrawableIntForString(icon))

        binding.imageButton.setOnClickListener {
            openIconBottomSheet()
        }
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
        binding.createButton.setOnClickListener {
            val verbal = binding.oralWeightingEditText.text
            when {
                binding.contentEditText.text.toString().isEmpty() -> binding.contentEditText.error = getString(R.string.field_empty)
                !verbal.isNullOrBlank() && verbal.toString().toInt() > 100 -> binding.oralWeightingEditText.error = getString(R.string.weighting_above_hundred)
                else -> {
                    if (::course.isInitialized) {
                        targetFragment.update(
                            Course(
                                name = binding.contentEditText.text.toString(),
                                advanced = binding.advancedCheckBox.isChecked,
                                icon = icon,
                                colour = "",
                                average = course.average,
                                oralWeighting = if (verbal.isNullOrBlank()) 0 else verbal.toString().toInt(),
                                gradeCount = course.gradeCount,
                                time = course.time,
                                courseId = course.courseId,
                                yearId = targetFragment.getSchoolYear()
                            )
                        )
                    } else {
                        targetFragment.insert(
                            Course(
                                name = binding.contentEditText.text.toString(),
                                advanced = binding.advancedCheckBox.isChecked,
                                icon = icon,
                                colour = "",
                                average = if (targetFragment.getGradeType() == QwarkUtil.GRADE_PRECISE) "0.00" else "0",
                                oralWeighting = if (verbal.isNullOrBlank()) 0 else verbal.toString().toInt(),
                                gradeCount = 0,
                                time = System.currentTimeMillis(),
                                courseId = 0,
                                yearId = targetFragment.getSchoolYear()
                            )
                        )
                    }
                    dismiss()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun openIconBottomSheet() = fragmentManager?.let { fm ->
        val sheet = IconPickerBottomSheet().apply {
            arguments = Bundle(1).apply {
                putString("currentIcon", icon)
            }
        }
        sheet.setTargetFragment(this, 42)
        sheet.show(fm, sheet.tag)
    }

    internal fun setIcon(icon: String) {
        binding.imageView.setImageResource(QwarkUtil.getDrawableIntForString(icon))
        this.icon = icon
    }
}