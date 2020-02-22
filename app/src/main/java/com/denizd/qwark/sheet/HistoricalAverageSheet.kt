package com.denizd.qwark.sheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.denizd.qwark.R
import com.denizd.qwark.adapter.HistoricalAverageAdapter
import com.denizd.qwark.databinding.RecyclerDialogBinding
import com.denizd.qwark.fragment.CourseFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HistoricalAverageSheet : BottomSheetDialogFragment() {

    private var _binding: RecyclerDialogBinding? = null
    private val binding: RecyclerDialogBinding get() = _binding!!
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = RecyclerDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val targetFragment = targetFragment as CourseFragment
        val data = targetFragment.getAveragesForCourse(arguments?.getInt("courseId") ?: -1)

        binding.title.text = getString(R.string.averages_for_course, arguments?.getString("courseName"))
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = HistoricalAverageAdapter(data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}