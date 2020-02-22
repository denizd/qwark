package com.denizd.qwark.sheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.denizd.qwark.R
import com.denizd.qwark.databinding.NoteOptionsDialogBinding
import com.denizd.qwark.fragment.NoteFragment
import com.denizd.qwark.model.Note
import com.denizd.qwark.util.QwarkUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NoteOptionsSheet : BottomSheetDialogFragment() {

    private var _binding: NoteOptionsDialogBinding? = null
    private val binding: NoteOptionsDialogBinding get() = _binding!!
    private lateinit var mContext: Context
    private lateinit var note: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        note = (targetFragment as NoteFragment).getNote(arguments?.getInt("noteId") ?: -1)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = NoteOptionsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (note.dismissed) {
            binding.dismissedStatusText.text = getString(R.string.mark_note_undone)
            binding.dismissedImageView.setImageDrawable(mContext.getDrawable(R.drawable.label))
        }
        if (note.category == "") {
            binding.viewCategoryButton.visibility = View.GONE
        }
        binding.viewCategoryText.text = getString(R.string.view_notes_for_category, note.category)

        val noteFragment = targetFragment as NoteFragment

        binding.updateDismissedButton.setOnClickListener {
            dismiss()
            noteFragment.updateDismissed(note)
        }
        binding.viewCategoryButton.setOnClickListener {
            dismiss()
            noteFragment.sortByCategory(note.category)
        }
        binding.editNoteButton.setOnClickListener {
            dismiss()
            noteFragment.createNoteDialog(note)
        }
        binding.deleteNoteButton.setOnClickListener {
            dismiss()
            noteFragment.deleteNote(note.noteId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}