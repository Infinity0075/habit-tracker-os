package com.anant.disciplinecore.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.anant.disciplinecore.data.local.entities.UserProgress

@Dao
interface UserProgressDao {

    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun getProgress(): LiveData<UserProgress>

    @Query("SELECT * FROM user_progress WHERE id = 1")
    suspend fun getProgressOnce(): UserProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: UserProgress)

    // =================================================
    // XP + LEVEL
    // =================================================

    @Query("""
        UPDATE user_progress
        SET xp = :xp,
            level = :level,
            totalXpEarned = :totalXpEarned
        WHERE id = 1
    """)
    suspend fun updateXpAndLevel(
        xp: Int,
        level: Int,
        totalXpEarned: Int
    )

    // =================================================
    // STREAK
    // =================================================

    @Query("""
        UPDATE user_progress
        SET currentStreak = :streak,
            bestStreak = :best,
            lastActiveDate = :date
        WHERE id = 1
    """)
    suspend fun updateStreak(
        streak: Int,
        best: Int,
        date: String
    )

    // =================================================
    // FOCUS MINUTES
    // =================================================

    @Query("""
        UPDATE user_progress
        SET totalFocusMinutes = totalFocusMinutes + :minutes
        WHERE id = 1
    """)
    suspend fun addFocusMinutes(
        minutes: Int
    )

    // =================================================
    // HABITS COMPLETED
    // =================================================

    @Query("""
        UPDATE user_progress
        SET totalHabitsCompleted =
            totalHabitsCompleted + 1
        WHERE id = 1
    """)
    suspend fun incrementHabitsCompleted()
}