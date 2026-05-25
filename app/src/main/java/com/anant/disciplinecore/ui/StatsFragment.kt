package com.anant.disciplinecore.ui

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.anant.disciplinecore.R
import com.anant.disciplinecore.databinding.FragmentStatsBinding
import com.anant.disciplinecore.viewmodel.HabitViewModel
import java.text.SimpleDateFormat
import java.util.*

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HabitViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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

    // ------------------------------------------------
    // STATS
    // ------------------------------------------------

    private fun observeStats() {

        viewModel.allHabits.observe(viewLifecycleOwner) { habits ->

            val total = habits.size

            val completed =
                habits.count { it.isCompletedToday }

            val score =
                if (total > 0)
                    (completed * 100) / total
                else
                    0

            val bestStreak =
                habits.maxOfOrNull { it.streak } ?: 0

            binding.tvDisciplineScore.text = "$score%"
            binding.tvTotalHabits.text = total.toString()
            binding.tvCompletedHabits.text = completed.toString()
            binding.tvBestStreak.text = bestStreak.toString()

            val sorted =
                habits.sortedByDescending { it.streak }

            val best = sorted.firstOrNull()
            val worst = sorted.lastOrNull()

            binding.tvBestHabit.text =
                best?.name ?: "—"

            binding.tvBestHabitStreak.text =
                if (best != null)
                    "🔥 ${best.streak} days"
                else
                    ""

            binding.tvWorstHabit.text =
                if (worst != null && worst != best)
                    worst.name
                else
                    "Keep pushing"

            binding.tvWorstHabitStreak.text =
                if (worst != null && worst != best)
                    "🔥 ${worst.streak} days"
                else
                    ""

            buildStreakHistory(
                habits.map { Pair(it.name, it.streak) }
            )
        }
    }

    // ------------------------------------------------
    // CHARTS
    // ------------------------------------------------

    private fun observeCharts() {

        viewModel.last7Days.observe(viewLifecycleOwner) { data ->
            buildWeeklyChart(data)
        }

        viewModel.last30Days.observe(viewLifecycleOwner) { data ->
            buildHeatmap(data)
        }
    }

    // ------------------------------------------------
    // WEEKLY CHART
    // ------------------------------------------------

    private fun buildWeeklyChart(data: Map<String, Int>) {

        val chartLayout = binding.layoutWeeklyChart
        val labelsLayout = binding.layoutWeekDayLabels

        chartLayout.removeAllViews()
        labelsLayout.removeAllViews()

        val sdf =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val dayFormat =
            SimpleDateFormat("EEE", Locale.getDefault())

        val density = resources.displayMetrics.density

        val maxHeight = (120 * density).toInt()

        val days = (6 downTo 0).map { offset ->

            val calendar = Calendar.getInstance()

            calendar.add(Calendar.DAY_OF_YEAR, -offset)

            val dateString = sdf.format(calendar.time)

            val label = dayFormat.format(calendar.time)

            val count = data[dateString] ?: 0

            Triple(label, count, offset == 0)
        }

        val maxCount =
            days.maxOfOrNull { it.second }?.coerceAtLeast(1)
                ?: 1

        days.forEach { (label, count, isToday) ->

            val column = LinearLayout(requireContext()).apply {

                orientation = LinearLayout.VERTICAL

                gravity =
                    Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL

                layoutParams =
                    LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1f
                    )
            }

            // VALUE

            val valueText = TextView(requireContext()).apply {

                text =
                    if (count > 0) count.toString() else ""

                textSize = 10f

                gravity = Gravity.CENTER

                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (isToday)
                            R.color.accent_amber
                        else
                            R.color.text_secondary
                    )
                )
            }

            // BAR

            val ratio =
                count.toFloat() / maxCount.toFloat()

            val barHeight =
                (ratio * maxHeight).toInt().coerceAtLeast(
                    (10 * density).toInt()
                )

            val bar = View(requireContext()).apply {

                layoutParams =
                    LinearLayout.LayoutParams(
                        (28 * density).toInt(),
                        barHeight
                    ).also {
                        it.topMargin = (8 * density).toInt()
                    }

                background = GradientDrawable().apply {

                    cornerRadius = 24f

                    setColor(
                        ContextCompat.getColor(
                            requireContext(),
                            when {
                                isToday ->
                                    R.color.accent_amber

                                ratio >= 0.75f ->
                                    R.color.green

                                ratio >= 0.35f ->
                                    R.color.accent_amber

                                else ->
                                    R.color.divider
                            }
                        )
                    )
                }
            }

            column.addView(valueText)
            column.addView(bar)

            chartLayout.addView(column)

            // DAY LABEL

            val dayText = TextView(requireContext()).apply {

                text = label

                textSize = 11f

                gravity = Gravity.CENTER

                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (isToday)
                            R.color.accent_amber
                        else
                            R.color.text_secondary
                    )
                )

                layoutParams =
                    LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
            }

            labelsLayout.addView(dayText)
        }
    }

    // ------------------------------------------------
    // HEATMAP
    // ------------------------------------------------

    private fun buildHeatmap(data: Map<String, Int>) {

        val container = binding.layoutHeatmap

        container.removeAllViews()

        val density = resources.displayMetrics.density

        val cell = (18 * density).toInt()

        val gap = (5 * density).toInt()

        val sdf =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val dates = (29 downTo 0).map { offset ->

            val calendar = Calendar.getInstance()

            calendar.add(Calendar.DAY_OF_YEAR, -offset)

            sdf.format(calendar.time)
        }

        val maxCount =
            data.values.maxOrNull()?.coerceAtLeast(1) ?: 1

        repeat(3) { row ->

            val rowLayout = LinearLayout(requireContext()).apply {

                orientation = LinearLayout.HORIZONTAL

                layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        it.bottomMargin = gap
                    }
            }

            repeat(10) { col ->

                val date =
                    dates.getOrNull(row * 10 + col) ?: ""

                val count = data[date] ?: 0

                val ratio =
                    count.toFloat() / maxCount.toFloat()

                val color = when {

                    count == 0 ->
                        R.color.divider

                    ratio < 0.4f ->
                        R.color.accent_amber_dark

                    ratio < 0.8f ->
                        R.color.accent_amber

                    else ->
                        R.color.green
                }

                val square = View(requireContext()).apply {

                    layoutParams =
                        LinearLayout.LayoutParams(
                            cell,
                            cell
                        ).also {
                            it.marginEnd = gap
                        }

                    background = GradientDrawable().apply {

                        cornerRadius = 8f

                        setColor(
                            ContextCompat.getColor(
                                requireContext(),
                                color
                            )
                        )
                    }
                }

                rowLayout.addView(square)
            }

            container.addView(rowLayout)
        }
    }

    // ------------------------------------------------
    // STREAK HISTORY
    // ------------------------------------------------

    private fun buildStreakHistory(
        habits: List<Pair<String, Int>>
    ) {

        val container = binding.layoutStreakHistory

        container.removeAllViews()

        val density = resources.displayMetrics.density

        if (habits.isEmpty()) {

            val empty = TextView(requireContext()).apply {

                text = "No habits yet."

                textSize = 14f

                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.text_secondary
                    )
                )
            }

            container.addView(empty)

            return
        }

        val max =
            habits.maxOfOrNull { it.second }
                ?.coerceAtLeast(1) ?: 1

        habits.sortedByDescending { it.second }
            .forEach { (name, streak) ->

                val wrapper =
                    LinearLayout(requireContext()).apply {

                        orientation = LinearLayout.VERTICAL

                        layoutParams =
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).also {
                                it.bottomMargin =
                                    (18 * density).toInt()
                            }
                    }

                val top =
                    LinearLayout(requireContext()).apply {

                        orientation = LinearLayout.HORIZONTAL

                        gravity = Gravity.CENTER_VERTICAL
                    }

                val nameText = TextView(requireContext()).apply {

                    text = name

                    textSize = 15f

                    setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.text_primary
                        )
                    )

                    layoutParams =
                        LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        )
                }

                val streakText = TextView(requireContext()).apply {

                    text = "🔥 $streak"

                    textSize = 13f

                    setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.accent_amber
                        )
                    )
                }

                top.addView(nameText)
                top.addView(streakText)

                // PROGRESS

                val progressBg = LinearLayout(requireContext()).apply {

                    layoutParams =
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            (8 * density).toInt()
                        ).also {
                            it.topMargin = (8 * density).toInt()
                        }

                    background = GradientDrawable().apply {

                        cornerRadius = 20f

                        setColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.divider
                            )
                        )
                    }
                }

                val progress =
                    View(requireContext()).apply {

                        layoutParams =
                            LinearLayout.LayoutParams(
                                ((streak.toFloat() / max.toFloat()) *
                                        resources.displayMetrics.widthPixels * 0.65f).toInt(),
                                (8 * density).toInt()
                            )

                        background = GradientDrawable().apply {

                            cornerRadius = 20f

                            setColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.accent_amber
                                )
                            )
                        }
                    }

                progressBg.addView(progress)

                wrapper.addView(top)
                wrapper.addView(progressBg)

                container.addView(wrapper)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}