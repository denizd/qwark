package com.denizd.qwark.sheet

import android.os.Bundle
import android.view.View
import com.denizd.qwark.R
import com.denizd.qwark.fragment.SettingsFragment
import com.denizd.qwark.model.SchoolYear

class YearCreateSheet : TextInputSheet() {

    private lateinit var settingsFragment: SettingsFragment
    private var currentYear: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentYear = arguments?.getString("currentYear") ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsFragment = targetFragment as SettingsFragment

        binding.textInputLayout.hint = getString(R.string.school_year_cap)
        if (currentYear != "") {
            binding.title.text = getString(R.string.edit_school_year)
            binding.createButton.text = getString(R.string.confirm)
            binding.editText.setText(currentYear)
        } else {
            binding.title.text = getString(R.string.add_school_year)
        }
    }

    override fun positiveButtonClicked() {
        val name = binding.editText.text
        if (binding.editText.text.isNullOrBlank()) {
            binding.editText.error = getString(R.string.field_empty)
        } else {
            if (currentYear == "") {
                settingsFragment.insert(SchoolYear(name.toString(), 0))
            } else {
                settingsFragment.setSchoolYear(name.toString())
            }
        }
        super.positiveButtonClicked()
    }
}