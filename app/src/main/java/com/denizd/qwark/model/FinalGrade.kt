package com.denizd.qwark.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "final_grade")
@ForeignKey(
    entity = ScoreProfile::class,
    parentColumns = ["score_profile_id"],
    childColumns = ["score_profile_id"],
    onDelete = CASCADE
)
data class FinalGrade(
    val name: String,
    val grade: String,
    val type: String,
    val note: String,
    @ColumnInfo(name = "course_id") val courseId: Int,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "final_grade_id") val finalGradeId: Int,
    @ColumnInfo(name = "score_profile_id") val scoreProfileId: Int
)