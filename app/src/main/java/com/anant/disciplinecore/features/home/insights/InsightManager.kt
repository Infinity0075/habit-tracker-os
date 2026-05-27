package com.anant.disciplinecore.features.home.insights

object InsightManager {

    data class Insight(
        val emoji: String,
        val message: String
    )

    fun getBasicInsights(): List<Insight> {
        return listOf(
            Insight("💡", "Consistency is the key to mastery."),
            Insight("⚡", "Momentum builds with every small win."),
            Insight("🎯", "Stay focused on your daily habits.")
        )
    }
}
