package com.denizd.qwark.sheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.denizd.qwark.adapter.IconAdapter
import com.denizd.qwark.util.QwarkUtil
import com.denizd.qwark.databinding.IconDialogBinding
import com.denizd.qwark.model.CourseIcon
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class IconPickerBottomSheet : BottomSheetDialogFragment(),
    IconAdapter.IconClickListener {

    private var _binding: IconDialogBinding? = null
    private val binding: IconDialogBinding get() = _binding!!
    private lateinit var mContext: Context
    private var icon: String = ""
    private lateinit var iconAdapter: IconAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        icon = arguments?.getString("currentIcon") ?: ""
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = IconDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val targetFragment = targetFragment as CourseCreateBottomSheet
        val icons = QwarkUtil.drawablesList
        iconAdapter = IconAdapter(icons, this)

        if (icon != "") {
            iconAdapter.setSelectedItem(icons.indexOf(CourseIcon(icon, QwarkUtil.getDrawableIntForString(icon))))
        }

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(mContext, 4)
            adapter = iconAdapter
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
        binding.confirmButton.setOnClickListener {
            targetFragment.setIcon(icon)
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onIconClick(key: String, value: Int, position: Int) {
        icon = key
        iconAdapter.setSelectedItem(position)
    }
}