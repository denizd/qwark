package com.denizd.qwark.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "grade")
@ForeignKey(
    entity = Course::class,
    parentColumns = ["course_id"],
    childColumns = ["course_id"],
    onDelete = ForeignKey.CASCADE
)
data class Grade(
    val grade: Int,
    val note: String,
    val verbal: Boolean,
    val time: Long,
    val weighting: Int,
    @ColumnInfo(name = "exam_time") val examTime: Long = -1L,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "grade_id") val gradeId: Int,
    @ColumnInfo(name = "course_id") val courseId: Int
)
