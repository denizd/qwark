package com.denizd.qwark.sheet

import android.animation.LayoutTransition
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.denizd.qwark.R
import com.denizd.qwark.util.QwarkUtil
import com.denizd.qwark.databinding.GradeCreateDialogBinding
import com.denizd.qwark.fragment.GradeFragment
import com.denizd.qwark.model.Grade
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.ParseException
import java.util.*

class GradeCreateSheet : BottomSheetDialogFragment() {

    private var _binding: GradeCreateDialogBinding? = null
    private val binding: GradeCreateDialogBinding get() = _binding!!
    private lateinit var mContext: Context
    private lateinit var grade: Grade
    private var courseId: Int = 0
    private lateinit var gradeFragment: GradeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gradeFragment = targetFragment as GradeFragment
        val gradeId = arguments?.getInt("gradeId") ?: -1
        if (gradeId == 0) { // 0 signifies no value has been set
            courseId = arguments?.getInt("courseId") ?: -1
        } else {
            grade = gradeFragment.getGrade(gradeId)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = GradeCreateDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LayoutTransition().apply {
            setAnimateParentHierarchy(false)
            binding.root.layoutTransition = this
        }

//        initGradeDropdown()

        binding.verbalSwitch.setOnCheckedChangeListener { switch, isChecked ->
            binding.verbalSwitchText.text = when (isChecked) {
                true -> getString(R.string.written)
                false -> getString(R.string.verbal)
            }
            if (!isChecked) {
                binding.examSwitch.isChecked = false
                switch.isChecked = false
            }
        }

        if (::grade.isInitialized) {
            with (binding) {
                title.text = getString(R.string.edit_grade)
                createButton.text = getString(R.string.confirm)
                gradePicker.value = if (grade.grade == -1) 0 else grade.grade
                weightingEditText.setText(grade.weighting.toString())
                noteEditText.setText(grade.note)
                verbalSwitch.isChecked = !grade.verbal
                examSwitch.isChecked = grade.examTime != -1L
                examEditText.setText(QwarkUtil.getSimpleDateFormat().format(Date(grade.examTime)))
                if (examSwitch.isChecked) examTextLayout.visibility = View.VISIBLE // also hide grade picker when applicable
            }
        }

        binding.gradePicker.displayedValues = QwarkUtil.getGradeArray(gradeFragment.getGradeType())

        binding.examSwitch.setOnCheckedChangeListener { _, isChecked ->
            setGradePickerVisibility(isChecked)
            binding.verbalSwitch.isChecked = true
            binding.examTextLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
//            if (!isChecked) initGradeDropdown()
        }

        binding.examEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setGradePickerVisibility(binding.examSwitch.isChecked)
            }
        })

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
        binding.createButton.setOnClickListener {
            when {
                binding.weightingEditText.text.isNullOrBlank() -> binding.weightingEditText.error =
                    getString(R.string.field_empty)
                binding.weightingEditText.text.toString().toInt() > 100 -> binding.weightingEditText.error =
                    getString(R.string.weighting_above_hundred)
                else -> {
                    try {
                        val examDate = if (binding.examSwitch.isChecked) parseExamDate() else 1L
                        val newGrade = Grade(
                            grade = if (binding.examSwitch.isChecked && examDate >= QwarkUtil.timeAtMidnight) -1 else binding.gradePicker.value,
                            verbal = !binding.verbalSwitch.isChecked,
                            weighting = binding.weightingEditText.text.toString().toInt(),
                            note = binding.noteEditText.text.toString(),
                            time = if (::grade.isInitialized) grade.time else System.currentTimeMillis(),
                            gradeId = if (::grade.isInitialized) grade.gradeId else 0,
                            courseId = if (::grade.isInitialized) grade.courseId else courseId,
                            examTime = if (binding.examSwitch.isChecked) examDate else -1L
                        )
                        if (::grade.isInitialized) {
                            gradeFragment.update(newGrade)
                        } else {
                            gradeFragment.insert(newGrade)
                        }
                        dismiss()
                    } catch (e: ParseException) {
                        binding.examEditText.error = getString(R.string.date_format_issue)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

//    private fun initGradeDropdown() {
//        binding.gradePicker.apply {
//            setAdapter(
//                ArrayAdapter.createFromResource(
//                    mContext,
//                    R.array.grades_points,
//                    R.layout.dropdown_item
//                ).also {
//                    it.setDropDownViewResource(R.layout.dropdown_item)
//                }
//            ) // TODO replace with NumberPicker or custom solution
//            setText(binding.gradePicker.adapter.getItem(0).toString(), false)
//        }
//    }

    @Throws(ParseException::class)
    private fun parseExamDate(): Long = QwarkUtil.getSimpleDateFormat().parse(binding.examEditText.text.toString())?.time ?: 0L

//    @Throws(NumberFormatException::class)
//    private fun getGrade(): Int = binding.gradePicker.text.toString().toInt()

    private fun setGradePickerVisibility(isExamGrade: Boolean) {
        if (binding.examEditText.text.toString().length == 10) {
            binding.gradePicker.visibility = try {
                if (isExamGrade && parseExamDate() >= QwarkUtil.timeAtMidnight) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            } catch (e: ParseException) {
                View.VISIBLE
            }
        }
        binding.examEditText.requestFocus()
    }
}