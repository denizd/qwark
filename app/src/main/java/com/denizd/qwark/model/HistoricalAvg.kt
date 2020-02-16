package com.denizd.qwark.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "historical_avg")
@ForeignKey(
    entity = Course::class,
    parentColumns = ["course_id"],
    childColumns = ["course_id"],
    onDelete = CASCADE)
data class HistoricalAvg(
    val average: String,
    val time: Long,
    @ColumnInfo(name = "course_id") val courseId: Int
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}