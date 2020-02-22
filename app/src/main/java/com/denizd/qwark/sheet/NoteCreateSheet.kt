package com.denizd.qwark.sheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.denizd.qwark.R
import com.denizd.qwark.databinding.NoteCreateDialogBinding
import com.denizd.qwark.fragment.NoteFragment
import com.denizd.qwark.model.Note
import com.denizd.qwark.util.QwarkUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NoteCreateSheet : BottomSheetDialogFragment() {

    private var _binding: NoteCreateDialogBinding? = null
    private val binding: NoteCreateDialogBinding get() = _binding!!
    private lateinit var mContext: Context
    private lateinit var note: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments?.size() ?: 0 != 0) note = (targetFragment as NoteFragment).getNote(arguments?.getInt("noteId") ?: -1)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = NoteCreateDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val targetFragment = targetFragment as NoteFragment

        binding.categoryDropDown.setAdapter(
            ArrayAdapter<String>(mContext, R.layout.dropdown_item, targetFragment.getAllCategories())
        )

        if (::note.isInitialized) {
            binding.title.text = getString(R.string.edit_note)
            binding.contentEditText.setText(note.content)
            binding.categoryDropDown.setText(note.category, false)
            binding.createButton.text = getString(R.string.confirm)
        }

        binding.createButton.setOnClickListener {
            if (binding.contentEditText.text.isNullOrBlank()) {
                binding.contentEditText.error = getString(R.string.field_empty)
            } else {
                if (::note.isInitialized) {
                    targetFragment.saveScrollPosition()
                    targetFragment.update(
                        Note(
                            content = binding.contentEditText.text.toString(),
                            dismissed = note.dismissed,
                            time = note.time,
                            category = binding.categoryDropDown.text.toString().trim(),
                            noteId = note.noteId
                        )
                    )
                } else {
                    targetFragment.insert(
                        Note(
                            content = binding.contentEditText.text.toString(),
                            dismissed = false,
                            time = System.currentTimeMillis(),
                            category = binding.categoryDropDown.text.toString().trim(),
                            noteId = 0
                        )
                    )
                }
                dismiss()
            }
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}