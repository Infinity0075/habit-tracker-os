package com.anant.disciplinecore.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anant.disciplinecore.R
import com.anant.disciplinecore.data.Habit
import com.anant.disciplinecore.databinding.FragmentHomeBinding
import com.anant.disciplinecore.viewmodel.HabitViewModel
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HabitViewModel by activityViewModels()

    private lateinit var adapter: HabitAdapter

    // =================================================
    // SWIPE PAINTS
    // =================================================

    private val swipeBgPaint = Paint().apply {
        color = Color.parseColor("#FB7185")
    }

    private val swipeIconPaint = Paint().apply {
        color = Color.WHITE
        textSize = 52f
        isAntiAlias = true
    }

    private val swipeLabelPaint = Paint().apply {
        color = Color.WHITE
        textSize = 42f
        isAntiAlias = true
    }

    // =================================================
    // DATE
    // =================================================

    private val dateFormatter =
        SimpleDateFormat(
            "EEEE, d MMMM",
            Locale.getDefault()
        )

    // =================================================
    // CREATE VIEW
    // =================================================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentHomeBinding.inflate(
                inflater,
                container,
                false
            )

        setupHeader()

        setupRecyclerView()

        observeHabits()

        observeDashboard()

        return binding.root
    }

    // =================================================
    // HEADER
    // =================================================

    private fun setupHeader() {

        val hour =
            Calendar.getInstance()
                .get(Calendar.HOUR_OF_DAY)

        val name =
            requireContext()
                .getSharedPreferences(
                    "settings_prefs",
                    Context.MODE_PRIVATE
                )
                .getString("user_name", "")
                ?.trim()

        val greeting = when {

            hour < 12 ->
                "Good Morning ☀️"

            hour < 17 ->
                "Good Afternoon 🌤️"

            else ->
                "Good Evening 🌙"
        }

        binding.tvGreeting.text =
            if (!name.isNullOrEmpty())
                "$greeting, $name!"
            else
                greeting

        binding.tvDate.text =
            dateFormatter.format(Date())
    }

    // =================================================
    // RECYCLER
    // =================================================

    private fun setupRecyclerView() {

        adapter = HabitAdapter(

            onToggle = { habit ->
                viewModel.toggleCompletion(habit)
            },

            onLongPress = { habit ->
                showDeleteDialog(
                    habit,
                    onCancel = null
                )
            },

            onHabitClick = { habit ->
                openHabitStats(habit)
            }
        )

        binding.rvHabits.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvHabits.adapter = adapter

        buildSwipeToDelete()
            .attachToRecyclerView(binding.rvHabits)
    }

    // =================================================
    // SWIPE DELETE
    // =================================================

    private fun buildSwipeToDelete():
            ItemTouchHelper {

        val callback =
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT
            ) {

                override fun onMove(
                    rv: RecyclerView,
                    vh: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ) = false

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {

                    val position =
                        viewHolder.bindingAdapterPosition

                    if (position ==
                        RecyclerView.NO_POSITION
                    ) return

                    val habit =
                        adapter.currentList[position]

                    showDeleteDialog(
                        habit,
                        onCancel = {
                            adapter.notifyItemChanged(position)
                        }
                    )
                }

                override fun onChildDraw(
                    canvas: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {

                    val itemView =
                        viewHolder.itemView

                    val itemHeight =
                        itemView.height.toFloat()

                    val midY =
                        itemView.top + itemHeight / 2f

                    if (dX < 0) {

                        canvas.drawRoundRect(
                            RectF(
                                itemView.right + dX,
                                itemView.top.toFloat(),
                                itemView.right.toFloat(),
                                itemView.bottom.toFloat()
                            ),
                            26f,
                            26f,
                            swipeBgPaint
                        )

                        canvas.drawText(
                            "🗑️",
                            itemView.right - 130f,
                            midY + 20f,
                            swipeIconPaint
                        )

                        canvas.drawText(
                            "Delete",
                            itemView.right - 120f,
                            midY + 60f,
                            swipeLabelPaint
                        )
                    }

                    super.onChildDraw(
                        canvas,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

                override fun getSwipeThreshold(
                    viewHolder: RecyclerView.ViewHolder
                ) = 0.35f

                override fun getSwipeEscapeVelocity(
                    defaultValue: Float
                ) = defaultValue * 3f
            }

        return ItemTouchHelper(callback)
    }

    // =================================================
    // DELETE DIALOG
    // =================================================

    private fun showDeleteDialog(
        habit: Habit,
        onCancel: (() -> Unit)?
    ) {

        AlertDialog.Builder(
            requireContext(),
            R.style.AlertDialogDark
        )

            .setTitle("Delete habit?")

            .setMessage(
                "\"${habit.name}\" and its " +
                        "${habit.streak}-day streak " +
                        "will be deleted."
            )

            .setPositiveButton("Delete") { _, _ ->

                viewModel.delete(habit)
            }

            .setNegativeButton("Cancel") { _, _ ->

                onCancel?.invoke()
            }

            .setOnCancelListener {

                onCancel?.invoke()
            }

            .show()
    }

    // =================================================
    // OPEN STATS
    // =================================================

    private fun openHabitStats(
        habit: Habit
    ) {

        if (!isAdded) return

        val fragment =
            HabitStatsFragment().apply {

                arguments = Bundle().apply {
                    putInt("habit_id", habit.id)
                }
            }

        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                fragment
            )
            .addToBackStack(null)
            .commit()
    }

    // =================================================
    // OBSERVE HABITS
    // =================================================

    private fun observeHabits() {

        viewModel.allHabits.observe(
            viewLifecycleOwner
        ) { habits ->

            val sorted =
                habits.sortedBy {
                    it.isCompletedToday
                }

            adapter.submitList(sorted)

            updateScoreCard(habits)

            binding.tvHabitCount.text =
                "${habits.size} habits"

            val isEmpty =
                habits.isEmpty()

            binding.emptyState.visibility =
                if (isEmpty)
                    View.VISIBLE
                else
                    View.GONE

            binding.rvHabits.visibility =
                if (isEmpty)
                    View.GONE
                else
                    View.VISIBLE
        }
    }

    // =================================================
    // DASHBOARD OBSERVERS
    // =================================================

    private fun observeDashboard() {

        viewModel.consistencyMessage.observe(
            viewLifecycleOwner
        ) {

            binding.tvScoreLabel.text = it
        }

        viewModel.motivationMessage.observe(
            viewLifecycleOwner
        ) {

            binding.tvInsight.text = it
        }

        viewModel.dailyScore.observe(
            viewLifecycleOwner
        ) {

            updateTier(it)
        }
    }

    // =================================================
    // SCORE CARD
    // =================================================

    private fun updateScoreCard(
        habits: List<Habit>
    ) {

        val total =
            habits.size

        val done =
            habits.count {
                it.isCompletedToday
            }

        val pct =
            if (total > 0)
                (done * 100) / total
            else
                0

        binding.tvScore.text =
            "$done / $total"

        binding.tvPct.text =
            "$pct%"

        binding.progressBar.progress =
            pct

        // =============================================
        // COLORS
        // =============================================

        val color = when {

            pct >= 85 ->
                R.color.green

            pct >= 60 ->
                R.color.accent_amber

            pct >= 30 ->
                R.color.soft_gold

            else ->
                R.color.red
        }

        binding.tvPct.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                color
            )
        )

        binding.progressBar.progressTintList =
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    color
                )
            )
    }

    // =================================================
    // TIER SYSTEM
    // =================================================

    private fun updateTier(
        score: Int
    ) {

        val tier = when {

            score >= 95 ->
                "Legendary 🔥"

            score >= 80 ->
                "Locked In ⚡"

            score >= 60 ->
                "Strong Momentum 💪"

            score >= 40 ->
                "Warming Up 🎯"

            score > 0 ->
                "Getting Started 🌱"

            else ->
                "Begin Again"
        }

        binding.tvTier.text = tier
    }

    // =================================================
    // RESUME
    // =================================================

    override fun onResume() {
        super.onResume()

        setupHeader()
    }

    // =================================================
    // DESTROY
    // =================================================

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}