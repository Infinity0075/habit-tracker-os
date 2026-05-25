package com.anant.disciplinecore.ui

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anant.disciplinecore.R
import com.anant.disciplinecore.data.Habit
import com.anant.disciplinecore.databinding.ItemHabitBinding

class HabitAdapter(
    private val onToggle: (Habit) -> Unit,
    private val onLongPress: (Habit) -> Unit,
    private val onHabitClick: (Habit) -> Unit
) : ListAdapter<Habit, HabitAdapter.HabitViewHolder>(DIFF_CALLBACK) {

    // ------------------------------------------------
    // CATEGORY ICONS
    // ------------------------------------------------

    private val categoryEmoji = mapOf(

        "General" to "🗂️",
        "Health" to "❤️",
        "Fitness" to "🏋️",
        "Mind" to "🧠",
        "Work" to "💼",
        "Finance" to "💰",
        "Social" to "👥",
        "Creative" to "🎨"
    )

    // ------------------------------------------------
    // VIEW HOLDER
    // ------------------------------------------------

    inner class HabitViewHolder(
        private val binding: ItemHabitBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit) {

            binding.apply {

                // =====================================
                // BASIC
                // =====================================

                tvEmoji.text = habit.emoji

                tvHabitName.text = habit.name

                tvStreak.text =
                    if (habit.streak > 0)
                        "🔥 ${habit.streak}"
                    else
                        "Start today"

                tvTotal.text =
                    "${habit.totalCompletions} completions"

                // =====================================
                // CATEGORY
                // =====================================

                val catEmoji =
                    categoryEmoji[habit.category] ?: "🗂️"

                tvCategory.text =
                    "$catEmoji ${habit.category}"

                // =====================================
                // CHECKBOX
                // =====================================

                cbDone.setOnCheckedChangeListener(null)

                cbDone.isChecked =
                    habit.isCompletedToday

                // =====================================
                // COMPLETED STATE
                // =====================================

                updateCompletedUI(habit)

                // =====================================
                // STREAK COLOR
                // =====================================

                updateStreakColor(habit.streak)

                // =====================================
                // CONSISTENCY GLOW
                // =====================================

                applyConsistencyGlow(habit)

                // =====================================
                // CHECKBOX LISTENER
                // =====================================

                cbDone.setOnCheckedChangeListener { _, isChecked ->

                    playCompletionAnimation(isChecked)

                    onToggle(habit)
                }

                // =====================================
                // LONG PRESS
                // =====================================

                root.setOnLongClickListener {

                    onLongPress(habit)

                    true
                }

                // =====================================
                // CLICK
                // =====================================

                root.setOnClickListener {

                    onHabitClick(habit)
                }
            }
        }

        // =================================================
        // COMPLETED UI
        // =================================================

        private fun updateCompletedUI(habit: Habit) {

            binding.apply {

                val alpha =
                    if (habit.isCompletedToday) 0.72f
                    else 1f

                root.alpha = alpha

                tvHabitName.paintFlags =
                    if (habit.isCompletedToday) {

                        tvHabitName.paintFlags or
                                Paint.STRIKE_THRU_TEXT_FLAG

                    } else {

                        tvHabitName.paintFlags and
                                Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }

                tvHabitName.animate()
                    .alpha(if (habit.isCompletedToday) 0.65f else 1f)
                    .setDuration(180)
                    .start()
            }
        }

        // =================================================
        // STREAK COLORS
        // =================================================

        private fun updateStreakColor(streak: Int) {

            val color = when {

                streak >= 30 ->
                    R.color.green

                streak >= 14 ->
                    R.color.accent_amber

                streak >= 7 ->
                    R.color.soft_gold

                else ->
                    R.color.text_secondary
            }

            binding.tvStreak.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    color
                )
            )
        }

        // =================================================
        // CONSISTENCY GLOW
        // =================================================

        private fun applyConsistencyGlow(habit: Habit) {

            val context = binding.root.context

            val background =
                binding.cardHabit.background

            if (background is GradientDrawable) {

                val strokeColor = when {

                    habit.consistencyScore >= 85 ->
                        ContextCompat.getColor(
                            context,
                            R.color.green
                        )

                    habit.consistencyScore >= 60 ->
                        ContextCompat.getColor(
                            context,
                            R.color.accent_amber
                        )

                    else ->
                        ContextCompat.getColor(
                            context,
                            R.color.stroke_light
                        )
                }

                background.setStroke(2, strokeColor)
            }
        }

        // =================================================
        // COMPLETION ANIMATION
        // =================================================

        private fun playCompletionAnimation(
            isCompleted: Boolean
        ) {

            // =========================================
            // CHECKBOX BOUNCE
            // =========================================

            binding.cbDone.animate()
                .scaleX(1.35f)
                .scaleY(1.35f)
                .setDuration(120)
                .withEndAction {

                    binding.cbDone.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .duration = 120
                }

            // =========================================
            // CARD PRESS
            // =========================================

            binding.cardHabit.animate()
                .scaleX(if (isCompleted) 0.97f else 1f)
                .scaleY(if (isCompleted) 0.97f else 1f)
                .setDuration(100)
                .withEndAction {

                    binding.cardHabit.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .duration = 120
                }

            // =========================================
            // ALPHA
            // =========================================

            binding.root.animate()
                .alpha(if (isCompleted) 0.72f else 1f)
                .setDuration(180)
                .start()

            // =========================================
            // COLOR FLASH
            // =========================================

            val from =
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.bg_card
                )

            val to =
                ContextCompat.getColor(
                    binding.root.context,
                    if (isCompleted)
                        R.color.card_highlight
                    else
                        R.color.bg_card
                )

            val animator =
                ValueAnimator.ofObject(
                    ArgbEvaluator(),
                    from,
                    to
                )

            animator.duration = 220

            animator.addUpdateListener {

                val color =
                    it.animatedValue as Int

                binding.cardHabit.setCardBackgroundColor(color)
            }

            animator.start()
        }
    }

    // ------------------------------------------------
    // CREATE HOLDER
    // ------------------------------------------------

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HabitViewHolder {

        val binding =
            ItemHabitBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return HabitViewHolder(binding)
    }

    // ------------------------------------------------
    // BIND
    // ------------------------------------------------

    override fun onBindViewHolder(
        holder: HabitViewHolder,
        position: Int
    ) {

        holder.bind(getItem(position))
    }

    // ------------------------------------------------
    // DIFF
    // ------------------------------------------------

    companion object {

        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Habit>() {

                override fun areItemsTheSame(
                    old: Habit,
                    new: Habit
                ) = old.id == new.id

                override fun areContentsTheSame(
                    old: Habit,
                    new: Habit
                ) = old == new
            }
    }
}