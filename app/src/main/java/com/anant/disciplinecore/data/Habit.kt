package com.anant.disciplinecore.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // ------------------------------------------------
    // BASIC INFO
    // ------------------------------------------------

    val name: String,

    val emoji: String = "⚡",

    val category: String = "General",

    // ------------------------------------------------
    // STREAKS
    // ------------------------------------------------

    val streak: Int = 0,

    val longestStreak: Int = 0,

    // ------------------------------------------------
    // COMPLETION
    // ------------------------------------------------

    val totalCompletions: Int = 0,

    val isCompletedToday: Boolean = false,

    val lastCompletedDate: String = "",

    // ------------------------------------------------
    // ANALYTICS
    // ------------------------------------------------

    // Total days habit existed
    val daysTracked: Int = 0,

    // Successful completion rate
    // Example: 82 means 82%
    val consistencyScore: Int = 0,

    // Last 7 days activity pattern
    // Example: "1101011"
    val weeklyPattern: String = "",

    // ------------------------------------------------
    // METADATA
    // ------------------------------------------------

    val createdAt: Long = System.currentTimeMillis(),

    // Optional color theme for future category colors
    val accentColor: String = "#FFB020",

    // Optional notes/description
    val notes: String = ""
)