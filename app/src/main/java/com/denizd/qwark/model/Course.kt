package com.denizd.qwark.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Data class intended to be used only as an entity for room. For access, use CourseExam class
 */
@Entity(tableName = "course")
@ForeignKey(
    entity = SchoolYear::class,
    parentColumns = ["key"], // this should be "year_id", but I'm too afraid to change it, since it may break the app
    childColumns = ["year_id"],
    onDelete = ForeignKey.CASCADE
)
data class Course(
    val name: String,
    val advanced: Boolean,
    val icon: String,
    val colour: String,
    val average: String,
    @ColumnInfo(name = "oral_weighting") val oralWeighting: Int,
    @ColumnInfo(name = "grade_count") val gradeCount: Int,
    val participation: String = "--|--",
    val time: Long,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "course_id") val courseId: Int,
    @ColumnInfo(name = "year_id") val yearId: Int
)