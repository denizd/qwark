package com.denizd.qwark.sheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.denizd.qwark.R
import com.denizd.qwark.databinding.TextSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FinalGradeTableSheet : BottomSheetDialogFragment() {

    private var _binding: TextSheetBinding? = null
    private val binding: TextSheetBinding get() = _binding!!
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = TextSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text = mContext.getString(R.string.points_to_grade_conversion_title)
        binding.content.text = mContext.getString(R.string.points_to_grade_conversion)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}