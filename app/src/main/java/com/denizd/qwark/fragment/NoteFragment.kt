package com.denizd.qwark.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.denizd.lawrence.util.viewBinding
import com.denizd.qwark.R
import com.denizd.qwark.adapter.NoteAdapter
import com.denizd.qwark.sheet.ConfirmDeletionSheet
import com.denizd.qwark.sheet.NoteCreateSheet
import com.denizd.qwark.sheet.NoteDisplaySheet
import com.denizd.qwark.sheet.NoteOptionsSheet
import com.denizd.qwark.util.OnBackPressed
import com.denizd.qwark.databinding.*
import com.denizd.qwark.model.Note
import com.denizd.qwark.viewmodel.NoteViewModel
import com.google.android.material.appbar.AppBarLayout

class NoteFragment : QwarkFragment(R.layout.padded_recycler_view), NoteAdapter.OnNoteClickListener, OnBackPressed {

    private lateinit var viewModel: NoteViewModel
    private val adapter = NoteAdapter(ArrayList(), this)

    private var scrollPosition: Parcelable? = null

    private lateinit var appBarLayout: AppBarLayout

    private val binding: PaddedRecyclerViewBinding by viewBinding(PaddedRecyclerViewBinding::bind)
    private var isInCategoryView = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]
        viewModel.allNotes?.observe(this@NoteFragment, Observer<List<Note>> { notes ->
            adapter.setNotes(notes)
            if (scrollPosition == null) {
                binding.recyclerView.scheduleLayoutAnimation()
            } else {
                restoreScrollPosition()
            }
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(getGridColumnCount(newConfig), StaggeredGridLayoutManager.VERTICAL)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appBarLayout = view.rootView.findViewById(R.id.app_bar_layout)
        superTitle = ""
        appBarTitle = getString(R.string.your_notes)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(getGridColumnCount(resources.configuration), StaggeredGridLayoutManager.VERTICAL)

        fab.apply {
            show()
            text = getString(R.string.add_note)
            setOnClickListener {
                createNoteDialog()
            }
        }
        binding.recyclerView.addFabScrollListener()
    }

    override fun onBackPressed(): Boolean {
        return if (isInCategoryView) {
            sortByCategory("")
            false
        } else {
            true
        }
    }

    fun sortByCategory(category: String) {
        appBarLayout.setExpanded(true)
        if (category.isEmpty()) {
            isInCategoryView = false
            superTitle = ""
        } else {
            isInCategoryView = true
            superTitle = getString(R.string.quotation_marks, category)
        }
        viewModel.refresh(category)
        scrollPosition = null
    }

    override fun onNoteClick(note: Note) {
        openBottomSheet(NoteDisplaySheet().also { sheet ->
            sheet.arguments = Bundle().apply { putInt("noteId", note.noteId) }
        })
    }

    fun updateDismissed(note: Note) {
        saveScrollPosition()
        viewModel.updateDismissedStatus(!note.dismissed, note.noteId)
    }

    fun deleteNote(noteId: Int) {
        val deletionSheet = ConfirmDeletionSheet(getString(R.string.confirm_note_deletion)) {
            scrollPosition = binding.recyclerView.layoutManager?.onSaveInstanceState()
            viewModel.delete(noteId)
            binding.recyclerView.scheduleLayoutAnimation()
        }
        openBottomSheet(deletionSheet)
    }

    override fun onNoteLongClick(note: Note) {
        createOptionsSheet(note)
    }

    override fun onCategoryChipClick(category: String) {
        sortByCategory(category)
    }

    private fun createOptionsSheet(note: Note) {
        openBottomSheet(NoteOptionsSheet().also { sheet ->
            sheet.arguments = Bundle().apply { putInt("noteId", note.noteId) }
        })
    }

    fun createNoteDialog(note: Note? = null) {
        val createSheet = NoteCreateSheet().also { sheet ->
            if (note != null) sheet.arguments = Bundle().apply { putInt("noteId", note.noteId) }
        }
        openBottomSheet(createSheet)
    }

    fun saveScrollPosition() {
        scrollPosition = binding.recyclerView.layoutManager?.onSaveInstanceState()
    }
    private fun restoreScrollPosition() {
        binding.recyclerView.layoutManager?.onRestoreInstanceState(scrollPosition)
    }

    override fun getGridColumnCount(config: Configuration): Int = super.getGridColumnCount(config) + 1

    fun insert(note: Note) { viewModel.insert(note) }
    fun update(note: Note) { viewModel.update(note) }
    fun getAllCategories(): List<String> = viewModel.getAllCategories()
    fun getNote(noteId: Int) = viewModel.getNote(noteId)
}