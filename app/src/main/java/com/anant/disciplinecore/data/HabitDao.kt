package com.anant.disciplinecore.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits ORDER BY createdAt ASC")
    fun getAllHabits(): LiveData<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    // Reset isCompletedToday for all habits where lastCompletedDate is not today
    @Query("UPDATE habits SET isCompletedToday = 0 WHERE lastCompletedDate != :today AND isCompletedToday = 1")
    suspend fun resetStaleCompletions(today: String)

    @Query("SELECT COUNT(*) FROM habits")
    suspend fun getTotalCount(): Int

    @Query("SELECT COUNT(*) FROM habits WHERE isCompletedToday = 1")
    suspend fun getCompletedTodayCount(): Int
}
