package com.denizd.qwark.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "school_day",
    foreignKeys = [
        ForeignKey(
            entity = SchoolYear::class,
            parentColumns = ["year_id"],
            childColumns = ["year_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SchoolDay(
    val day: String,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "day_id") val dayId: Int,
    @ColumnInfo(name = "year_id") val yearId: Int
)