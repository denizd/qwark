package com.denizd.qwark.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.denizd.qwark.model.Note

class NoteViewModel(application: Application) : QwarkViewModel(application) {

    private val reloadTrigger = MutableLiveData<String>()
    val allNotes: LiveData<List<Note>> = Transformations.switchMap(reloadTrigger) { category ->
        if (category == "") {
            repo.allNotes
        } else {
            repo.getNotesForCategory(category)
        }
    }

    init { refresh("") }

    fun refresh(category: String) { reloadTrigger.value = category }

    fun insert(note: Note) = doAsync { repo.insert(note) }

    fun updateDismissedStatus(newDismissedStatus: Boolean, noteId: Int) = doAsync { repo.updateDismissed(newDismissedStatus, noteId) }

    fun delete(noteId: Int) = doAsync { repo.deleteNote(noteId) }

    fun update(note: Note) = doAsync { repo.update(note) }

    fun getAllCategories(): List<String> = returnBlocking { repo.getAllCategories() }

    fun getNote(noteId: Int) = returnBlocking { repo.getNote(noteId) }
}