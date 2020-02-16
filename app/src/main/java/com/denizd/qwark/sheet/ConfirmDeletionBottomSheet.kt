package com.denizd.qwark.sheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.denizd.qwark.databinding.ConfirmationDialogBinding
import com.denizd.qwark.viewmodel.ConfirmDeletionViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ConfirmDeletionBottomSheet() : BottomSheetDialogFragment() {

    constructor(text: String, confirmedAction: () -> Unit) : this() {
        this.text = text
        this.confirmedAction = confirmedAction
    }

    private var _binding: ConfirmationDialogBinding? = null
    private val binding: ConfirmationDialogBinding get() = _binding!!
    private lateinit var mContext: Context

    private lateinit var text: String
    private lateinit var confirmedAction: () -> Unit

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ConfirmationDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this)[ConfirmDeletionViewModel::class.java]

        if (::text.isInitialized) {
            viewModel.textBody = text
            viewModel.confirmedFunction = confirmedAction
        }

        binding.content.text = viewModel.textBody
        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.confirmButton.setOnClickListener {
            viewModel.confirmedFunction()
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}