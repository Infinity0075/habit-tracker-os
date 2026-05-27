package com.anant.disciplinecore.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_wins")
data class DailyWin(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val text: String,

    val timestamp: Long = System.currentTimeMillis()
)