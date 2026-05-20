package com.anant.disciplinecore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.anant.disciplinecore.databinding.FragmentStatsBinding
import com.anant.disciplinecore.viewmodel.HabitViewModel

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HabitViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentStatsBinding.inflate(
            inflater,
            container,
            false
        )

        observeStats()

        return binding.root
    }

    private fun observeStats() {

        viewModel.allHabits.observe(viewLifecycleOwner) { habits ->

            val totalHabits = habits.size

            val completedHabits =
                habits.count { it.isCompletedToday }

            val disciplineScore =
                if (totalHabits > 0)
                    (completedHabits * 100) / totalHabits
                else
                    0

            val bestStreak =
                habits.maxOfOrNull { it.streak } ?: 0

            binding.tvDisciplineScore.text =
                "$disciplineScore%"

            binding.tvTotalHabits.text =
                totalHabits.toString()

            binding.tvCompletedHabits.text =
                completedHabits.toString()

            binding.tvBestStreak.text =
                "$bestStreak Days"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}