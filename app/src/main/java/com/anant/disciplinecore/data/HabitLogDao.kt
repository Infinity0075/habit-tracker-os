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

    // ── Added for reset all data ──────────────────────────────────
    @Query("DELETE FROM habit_logs")
    suspend fun deleteAllLogs()
}