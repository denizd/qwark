package com.denizd.qwark.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.denizd.qwark.database.NoteRepository
import com.denizd.qwark.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class NoteViewModel(application: Application) : QwarkViewModel(application) {

    private val repo = NoteRepository(application)
    private val reloadTrigger = MutableLiveData<String>()
    val allNotes: LiveData<List<Note>>? = Transformations.switchMap(reloadTrigger) { category ->
        if (category == "") {
            repo.allNotes
        } else {
            repo.getNotesForCategory(category)
        }
    }

    init {
        refresh("")
    }

    fun refresh(category: String) {
        reloadTrigger.value = category
    }

    fun insert(note: Note) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.insert(note)
            }
        }
    }

    fun updateDismissedStatus(newDismissedStatus: Boolean, noteId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.updateDismissed(newDismissedStatus, noteId)
            }
        }
    }

    fun delete(noteId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.delete(noteId)
            }
        }
    }

//    // TODO get this as LiveData variable to get changes
//    fun getNotesForCategory(category: String): LiveData<List<Note>>? {
//        return runBlocking {
//            withContext(Dispatchers.IO) {
//                repo.notesForCategory(category)
//            }
//        }
//    }

    fun update(note: Note) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.update(note)
            }
        }
    }

    fun getAllCategories(): List<String> = runBlocking {
        withContext(Dispatchers.IO) {
            repo.getAllCategories()
        }
    }

    fun getNote(noteId: Int) = returnBlocking { repo.getNote(noteId) }
}