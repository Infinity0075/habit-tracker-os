package com.anant.disciplinecore.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.anant.disciplinecore.R
import com.anant.disciplinecore.data.Habit
import com.anant.disciplinecore.databinding.ActivityMainBinding
import com.anant.disciplinecore.viewmodel.HabitViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: HabitViewModel by viewModels()
    private lateinit var adapter: HabitAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader()
        setupRecyclerView()
        observeHabits()
        setupFab()
    }

    private fun setupHeader() {
        // Greeting based on time of day
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Good Morning ☀️"
            hour < 17 -> "Good Afternoon 🌤️"
            else      -> "Good Evening 🌙"
        }
        binding.tvGreeting.text = greeting

        val dateStr = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date())
        binding.tvDate.text = dateStr
    }

    private fun setupRecyclerView() {
        adapter = HabitAdapter(
            onToggle = { habit -> viewModel.toggleCompletion(habit) },
            onLongPress = { habit -> confirmDelete(habit) }
        )
        binding.rvHabits.layoutManager = LinearLayoutManager(this)
        binding.rvHabits.adapter = adapter
    }

    private fun observeHabits() {
        viewModel.allHabits.observe(this) { habits ->
            adapter.submitList(habits)
            updateScoreCard(habits)
            binding.emptyState.visibility = if (habits.isEmpty()) View.VISIBLE else View.GONE
            binding.rvHabits.visibility = if (habits.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun updateScoreCard(habits: List<Habit>) {
        val total = habits.size
        val done = habits.count { it.isCompletedToday }
        val pct = if (total > 0) (done * 100) / total else 0

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

        // Progress bar color shifts with completion
        val color = when {
            pct == 100 -> Color.parseColor("#22C55E")  // green
            pct >= 50  -> Color.parseColor("#F59E0B")  // amber
            else       -> Color.parseColor("#EF4444")  // red
        }
        binding.progressBar.progressTintList = android.content.res.ColorStateList.valueOf(color)
    }

    private fun setupFab() {

        binding.fab.setOnClickListener {

            val dialog = AddHabitDialog()

            dialog.onAdd = { habit ->
                viewModel.insert(habit)
            }

            dialog.show(supportFragmentManager, "AddHabitDialog")
        }
    }

    private fun confirmDelete(habit: Habit) {
        AlertDialog.Builder(this, R.style.AlertDialogDark)
            .setTitle("Delete habit?")
            .setMessage("\"${habit.name}\" and its ${habit.streak}-day streak will be gone forever.")
            .setPositiveButton("Delete") { _, _ -> viewModel.delete(habit) }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
