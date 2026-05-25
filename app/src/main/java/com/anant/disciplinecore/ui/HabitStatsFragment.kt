package com.anant.disciplinecore.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.anant.disciplinecore.databinding.FragmentHabitStatsBinding
import com.anant.disciplinecore.viewmodel.HabitViewModel

class HabitStatsFragment : Fragment() {

    private var _binding: FragmentHabitStatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HabitViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHabitStatsBinding.inflate(
            inflater,
            container,
            false
        )

        val habitId = arguments?.getInt("habit_id") ?: -1

        observeHabit(habitId)

        return binding.root
    }

    private fun observeHabit(habitId: Int) {

        viewModel.allHabits.observe(viewLifecycleOwner) { habits ->

            val habit = habits.find { it.id == habitId } ?: return@observe

            binding.tvHabitName.text = "${habit.emoji} ${habit.name}"
            binding.tvCategory.text = habit.category
            binding.tvStreak.text = "🔥 ${habit.streak}"
            binding.tvLongest.text = "🏆 ${habit.longestStreak}"
            binding.tvTotal.text = "✅ ${habit.totalCompletions}"

            val consistency =
                if (habit.totalCompletions > 0)
                    (habit.streak * 100) / habit.totalCompletions
                else
                    0

            binding.tvConsistency.text = "$consistency%"

            binding.progressBar.progress = consistency

            val color = when {
                consistency >= 80 ->
                    Color.parseColor("#22C55E")

                consistency >= 50 ->
                    Color.parseColor("#F59E0B")

                else ->
                    Color.parseColor("#EF4444")
            }

            binding.progressBar.progressTintList =
                android.content.res.ColorStateList.valueOf(color)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}