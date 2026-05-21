package com.anant.disciplinecore.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HabitLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HabitLog)

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM habit_logs
            WHERE habitId = :habitId
            AND date = :date
        )
    """)
    suspend fun isHabitCompletedToday(
        habitId: Int,
        date: String
    ): Boolean

    @Query("""
        SELECT * FROM habit_logs
        WHERE habitId = :habitId
        ORDER BY date DESC
    """)
    suspend fun getLogsForHabit(
        habitId: Int
    ): List<HabitLog>

    // ── Get all logs for last 30 days ─────────────────────────────
    @Query("""
        SELECT * FROM habit_logs
        WHERE date >= :startDate
        ORDER BY date ASC
    """)
    suspend fun getLogsFrom(startDate: String): List<HabitLog>

    // ── Get distinct dates where ANY habit was completed ──────────
    @Query("""
        SELECT date, COUNT(*) as count
        FROM habit_logs
        WHERE date >= :startDate
        AND completed = 1
        GROUP BY date
        ORDER BY date ASC
    """)
    suspend fun getDailyCompletionCounts(startDate: String): List<DailyCount>

    // ── Delete all logs ───────────────────────────────────────────
    @Query("DELETE FROM habit_logs")
    suspend fun deleteAllLogs()
}

// ── Helper data class for grouped query ──────────────────────────
data class DailyCount(
    val date: String,
    val count: Int
)