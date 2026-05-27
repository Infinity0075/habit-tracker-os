package com.anant.disciplinecore.data.repository

import androidx.lifecycle.LiveData
import com.anant.disciplinecore.data.local.dao.UserProgressDao
import com.anant.disciplinecore.data.local.entities.UserProgress
import com.anant.disciplinecore.core.gamification.XPManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UserProgressRepository(private val dao: UserProgressDao) {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val progress: LiveData<UserProgress> = dao.getProgress()

    // ── XP ────────────────────────────────────────────────────────────────────

    suspend fun awardXp(amount: Int) {
        val current = getOrCreateProgress()
        val newTotal = current.totalXpEarned + amount
        val newLevel = XPManager.getLevelForXp(newTotal)
        dao.updateXpAndLevel(
            xp = newTotal, // Using totalXpEarned as xp for now if they are same
            level = newLevel,
            totalXpEarned = newTotal
        )
    }

    // Compatibility method for HomeViewModel
    suspend fun addXp(current: UserProgress?, amount: Int) {
        awardXp(amount)
    }

    suspend fun onHabitCompleted(allHabitsCompleted: Boolean, habitStreak: Int) {
        var xpToAward = XPManager.XP_HABIT_COMPLETE
        if (allHabitsCompleted) xpToAward += XPManager.XP_ALL_HABITS_COMPLETE
        xpToAward += XPManager.streakBonusXp(habitStreak)
        awardXp(xpToAward)
        updateGlobalStreak()
    }

    suspend fun onFocusSessionCompleted(
        minutes: Int
    ) {

        dao.addFocusMinutes(minutes)

        awardXp(
            XPManager.XP_FOCUS_SESSION
        )

        updateGlobalStreak()
    }
    suspend fun addFocusMinutes(
        minutes: Int
    ) {

        dao.addFocusMinutes(minutes)
    }

    suspend fun incrementHabitsCompleted() {

        dao.incrementHabitsCompleted()
    }

    // ── Global Streak ─────────────────────────────────────────────────────────

    suspend fun updateGlobalStreak() {
        val current = getOrCreateProgress()
        val today = LocalDate.now().format(dateFormatter)

        if (current.lastActiveDate == today) return // already updated today

        val yesterday = LocalDate.now().minusDays(1).format(dateFormatter)
        val newStreak = if (current.lastActiveDate == yesterday) {
            current.currentStreak + 1
        } else {
            1 // streak broken or first day
        }
        val newBest = maxOf(newStreak, current.bestStreak)
        dao.updateStreak(streak = newStreak, best = newBest, date = today)

        // Award bonus XP for streak milestones
        val bonus = XPManager.streakBonusXp(newStreak)
        if (bonus > 0) awardXp(bonus)
    }

    // Compatibility method for HomeViewModel
    suspend fun updateGlobalStreak(current: UserProgress?) {
        updateGlobalStreak()
    }

    // ── Initialization ────────────────────────────────────────────────────────

    suspend fun initialize() {
        getOrCreateProgress()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private suspend fun getOrCreateProgress(): UserProgress {
        return dao.getProgressOnce() ?: run {
            val fresh = UserProgress()
            dao.insertOrUpdate(fresh)
            fresh
        }
    }

    suspend fun ensureProgressExists() {
        getOrCreateProgress()
    }
}
