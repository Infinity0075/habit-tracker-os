package com.anant.disciplinecore.core.analytics

import com.anant.disciplinecore.data.local.entities.Habit

object InsightGenerator {

    // =========================================================
    // GENERATE HOME INSIGHT
    // =========================================================

    fun generateHomeInsight(
        habits: List<Habit>
    ): String {

        if (habits.isEmpty()) {
            return "Start with one habit. Momentum changes everything."
        }

        val completionRate =
            DisciplineEngine.calculateCompletionRate(habits)

        val longestStreak =
            DisciplineEngine.getLongestStreak(habits)

        return when {

            completionRate >= 90 ->
                "You're operating at an elite level today."

            completionRate >= 70 ->
                "Strong consistency. Keep the momentum alive."

            longestStreak >= 30 ->
                "Your streak discipline is becoming part of your identity."

            completionRate >= 50 ->
                "Good progress today. Stay focused."

            else ->
                "Discipline is built through small repeated actions."
        }
    }

    // =========================================================
    // GENERATE MOTIVATION MESSAGE
    // =========================================================

    fun generateMotivationMessage(
        score: Int
    ): String {

        return when {

            score >= 800 ->
                "Locked in. Relentless focus."

            score >= 600 ->
                "Momentum is building beautifully."

            score >= 400 ->
                "You are becoming more disciplined daily."

            score >= 200 ->
                "Progress matters more than perfection."

            else ->
                "Start small. Stay consistent."
        }
    }

    // =========================================================
    // GENERATE STREAK MESSAGE
    // =========================================================

    fun generateStreakMessage(
        streak: Int
    ): String {

        return when {

            streak >= 100 ->
                "Legendary consistency."

            streak >= 50 ->
                "Your discipline is becoming automatic."

            streak >= 30 ->
                "One month of consistency. Powerful."

            streak >= 7 ->
                "A full week locked in."

            streak >= 3 ->
                "Momentum is starting."

            else ->
                "Every streak starts with day one."
        }
    }

    // =========================================================
    // GENERATE FOCUS MESSAGE
    // =========================================================

    fun generateFocusMessage(
        sessionsCompleted: Int
    ): String {

        return when {

            sessionsCompleted >= 10 ->
                "Deep focus mode activated."

            sessionsCompleted >= 5 ->
                "You're building serious focus endurance."

            sessionsCompleted >= 3 ->
                "Strong focus sessions today."

            sessionsCompleted >= 1 ->
                "Good work. Keep showing up."

            else ->
                "Protect your attention. It shapes your future."
        }
    }
}