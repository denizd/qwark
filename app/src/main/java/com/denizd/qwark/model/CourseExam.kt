package com.denizd.qwark.model

import androidx.room.ColumnInfo

data class CourseExam(
    val name: String,
    val advanced: Boolean,
    val icon: String,
    val colour: String,
    val average: String,
    @ColumnInfo(name = "oral_weighting") val oralWeighting: Int,
    @ColumnInfo(name = "grade_count") val gradeCount: Int,
    val participation: String = "--|--",
    val time: Long,
    @ColumnInfo(name = "course_id") val courseId: Int,
    @ColumnInfo(name = "year_id") val yearId: Int,

    @ColumnInfo(name = "exam_time") val examTime: Long = -1L
)