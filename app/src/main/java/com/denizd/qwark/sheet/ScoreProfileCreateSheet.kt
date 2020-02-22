package com.denizd.qwark.sheet

import android.os.Bundle
import android.view.View
import com.denizd.qwark.R
import com.denizd.qwark.fragment.SettingsFragment

class ScoreProfileCreateSheet : TextInputSheet() {

    private lateinit var settingsFragment: SettingsFragment
    private var currentScoreProfile: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentScoreProfile = arguments?.getString("currentScoreProfile") ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsFragment = targetFragment as SettingsFragment

        binding.textInputLayout.hint = getString(R.string.score_profile)
        if (currentScoreProfile != "") {
            binding.title.text = getString(R.string.edit_score_profile)
            binding.createButton.text = getString(R.string.confirm)
            binding.editText.setText(currentScoreProfile)
        } else {
            binding.title.text = getString(R.string.add_score_profile)
        }
    }

    override fun positiveButtonClicked() {
        val name = binding.editText.text
        if (name.isNullOrBlank()) {
            binding.editText.error = getString(R.string.field_empty)
        } else {
            if (currentScoreProfile == "") {
                settingsFragment.initNewFinalScore(name.toString())
            } else {
                settingsFragment.updateScoreProfile(name.toString())
            }
        }
        super.positiveButtonClicked()
    }
}