package com.anant.disciplinecore.core.analytics

import com.anant.disciplinecore.data.local.entities.Habit
import kotlin.math.roundToInt

object DisciplineEngine {

    // =========================================================
    // DISCIPLINE SCORE
    // =========================================================

    fun calculateDisciplineScore(
        habits: List<Habit>
    ): Int {

        if (habits.isEmpty()) return 0

        val completionScore =
            habits.count { it.isCompletedToday } * 10

        val streakScore =
            habits.sumOf { habit ->
                habit.streak
            }

        val consistencyScore =
            habits.sumOf { habit ->
                habit.consistencyScore
            }

        val totalScore =
            completionScore +
                    streakScore +
                    consistencyScore

        return totalScore.coerceIn(0, 1000)
    }

    // =========================================================
    // COMPLETION PERCENTAGE
    // =========================================================

    fun calculateCompletionRate(
        habits: List<Habit>
    ): Int {

        if (habits.isEmpty()) return 0

        val completed =
            habits.count { it.isCompletedToday }

        return (
                completed.toFloat() /
                        habits.size.toFloat() * 100
                ).roundToInt()
    }

    // =========================================================
    // LONGEST STREAK
    // =========================================================

    fun getLongestStreak(
        habits: List<Habit>
    ): Int {

        return habits.maxOfOrNull {
            it.longestStreak
        } ?: 0
    }

    // =========================================================
    // TOTAL ACTIVE STREAKS
    // =========================================================

    fun getTotalCurrentStreaks(
        habits: List<Habit>
    ): Int {

        return habits.sumOf { habit ->
            habit.streak
        }
    }

    // =========================================================
    // MOST CONSISTENT HABIT
    // =========================================================

    fun getMostConsistentHabit(
        habits: List<Habit>
    ): Habit? {

        return habits.maxByOrNull {
            it.consistencyScore
        }
    }

    // =========================================================
    // DAILY INSIGHT
    // =========================================================

    fun generateDailyInsight(
        habits: List<Habit>
    ): String {

        if (habits.isEmpty()) {
            return "Start building your discipline system today."
        }

        val completionRate =
            calculateCompletionRate(habits)

        return when {

            completionRate >= 90 ->
                "Elite consistency. You're building momentum."

            completionRate >= 70 ->
                "Strong discipline today. Stay consistent."

            completionRate >= 50 ->
                "You're progressing. Push a little harder."

            else ->
                "Small actions today create powerful results tomorrow."
        }
    }
}