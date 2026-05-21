package com.anant.disciplinecore.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anant.disciplinecore.data.Habit
import com.anant.disciplinecore.databinding.ItemHabitBinding

class HabitAdapter(
    private val onToggle: (Habit) -> Unit,
    private val onLongPress: (Habit) -> Unit
) : ListAdapter<Habit, HabitAdapter.HabitViewHolder>(DIFF_CALLBACK) {

    // ── Category emoji map ────────────────────────────────────────
    private val categoryEmoji = mapOf(
        "General"  to "🗂️",
        "Health"   to "❤️",
        "Fitness"  to "🏋️",
        "Mind"     to "🧠",
        "Work"     to "💼",
        "Finance"  to "💰",
        "Social"   to "👥",
        "Creative" to "🎨"
    )

    inner class HabitViewHolder(private val binding: ItemHabitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit) {
            binding.apply {

                // Basic fields
                tvEmoji.text     = habit.emoji
                tvHabitName.text = habit.name
                tvStreak.text    = if (habit.streak > 0) "🔥 ${habit.streak}" else "—"
                tvTotal.text     = "${habit.totalCompletions}x done"

                // Category badge
                val catEmoji = categoryEmoji[habit.category] ?: "🗂️"
                tvCategory.text = "$catEmoji ${habit.category}"

                // Checkbox — remove listener before setting state
                cbDone.setOnCheckedChangeListener(null)
                cbDone.isChecked = habit.isCompletedToday

                // Completed visual state
                root.alpha = if (habit.isCompletedToday) 0.75f else 1.0f
                tvHabitName.paintFlags = if (habit.isCompletedToday) {
                    tvHabitName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    tvHabitName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }

                // Listeners
                cbDone.setOnCheckedChangeListener { _, _ -> onToggle(habit) }
                root.setOnLongClickListener {
                    onLongPress(habit)
                    true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Habit>() {
            override fun areItemsTheSame(old: Habit, new: Habit) = old.id == new.id
            override fun areContentsTheSame(old: Habit, new: Habit) = old == new
        }
    }
}