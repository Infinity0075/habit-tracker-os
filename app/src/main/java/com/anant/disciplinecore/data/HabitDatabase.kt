package com.anant.disciplinecore.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Habit::class,
        HabitLog::class,
        DailyWin::class
    ],

    // IMPORTANT:
    // Increased because Habit entity changed
    version = 5,

    exportSchema = false
)
abstract class HabitDatabase : RoomDatabase() {

    // ------------------------------------------------
    // DAOS
    // ------------------------------------------------

    abstract fun habitDao(): HabitDao

    abstract fun habitLogDao(): HabitLogDao

    abstract fun dailyWinDao(): DailyWinDao

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

                        // TEMPORARY:
                        // Safe during development
                        .fallbackToDestructiveMigration()

                        .build()

                INSTANCE = instance

                instance
            }
        }
    }
}