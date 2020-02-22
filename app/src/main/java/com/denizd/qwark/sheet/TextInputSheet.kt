package com.denizd.qwark.sheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.denizd.qwark.databinding.TextInputSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class TextInputSheet : BottomSheetDialogFragment() {

    protected var _binding: TextInputSheetBinding? = null
    protected val binding: TextInputSheetBinding get() = _binding!!
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = TextInputSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createButton.setOnClickListener { positiveButtonClicked() }
        binding.cancelButton.setOnClickListener { negativeButtonClicked() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    protected open fun positiveButtonClicked() { dismiss() }
    protected open fun negativeButtonClicked() { dismiss() }
}