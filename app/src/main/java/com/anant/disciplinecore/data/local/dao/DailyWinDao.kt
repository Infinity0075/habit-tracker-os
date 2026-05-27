package com.anant.disciplinecore.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.anant.disciplinecore.data.local.entities.DailyWin

@Dao
interface DailyWinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(win: DailyWin)

    @Delete
    suspend fun delete(win: DailyWin)

    @Query("SELECT * FROM daily_wins ORDER BY timestamp DESC")
    fun getAllWins(): LiveData<List<DailyWin>>
}