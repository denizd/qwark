package com.denizd.qwark.sheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.denizd.qwark.R
import com.denizd.qwark.util.QwarkUtil
import com.denizd.qwark.databinding.NoteBottomSheetBinding
import com.denizd.qwark.model.Note
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

internal class NoteDisplayBottomSheet : BottomSheetDialogFragment() {

    private var _binding: NoteBottomSheetBinding? = null
    private val binding: NoteBottomSheetBinding get() = _binding!!
    private lateinit var mContext: Context
    private lateinit var note: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        note = QwarkUtil.getNoteFromBundle(arguments)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = NoteBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return super.onCreateDialog(savedInstanceState).also { dialog ->
//            dialog.setOnShowListener {
//                BottomSheetBehavior.from(dialog.findViewById<FrameLayout>(
//                    com.google.android.material.R.id.design_bottom_sheet
//                )).apply {
//                    state = BottomSheetBehavior.STATE_EXPANDED
//                }
//            }
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = QwarkUtil.getCreatedString(mContext, note.time)
        val noteText = if (note.dismissed) getString(R.string.note_dismissed) else "%s"
        binding.note.text = String.format(noteText, note.content)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}