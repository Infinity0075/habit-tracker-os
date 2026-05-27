package com.anant.disciplinecore.core.gamification

object XPManager {

    // =================================================
    // XP REWARDS
    // =================================================

    const val XP_HABIT_COMPLETE = 10
    const val XP_ALL_HABITS_COMPLETE = 50
    const val XP_FOCUS_SESSION = 25

    const val STREAK_7_XP = 30
    const val STREAK_30_XP = 120

    // For backward compatibility or if used elsewhere
    const val HABIT_COMPLETE_XP = 10
    const val FULL_DAY_XP = 50

    // =================================================
    // LEVEL CALCULATION
    // =================================================

    fun calculateLevel(
        xp: Int
    ): Int {
        return (xp / 120) + 1
    }

    fun getLevelForXp(xp: Int): Int = calculateLevel(xp)

    // =================================================
    // XP REQUIRED
    // =================================================

    fun xpForNextLevel(
        level: Int
    ): Int {
        return level * 120
    }

    // =================================================
    // STREAK BONUS
    // =================================================

    fun streakBonusXp(streak: Int): Int {
        return when (streak) {
            7 -> STREAK_7_XP
            30 -> STREAK_30_XP
            else -> 0
        }
    }

    // =================================================
    // TITLES
    // =================================================

    fun getRankTitle(
        level: Int
    ): String {

        return when {

            level >= 40 ->
                "Monster Mode"

            level >= 30 ->
                "Elite"

            level >= 20 ->
                "Warrior"

            level >= 10 ->
                "Disciplined"

            else ->
                "Beginner"
        }
    }
}
