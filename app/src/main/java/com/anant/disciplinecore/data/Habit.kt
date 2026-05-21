package com.anant.disciplinecore.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val emoji: String = "⚡",
    val category: String = "General",   // ← new field
    val streak: Int = 0,
    val longestStreak: Int = 0,
    val totalCompletions: Int = 0,
    val lastCompletedDate: String = "",
    val isCompletedToday: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)