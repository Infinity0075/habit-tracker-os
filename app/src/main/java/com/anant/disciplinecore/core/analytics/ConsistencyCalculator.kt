package com.anant.disciplinecore.core.analytics

object ConsistencyCalculator {

    // =========================================================
    // CALCULATE CONSISTENCY SCORE
    // =========================================================

    fun calculateConsistencyScore(
        completedDays: Int,
        totalTrackedDays: Int
    ): Float {

        if (totalTrackedDays <= 0) {
            return 0f
        }

        return (
                completedDays.toFloat() /
                        totalTrackedDays.toFloat()
                ) * 100f
    }

    // =========================================================
    // GET CONSISTENCY LABEL
    // =========================================================

    fun getConsistencyLabel(
        score: Float
    ): String {

        return when {

            score >= 90f ->
                "Elite"

            score >= 75f ->
                "Strong"

            score >= 60f ->
                "Consistent"

            score >= 40f ->
                "Improving"

            else ->
                "Needs Focus"
        }
    }

    // =========================================================
    // GET CONSISTENCY MESSAGE
    // =========================================================

    fun getConsistencyMessage(
        score: Float
    ): String {

        return when {

            score >= 90f ->
                "You are operating with elite discipline."

            score >= 75f ->
                "Your consistency is becoming a real strength."

            score >= 60f ->
                "Good momentum. Stay locked in."

            score >= 40f ->
                "You're improving. Keep showing up daily."

            else ->
                "Consistency is built one action at a time."
        }
    }

    // =========================================================
    // NORMALIZE SCORE
    // =========================================================

    fun normalizeScore(
        score: Float
    ): Float {

        return score
            .coerceIn(0f, 100f)
    }
}