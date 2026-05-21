package com.anant.disciplinecore.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anant.disciplinecore.R
import com.anant.disciplinecore.data.Habit
import com.anant.disciplinecore.databinding.FragmentHomeBinding
import com.anant.disciplinecore.viewmodel.HabitViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.fragment.app.activityViewModels

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HabitViewModel by activityViewModels()
    private lateinit var adapter: HabitAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupHeader()
        setupRecyclerView()
        observeHabits()
        return binding.root
    }

    // ── Header with username ──────────────────────────────────────
    private fun setupHeader() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        // Load saved name from Settings
        val prefs = requireContext()
            .getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        val name  = prefs.getString("user_name", "")?.trim()

        // Greeting changes by time of day
        val timeGreeting = when {
            hour < 12 -> "Good Morning ☀️"
            hour < 17 -> "Good Afternoon 🌤️"
            else      -> "Good Evening 🌙"
        }

        // If name is saved, personalise it
        binding.tvGreeting.text = if (!name.isNullOrEmpty()) {
            "$timeGreeting, $name!"
        } else {
            timeGreeting
        }

        val dateStr = SimpleDateFormat(
            "EEEE, d MMMM",
            Locale.getDefault()
        ).format(Date())

        binding.tvDate.text = dateStr
    }

    private fun setupRecyclerView() {
        adapter = HabitAdapter(
            onToggle = { habit ->
                viewModel.toggleCompletion(habit)
            },
            onLongPress = { habit ->
                confirmDelete(habit)
            }
        )

        binding.rvHabits.layoutManager =
            LinearLayoutManager(requireContext())
        binding.rvHabits.adapter = adapter
    }

    private fun observeHabits() {
        viewModel.allHabits.observe(viewLifecycleOwner) { habits ->

            val sortedHabits = habits.sortedBy {
                it.isCompletedToday
            }

            adapter.submitList(sortedHabits)
            updateScoreCard(habits)

            binding.emptyState.visibility =
                if (habits.isEmpty()) View.VISIBLE else View.GONE

            binding.rvHabits.visibility =
                if (habits.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun updateScoreCard(habits: List<Habit>) {
        val total = habits.size
        val done  = habits.count { it.isCompletedToday }
        val pct   = if (total > 0) (done * 100) / total else 0

        binding.tvScore.text = "$done / $total"

        binding.tvScoreLabel.text = when {
            total == 0 -> "No habits yet. Add one!"
            pct == 100 -> "🔥 Full discipline! Locked in."
            pct >= 75  -> "💪 Almost there, keep going!"
            pct >= 50  -> "⚡ Halfway done — push through."
            pct > 0    -> "🎯 You've started. Finish strong."
            else       -> "📋 $total habits to complete today"
        }

        binding.progressBar.progress = pct
        binding.tvPct.text = "$pct%"

        val color = when {
            pct == 100 -> Color.parseColor("#22C55E")
            pct >= 50  -> Color.parseColor("#F59E0B")
            else       -> Color.parseColor("#EF4444")
        }

        binding.progressBar.progressTintList =
            android.content.res.ColorStateList.valueOf(color)
    }

    // ── Refresh greeting when coming back to this screen ─────────
    override fun onResume() {
        super.onResume()
        setupHeader()
    }

    private fun confirmDelete(habit: Habit) {
        AlertDialog.Builder(requireContext(), R.style.AlertDialogDark)
            .setTitle("Delete habit?")
            .setMessage(
                "\"${habit.name}\" and its ${habit.streak}-day streak will be gone forever."
            )
            .setPositiveButton("Delete") { _, _ ->
                viewModel.delete(habit)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}