package com.anant.disciplinecore.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress(

    @PrimaryKey
    val id: Int = 1,

    // =================================================
    // XP
    // =================================================

    val xp: Int = 0,

    val level: Int = 1,

    val totalXpEarned: Int = 0,

    // =================================================
    // GLOBAL STREAK
    // =================================================

    val currentStreak: Int = 0,

    val bestStreak: Int = 0,

    // =================================================
    // STATS
    // =================================================

    val totalHabitsCompleted: Int = 0,

    val totalFocusMinutes: Int = 0,

    // =================================================
    // LAST ACTIVE
    // =================================================

    val lastActiveDate: String = ""
)