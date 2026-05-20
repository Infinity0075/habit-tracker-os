package com.anant.disciplinecore.ui

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

    inner class HabitViewHolder(private val binding: ItemHabitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit) {
            binding.apply {
                tvEmoji.text = habit.emoji
                tvHabitName.text = habit.name
                tvStreak.text = if (habit.streak > 0) "🔥 ${habit.streak}" else "—"
                tvTotal.text = "${habit.totalCompletions}x done"

                // Checkbox state without triggering listener
                cbDone.setOnCheckedChangeListener(null)
                cbDone.isChecked = habit.isCompletedToday

                // Visual state for completed habit
                root.alpha = if (habit.isCompletedToday) 0.75f else 1.0f
                tvHabitName.paintFlags = if (habit.isCompletedToday) {
                    tvHabitName.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    tvHabitName.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }

                cbDone.setOnCheckedChangeListener { _, _ -> onToggle(habit) }
                root.setOnLongClickListener {
                    onLongPress(habit)
                    true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Habit>() {
            override fun areItemsTheSame(oldItem: Habit, newItem: Habit) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Habit, newItem: Habit) = oldItem == newItem
        }
    }
}
