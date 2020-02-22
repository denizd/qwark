package com.denizd.qwark.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.denizd.qwark.R
import com.denizd.qwark.sheet.ConfirmDeletionSheet
import com.denizd.qwark.sheet.CopyCoursesSheet
import com.denizd.qwark.sheet.ScoreProfileCreateSheet
import com.denizd.qwark.sheet.YearCreateSheet
import com.denizd.qwark.databinding.SettingsFragmentBinding
import com.denizd.qwark.model.SchoolYear
import com.denizd.qwark.viewmodel.SettingsViewModel
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : QwarkFragment() {

    private var _binding: SettingsFragmentBinding? = null
    private val binding: SettingsFragmentBinding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        superTitle = ""
        appBarTitle = getString(R.string.settings)
        fab.hide()

        binding.darkModeDropdown.apply {
            setAdapter(
                ArrayAdapter.createFromResource(
                    context,
                    R.array.dark_mode_options,
                    R.layout.dropdown_item
                )
            )
            binding.darkModeDropdown.setText(
                binding.darkModeDropdown.adapter.getItem(viewModel.getAppTheme()).toString(),
                false
            )
            binding.darkModeDropdown.setOnItemClickListener { _, _, position, _ ->
                viewModel.setAppTheme(position)
            }
        }

        binding.showGradeAverageSwitch.apply {
            isChecked = viewModel.getShowGradeAverage()
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.setShowGradeAverage(isChecked)
            }
        }

        binding.courseSortTypeDropdown.apply {
            setAdapter(ArrayAdapter.createFromResource(context, R.array.course_sort_type_options, R.layout.dropdown_item))
            setText(binding.courseSortTypeDropdown.adapter.getItem(viewModel.getCourseSortType()).toString(), false)
            setOnItemClickListener { _, _, position, _ ->
                viewModel.setCourseSortType(position)
            }
        }

        binding.gradeTypeDropdown.apply {
            setAdapter(
                ArrayAdapter.createFromResource(
                    context,
                    R.array.grade_type_array,
                    R.layout.dropdown_item
                )
            )
            setText(
                adapter.getItem(viewModel.getGradeType()).toString(),
                false
            )
            setOnItemClickListener { _, _, position, _ ->
                viewModel.setGradeType(position)
            }
        }

        viewModel.allYears.observe(viewLifecycleOwner, Observer { years ->
            binding.schoolYearDropdown.apply {
                setAdapter(
                    ArrayAdapter<String>(
                        context,
                        R.layout.dropdown_item,
                        years.map { year -> year.year }
                    )
                )
                try {
                    setText(viewModel.getCurrentSchoolYear(), false)
                } catch (e: IndexOutOfBoundsException) {
                    // do nothing if no school years exist or none has been selected
                }
                setOnItemClickListener { _, _, position, _ ->
                    viewModel.updateSchoolYear(years[position])
                }
            }
        })

        binding.addYearButton.setOnClickListener {
            createSchoolYearSheet()
        }
        binding.editYearButton.setOnClickListener {
            createSchoolYearSheet(edit = true) // TODO prohibit opening when text field is empty
        }
        binding.deleteYearButton.setOnClickListener {
            if (binding.schoolYearDropdown.text.isNullOrBlank()) {
                presentErrorSnackBar(getString(R.string.no_school_year_selected))
            } else {
                val deleteYearSheet = ConfirmDeletionSheet(getString(R.string.delete_school_year_desc, binding.schoolYearDropdown.text.toString())) {
                    viewModel.deleteSchoolYear()
                    binding.schoolYearDropdown.setText("")
                }
                openBottomSheet(deleteYearSheet)
            }
        }
        binding.copyCoursesButton.setOnClickListener {
            openBottomSheet(CopyCoursesSheet())
        }
//        binding.copyCoursesButton.setOnLongClickListener {
//            // TODO this is currently an experiment
//            fun makeSnackbar(text: String) {
//                Snackbar.make(snackBarContainer, text, Snackbar.LENGTH_LONG).show()
//            }
//            makeSnackbar("Sending")
//            viewModel.send {
//                makeSnackbar("Sent all notes successfully!")
//            }
//            true
//        }

        binding.participationCountDropdown.apply {
            setAdapter(
                ArrayAdapter.createFromResource(
                    context,
                    R.array.participation_display_array,
                    R.layout.dropdown_item
                )
            )
            setText(
                adapter.getItem(viewModel.getParticipationDisplay()).toString(),
                false
            )
            setOnItemClickListener { _, _, position, _ ->
                viewModel.setParticipationDisplay(position)
            }
        }

        binding.participationDayDropdown.apply {
            setAdapter(
                ArrayAdapter.createFromResource(
                    context,
                    R.array.participation_day_array,
                    R.layout.dropdown_item
                )
            )
            setText(
                adapter.getItem(viewModel.getParticipationDay()).toString(),
                false
            )
            setOnItemClickListener { _, _, position, _ ->
                viewModel.setParticipationDay(position)
            }
        }

        viewModel.allScoreProfiles.observe(viewLifecycleOwner, Observer { scoreProfiles ->
            binding.scoreProfileDropdown.apply {
                setAdapter(
                    ArrayAdapter<String>(
                        context,
                        R.layout.dropdown_item,
                        scoreProfiles.map { s -> s.name }
                    )
                )
                try {
                    setText(viewModel.getScoreProfileName(), false)
                } catch (e: IndexOutOfBoundsException) {
                    // do nothing if no score profiles exist or none has been selected
                }
                setOnItemClickListener { _, _, position, _ ->
                    viewModel.setCurrentScoreProfile(scoreProfiles[position])
                }
            }
        })

        binding.addScoreProfileButton.setOnClickListener {
            createScoreProfileSheet()
        }
        binding.editScoreProfileButton.setOnClickListener {
            createScoreProfileSheet(edit = true)
        }
        binding.deleteScoreProfileButton.setOnClickListener {
            if (binding.scoreProfileDropdown.text.isNullOrBlank()) {
                presentErrorSnackBar(getString(R.string.no_score_profile_selected))
            } else {
                val deleteScoreProfileSheet = ConfirmDeletionSheet(
                    getString(R.string.delete_score_profile_desc,
                        binding.scoreProfileDropdown.text.toString())
                ) {
                    viewModel.deleteScoreProfile()
                    binding.scoreProfileDropdown.setText("")
                }
                openBottomSheet(deleteScoreProfileSheet)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.settingsScrollView.applyPadding()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun copyCourses(yearId: Int) {
        viewModel.copyCourses(yearId)
        Snackbar
            .make(snackBarContainer, getString(R.string.copy_courses_success), Snackbar.LENGTH_LONG)
            .show()
    }

    fun getSchoolYears() = viewModel.allYears.value // TODO does this work on LiveData?
    fun getCurrentSchoolYearId() = viewModel.getCurrentSchoolYearId()

    private fun createSchoolYearSheet(edit: Boolean = false) {
        val currentYear = binding.schoolYearDropdown.text.toString()
        if (edit && currentYear == "") {
            presentErrorSnackBar(getString(R.string.no_school_year_selected))
        } else {
            val yearCreateSheet = YearCreateSheet().also { sheet ->
                sheet.arguments = Bundle(1).apply {
                    putString("currentYear", if (edit) currentYear else "")
                }
            }
            openBottomSheet(yearCreateSheet)
        }
    }

    private fun presentErrorSnackBar(message: String) {
        Snackbar
            .make(snackBarContainer, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(context.getColor(R.color.colorWarning))
            .show()
    }

    private fun createScoreProfileSheet(edit: Boolean = false) {
        val currentScoreProfile = binding.scoreProfileDropdown.text.toString()
        if (edit && currentScoreProfile == "") {
            presentErrorSnackBar(getString(R.string.no_score_profile_selected))
        } else {
            openBottomSheet(ScoreProfileCreateSheet().also { sheet ->
                sheet.arguments = Bundle(1).apply {
                    putString("currentScoreProfile", if (edit) currentScoreProfile else "")
                }
            })
        }
    }

    fun setSchoolYear(name: String) {
        viewModel.updateSchoolYear(name)
        binding.schoolYearDropdown.setText(name)
    }

    fun insert(schoolYear: SchoolYear) { viewModel.insert(schoolYear) }

    fun updateScoreProfile(name: String) {
        viewModel.updateScoreProfile(name)
        binding.scoreProfileDropdown.setText(name)
    }

    fun initNewFinalScore(name: String) {
        viewModel.initNewFinalScore(name)
    }
}