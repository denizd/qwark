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
import com.denizd.qwark.R
import com.denizd.qwark.adapter.NoteAdapter
import com.denizd.qwark.sheet.ConfirmDeletionBottomSheet
import com.denizd.qwark.sheet.NoteCreateBottomSheet
import com.denizd.qwark.sheet.NoteDisplayBottomSheet
import com.denizd.qwark.sheet.NoteOptionsBottomSheet
import com.denizd.qwark.util.OnBackPressed
import com.denizd.qwark.databinding.*
import com.denizd.qwark.model.Note
import com.denizd.qwark.util.QwarkUtil
import com.denizd.qwark.viewmodel.NoteViewModel
import com.google.android.material.appbar.AppBarLayout

class NoteFragment : QwarkFragment(), NoteAdapter.OnNoteClickListener, OnBackPressed {

    private lateinit var viewModel: NoteViewModel
    private val adapter = NoteAdapter(ArrayList(), this)

    private var scrollPosition: Parcelable? = null

    private lateinit var appBarLayout: AppBarLayout

    private var _binding: PaddedRecyclerViewBinding? = null
    private val binding: PaddedRecyclerViewBinding get() = _binding!!
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = PaddedRecyclerViewBinding.inflate(inflater, container, false)
        return binding.root
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

    override fun onResume() {
        super.onResume()
        binding.recyclerView.applyPadding(verticalPadding = 4)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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
        openBottomSheet(NoteDisplayBottomSheet().also { sheet ->
            QwarkUtil.putNoteInBundle(sheet, note)
        })
    }

    fun updateDismissed(note: Note) {
        saveScrollPosition()
        viewModel.updateDismissedStatus(!note.dismissed, note.noteId)
    }

    fun deleteNote(noteId: Int) {
        val deletionSheet = ConfirmDeletionBottomSheet(getString(R.string.confirm_note_deletion)) {
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
        openBottomSheet(NoteOptionsBottomSheet().also { sheet ->
            QwarkUtil.putNoteInBundle(sheet, note)
        })
    }

    fun createNoteDialog(note: Note? = null) {
        val createSheet = NoteCreateBottomSheet().also { sheet ->
            if (note != null) QwarkUtil.putNoteInBundle(sheet, note)
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
}