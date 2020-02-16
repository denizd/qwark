package com.denizd.qwark.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "school_year")
data class SchoolYear(
    val year: String,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "year_id") val yearId: Int
)