package com.anant.disciplinecore.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.anant.disciplinecore.data.local.dao.DailyWinDao
import com.anant.disciplinecore.data.local.dao.HabitDao
import com.anant.disciplinecore.data.local.dao.HabitLogDao
import com.anant.disciplinecore.data.local.dao.UserProgressDao
import com.anant.disciplinecore.data.local.entities.DailyWin
import com.anant.disciplinecore.data.local.entities.Habit
import com.anant.disciplinecore.data.local.entities.HabitLog
import com.anant.disciplinecore.data.local.entities.UserProgress

@Database(
    entities = [
        Habit::class,
        HabitLog::class,
        DailyWin::class,
        UserProgress::class
    ],
    version = 6,
    exportSchema = false
)

abstract class HabitDatabase : RoomDatabase() {

    // ------------------------------------------------
    // DAOS
    // ------------------------------------------------

    abstract fun habitDao(): HabitDao

    abstract fun habitLogDao(): HabitLogDao

    abstract fun dailyWinDao(): DailyWinDao

    abstract fun userProgressDao(): UserProgressDao

    companion object {

        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getDatabase(
            context: Context
        ): HabitDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        HabitDatabase::class.java,
                        "discipline_core_db"
                    )

                        // =============================================
                        // SAFE FOR DEVELOPMENT
                        // =============================================

                        .fallbackToDestructiveMigration()

                        .build()

                INSTANCE = instance

                instance
            }
        }
    }
}