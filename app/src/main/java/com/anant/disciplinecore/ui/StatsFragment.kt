package com.anant.disciplinecore.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.anant.disciplinecore.databinding.FragmentStatsBinding
import com.anant.disciplinecore.viewmodel.HabitViewModel
import java.util.Calendar

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HabitViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeStats()
    }

    private fun observeStats() {
        viewModel.allHabits.observe(viewLifecycleOwner) { habits ->

            // ── Basic counts ─────────────────────────────────────
            val total     = habits.size
            val completed = habits.count { it.isCompletedToday }
            val score     = if (total > 0) (completed * 100) / total else 0
            val bestStreak = habits.maxOfOrNull { it.streak } ?: 0

            binding.tvDisciplineScore.text  = "$score%"
            binding.tvTotalHabits.text      = total.toString()
            binding.tvCompletedHabits.text  = completed.toString()
            binding.tvBestStreak.text       = bestStreak.toString()

            // ── Best / Worst habits ──────────────────────────────
            val sorted = habits.sortedByDescending { it.streak }
            val best   = sorted.firstOrNull()
            val worst  = sorted.lastOrNull()

            binding.tvBestHabit.text =
                best?.name ?: "—"
            binding.tvBestHabitStreak.text =
                if (best != null) "🔥 ${best.streak} day streak" else ""

            binding.tvWorstHabit.text =
                if (worst != null && worst != best) worst.name else "Keep it up!"
            binding.tvWorstHabitStreak.text =
                if (worst != null && worst != best) "🔥 ${worst.streak} days" else ""

            // ── Weekly bar chart ─────────────────────────────────
            buildWeeklyChart(completed, total)

            // ── 30-day heatmap ───────────────────────────────────
            buildHeatmap(habits.map { it.streak })

            // ── Streak history ───────────────────────────────────
            buildStreakHistory(habits.map { Pair(it.name, it.streak) })
        }
    }

    // ── Weekly Bar Chart ─────────────────────────────────────────
    private fun buildWeeklyChart(completedToday: Int, totalToday: Int) {
        val chartLayout  = binding.layoutWeeklyChart
        val labelsLayout = binding.layoutWeekDayLabels
        chartLayout.removeAllViews()
        labelsLayout.removeAllViews()

        val days   = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
        val today  = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        // DAY_OF_WEEK: Sun=1 Mon=2 ... Sat=7
        val todayIndex = if (today == 1) 6 else today - 2 // convert to Mon=0

        // Simulate values — today is real, rest are plausible mock data
        val pcts = mutableListOf(72, 50, 88, 60, 40, 100, 75)
        if (totalToday > 0)
            pcts[todayIndex] = (completedToday * 100) / totalToday
        else
            pcts[todayIndex] = 0

        val maxBarHeightDp = 100
        val density = resources.displayMetrics.density

        days.forEachIndexed { i, label ->
            val pct     = pcts[i]
            val isToday = i == todayIndex
            val barColor = when {
                pct >= 80 -> Color.parseColor("#22C55E")
                pct >= 50 -> Color.parseColor("#F59E0B")
                else      -> Color.parseColor("#3A3A45")
            }

            // Column wrapper
            val col = LinearLayout(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                orientation = LinearLayout.VERTICAL
                gravity     = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            }

            // Pct label above bar
            val pctLabel = TextView(requireContext()).apply {
                text      = if (pct > 0) "$pct%" else ""
                textSize  = 8f
                setTextColor(if (isToday) Color.parseColor("#F59E0B")
                else Color.parseColor("#6B6B80"))
                gravity   = Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            }

            // Bar
            val barHeightPx = ((pct.coerceAtLeast(5) / 100f) *
                    maxBarHeightDp * density).toInt()
            val bar = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    (24 * density).toInt(), barHeightPx).also {
                    it.gravity = Gravity.CENTER_HORIZONTAL
                }
                setBackgroundColor(
                    if (isToday) Color.parseColor("#F59E0B") else barColor)
                background = resources.getDrawable(
                    android.R.drawable.dialog_holo_light_frame, null)
                    .also { /* skip shape, just color */ }
                setBackgroundColor(
                    if (isToday) Color.parseColor("#F59E0B") else barColor)
            }

            col.addView(pctLabel)
            col.addView(bar)
            chartLayout.addView(col)

            // Day label
            val dayLabel = TextView(requireContext()).apply {
                text      = label
                textSize  = 10f
                gravity   = Gravity.CENTER
                setTextColor(
                    if (isToday) Color.parseColor("#F59E0B")
                    else Color.parseColor("#6B6B80"))
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            labelsLayout.addView(dayLabel)
        }
    }

    // ── 30-Day Heatmap ───────────────────────────────────────────
    private fun buildHeatmap(streaks: List<Int>) {
        val container = binding.layoutHeatmap
        container.removeAllViews()

        val density   = resources.displayMetrics.density
        val cellSize  = (14 * density).toInt()
        val cellGap   = (4  * density).toInt()
        val cols      = 10  // 10 columns × 3 rows = 30 days

        repeat(3) { row ->
            val rowLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT).also {
                    it.bottomMargin = cellGap
                }
            }

            repeat(cols) { col ->
                val dayIndex = row * cols + col
                // Use streak data cyclically to fill 30 days
                val value = streaks.getOrElse(dayIndex % streaks.size.coerceAtLeast(1)) { 0 }
                val color = when {
                    value == 0  -> Color.parseColor("#2A2A35")
                    value < 3   -> Color.parseColor("#6B3F00")
                    else        -> Color.parseColor("#F59E0B")
                }
                val cell = View(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(cellSize, cellSize).also {
                        it.marginEnd = cellGap
                    }
                    setBackgroundColor(color)
                }
                rowLayout.addView(cell)
            }
            container.addView(rowLayout)
        }
    }

    // ── Streak History ───────────────────────────────────────────
    private fun buildStreakHistory(habits: List<Pair<String, Int>>) {
        val container = binding.layoutStreakHistory
        container.removeAllViews()
        val density = resources.displayMetrics.density

        if (habits.isEmpty()) {
            val empty = TextView(requireContext()).apply {
                text      = "No habits yet. Add some!"
                textSize  = 13f
                setTextColor(Color.parseColor("#6B6B80"))
            }
            container.addView(empty)
            return
        }

        habits.sortedByDescending { it.second }.forEach { (name, streak) ->
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity     = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT).also {
                    it.bottomMargin = (10 * density).toInt()
                }
            }

            val nameView = TextView(requireContext()).apply {
                text      = name
                textSize  = 14f
                setTextColor(Color.parseColor("#F5F5F0"))
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val streakView = TextView(requireContext()).apply {
                text      = "🔥 $streak days"
                textSize  = 13f
                setTextColor(Color.parseColor("#F59E0B"))
                gravity   = Gravity.END
            }

            // Mini progress bar under the name
            val barBg = View(requireContext()).apply {
                setBackgroundColor(Color.parseColor("#2A2A35"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (4 * density).toInt())
            }

            val col = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            col.addView(nameView)
            col.addView(barBg)

            row.addView(col)
            row.addView(streakView)
            container.addView(row)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}