package com.denizd.qwark.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "course_schoolday_relation",
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
data class CourseDayRelation(
    @ColumnInfo(name = "course_id") val courseId: Int,
    @ColumnInfo(name = "day_id") val dayId: Int,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "relation_id") val relationId: Int
)