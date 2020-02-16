package com.denizd.qwark.database

import android.app.Application

import androidx.lifecycle.LiveData
import com.denizd.qwark.model.Note

class NoteRepository(app: Application) {

    private val dao: QwarkDao = QwarkDatabase.getInstance(app).dao()
    val allNotes: LiveData<List<Note>> = dao.allNotes

    fun getNotesForCategory(category: String): LiveData<List<Note>> = dao.notesForCategory(category)
    fun getAllCategories(): List<String> = dao.getAllCategories()
    fun insert(note: Note) { dao.insert(note) }
    fun update(note: Note) { dao.update(note) }
    fun updateDismissed(newDismissedStatus: Boolean, noteId: Int) { dao.updateDismissed(newDismissedStatus, noteId) }
    fun delete(noteId: Int) { dao.deleteNote(noteId) }
}