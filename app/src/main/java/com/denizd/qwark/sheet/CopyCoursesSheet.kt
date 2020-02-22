package com.denizd.qwark.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.denizd.qwark.R
import com.denizd.qwark.adapter.YearAdapter
import com.denizd.qwark.databinding.RecyclerDialogBinding
import com.denizd.qwark.fragment.SettingsFragment
import com.denizd.qwark.model.SchoolYear
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CopyCoursesSheet : BottomSheetDialogFragment(), YearAdapter.YearClickListener {

    private var _binding: RecyclerDialogBinding? = null
    private val binding: RecyclerDialogBinding get() = _binding!!
    private lateinit var settingsTargetFragment: SettingsFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = RecyclerDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsTargetFragment = targetFragment as SettingsFragment
        fillWithYears()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun fillWithYears() {
        binding.title.text = getString(R.string.pick_a_year)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = YearAdapter((settingsTargetFragment.getSchoolYears() ?: ArrayList()).filter { y ->
                y.yearId != settingsTargetFragment.getCurrentSchoolYearId()
            }, this@CopyCoursesSheet)
            scheduleLayoutAnimation()
        }
    }

    override fun onYearClick(schoolYear: SchoolYear) {
        settingsTargetFragment.copyCourses(schoolYear.yearId)
        dismiss()
    }
}
