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
import java.text.SimpleDateFormat
import java.util.*

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
        observeCharts()
        viewModel.loadChartData()
    }

    // ── Basic stats from habits ───────────────────────────────────
    private fun observeStats() {
        viewModel.allHabits.observe(viewLifecycleOwner) { habits ->

            val total      = habits.size
            val completed  = habits.count { it.isCompletedToday }
            val score      = if (total > 0) (completed * 100) / total else 0
            val bestStreak = habits.maxOfOrNull { it.streak } ?: 0

            binding.tvDisciplineScore.text = "$score%"
            binding.tvTotalHabits.text     = total.toString()
            binding.tvCompletedHabits.text = completed.toString()
            binding.tvBestStreak.text      = bestStreak.toString()

            // Best / worst habits
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

            // Streak history
            buildStreakHistory(habits.map { Pair(it.name, it.streak) })
        }
    }

    // ── Real chart data from logs ─────────────────────────────────
    private fun observeCharts() {

        // Weekly bar chart — real data
        viewModel.last7Days.observe(viewLifecycleOwner) { data ->
            buildWeeklyChart(data)
        }

        // 30-day heatmap — real data
        viewModel.last30Days.observe(viewLifecycleOwner) { data ->
            buildHeatmap(data)
        }
    }

    // ── Weekly Bar Chart (real data) ──────────────────────────────
    private fun buildWeeklyChart(data: Map<String, Int>) {
        val chartLayout  = binding.layoutWeeklyChart
        val labelsLayout = binding.layoutWeekDayLabels
        chartLayout.removeAllViews()
        labelsLayout.removeAllViews()

        val sdf     = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFmt  = SimpleDateFormat("EEE", Locale.getDefault())
        val density = resources.displayMetrics.density
        val maxBarH = 100

        // Build last 7 days list
        val days = (6 downTo 0).map { offset ->
            val cal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -offset)
            }
            val dateStr = sdf.format(cal.time)
            val label   = dayFmt.format(cal.time) // Mon, Tue...
            val count   = data[dateStr] ?: 0
            val isToday = offset == 0
            Triple(dateStr, label, Pair(count, isToday))
        }

        val maxCount = days.maxOfOrNull { it.third.first } ?: 1

        days.forEach { (_, label, info) ->
            val (count, isToday) = info
            val pct = if (maxCount > 0) (count * 100) / maxCount else 0

            val barColor = when {
                count == 0 -> Color.parseColor("#2A2A35")
                pct >= 80  -> Color.parseColor("#22C55E")
                pct >= 40  -> Color.parseColor("#F59E0B")
                else       -> Color.parseColor("#3A3A45")
            }

            // Column
            val col = LinearLayout(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                orientation = LinearLayout.VERTICAL
                gravity     = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            }

            // Count label
            val countLabel = TextView(requireContext()).apply {
                text     = if (count > 0) "$count" else ""
                textSize = 8f
                setTextColor(
                    if (isToday) Color.parseColor("#F59E0B")
                    else Color.parseColor("#6B6B80"))
                gravity  = Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            }

            // Bar
            val barH = ((pct.coerceAtLeast(5) / 100f) * maxBarH * density).toInt()
            val bar  = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    (24 * density).toInt(), barH).also {
                    it.gravity = Gravity.CENTER_HORIZONTAL
                }
                setBackgroundColor(
                    if (isToday) Color.parseColor("#F59E0B") else barColor)
            }

            col.addView(countLabel)
            col.addView(bar)
            chartLayout.addView(col)

            // Day label
            val dayLabel = TextView(requireContext()).apply {
                text     = label
                textSize = 10f
                gravity  = Gravity.CENTER
                setTextColor(
                    if (isToday) Color.parseColor("#F59E0B")
                    else Color.parseColor("#6B6B80"))
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            labelsLayout.addView(dayLabel)
        }
    }

    // ── 30-Day Heatmap (real data) ────────────────────────────────
    private fun buildHeatmap(data: Map<String, Int>) {
        val container = binding.layoutHeatmap
        container.removeAllViews()

        val density  = resources.displayMetrics.density
        val cellSize = (14 * density).toInt()
        val cellGap  = (4  * density).toInt()
        val cols     = 10 // 10 cols × 3 rows = 30 days
        val sdf      = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Build last 30 dates
        val dates = (29 downTo 0).map { offset ->
            val cal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -offset)
            }
            sdf.format(cal.time)
        }

        val maxCount = data.values.maxOrNull()?.coerceAtLeast(1) ?: 1

        repeat(3) { row ->
            val rowLayout = LinearLayout(requireContext()).apply {
                orientation  = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT).also {
                    it.bottomMargin = cellGap
                }
            }

            repeat(cols) { col ->
                val dateStr = dates.getOrNull(row * cols + col) ?: ""
                val count   = data[dateStr] ?: 0
                val ratio   = count.toFloat() / maxCount

                val color = when {
                    count == 0   -> Color.parseColor("#2A2A35")
                    ratio < 0.4f -> Color.parseColor("#6B3F00")
                    ratio < 0.8f -> Color.parseColor("#D97706")
                    else         -> Color.parseColor("#F59E0B")
                }

                val cell = View(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        cellSize, cellSize).also {
                        it.marginEnd = cellGap
                    }
                    setBackgroundColor(color)
                }
                rowLayout.addView(cell)
            }
            container.addView(rowLayout)
        }
    }

    // ── Streak History ────────────────────────────────────────────
    private fun buildStreakHistory(habits: List<Pair<String, Int>>) {
        val container = binding.layoutStreakHistory
        container.removeAllViews()
        val density = resources.displayMetrics.density

        if (habits.isEmpty()) {
            val empty = TextView(requireContext()).apply {
                text     = "No habits yet. Add some!"
                textSize = 13f
                setTextColor(Color.parseColor("#6B6B80"))
            }
            container.addView(empty)
            return
        }

        habits.sortedByDescending { it.second }.forEach { (name, streak) ->
            val row = LinearLayout(requireContext()).apply {
                orientation  = LinearLayout.HORIZONTAL
                gravity      = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT).also {
                    it.bottomMargin = (10 * density).toInt()
                }
            }

            val col = LinearLayout(requireContext()).apply {
                orientation  = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val nameView = TextView(requireContext()).apply {
                text     = name
                textSize = 14f
                setTextColor(Color.parseColor("#F5F5F0"))
            }

            // Mini progress bar
            val maxStreak = (habits.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)
            val barWidth  = ((streak.toFloat() / maxStreak) *
                    resources.displayMetrics.widthPixels * 0.55f).toInt()

            val barBg = View(requireContext()).apply {
                setBackgroundColor(Color.parseColor("#2A2A35"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (4 * density).toInt()).also {
                    it.topMargin = (4 * density).toInt()
                }
            }

            val barFill = View(requireContext()).apply {
                setBackgroundColor(Color.parseColor("#F59E0B"))
                layoutParams = LinearLayout.LayoutParams(
                    barWidth, (4 * density).toInt()).also {
                    it.topMargin = (4 * density).toInt()
                }
            }

            col.addView(nameView)
            col.addView(barBg)
            col.addView(barFill)

            val streakView = TextView(requireContext()).apply {
                text     = "🔥 $streak days"
                textSize = 13f
                setTextColor(Color.parseColor("#F59E0B"))
                gravity  = Gravity.END
            }

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