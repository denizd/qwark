package com.denizd.qwark.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "participation",
    foreignKeys = [
        ForeignKey(
            entity = Course::class,
            parentColumns = ["course_id"],
            childColumns = ["course_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SchoolDay::class,
            parentColumns = ["day_id"],
            childColumns = ["day_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Participation(
    @ColumnInfo(name = "times_hand_raised") val timesHandRaised: Int,
    @ColumnInfo(name = "times_spoken") val timesSpoken: Int,
    val time: Long,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "participation_id") val participationId: Int,
    @ColumnInfo(name = "course_id") val courseId: Int,
    @ColumnInfo(name = "day_id") val dayId: Int
)