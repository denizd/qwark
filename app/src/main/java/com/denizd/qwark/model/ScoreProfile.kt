package com.denizd.qwark.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "score_profile")
data class ScoreProfile(
    val name: String,
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "score_profile_id") val scoreProfileId: Int
)