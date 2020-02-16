package com.denizd.qwark.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class Note(
    val content: String,
    val dismissed: Boolean,
    val time: Long,
    val category: String,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "note_id") val noteId: Int
)