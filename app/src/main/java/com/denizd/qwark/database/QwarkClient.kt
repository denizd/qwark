package com.denizd.qwark.database

import android.content.Context
import com.denizd.qwark.model.Note
import java.io.OutputStreamWriter
import java.net.Socket

class QwarkClient(applicationContext: Context) {

    private val dao: QwarkDao = QwarkDatabase.getInstance(applicationContext).dao()

    fun send(finish: () -> Unit) {
        try {
            Socket("172.20.10.7", 22222).use { socket ->
                OutputStreamWriter(socket.getOutputStream(), "utf-8").use { writer ->
                    writer.write(dao.getNotesAsList().prepare())
                    writer.flush()
                }
            }
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun List<Note>.prepare(): String {
        var result = ""
        forEach { note ->
            result += "$${note.content}^${note.dismissed}^${note.time}^${note.category}^${note.noteId}|"
        }
        result += '\\'
        return result
    }
}