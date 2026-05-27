package com.anant.disciplinecore.features.home

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
import com.anant.disciplinecore.core.gamification.XPManager
import com.anant.disciplinecore.data.local.entities.Habit
import com.anant.disciplinecore.databinding.FragmentHomeBinding
import com.anant.disciplinecore.features.home.adapter.HabitAdapter
import com.anant.disciplinecore.features.stats.HabitStatsFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    // =================================================
    // BINDING
    // =================================================

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    // =================================================
    // VIEWMODEL
    // =================================================

    private val viewModel: HomeViewModel by activityViewModels()

    // =================================================
    // ADAPTER
    // =================================================

    private lateinit var adapter: HabitAdapter

    // =================================================
    // SWIPE PAINTS
    // =================================================

    private val swipeBgPaint = Paint().apply {
        color = Color.parseColor("#FB7185")
        isAntiAlias = true
    }

    private val swipeIconPaint = Paint().apply {
        color = Color.WHITE
        textSize = 52f
        isAntiAlias = true
    }

    private val swipeLabelPaint = Paint().apply {
        color = Color.WHITE
        textSize = 38f
        isAntiAlias = true
    }

    // =================================================
    // DATE FORMATTER
    // =================================================

    private val dateFormatter = SimpleDateFormat(
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

        _binding = FragmentHomeBinding.inflate(
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

        val hour = Calendar.getInstance()
            .get(Calendar.HOUR_OF_DAY)

        val name = requireContext()
            .getSharedPreferences(
                "settings_prefs",
                Context.MODE_PRIVATE
            )
            .getString(
                "user_name",
                ""
            )
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
            if (!name.isNullOrEmpty()) {
                "$greeting, $name!"
            } else {
                greeting
            }

        binding.tvDate.text =
            dateFormatter.format(Date())
    }

    // =================================================
    // RECYCLER VIEW
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

        binding.rvHabits.apply {

            layoutManager =
                LinearLayoutManager(requireContext())

            adapter =
                this@HomeFragment.adapter

            setHasFixedSize(true)
        }

        buildSwipeToDelete()
            .attachToRecyclerView(binding.rvHabits)
    }

    // =================================================
    // SWIPE TO DELETE
    // =================================================

    private fun buildSwipeToDelete(): ItemTouchHelper {

        val callback =
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

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
                            28f,
                            28f,
                            swipeBgPaint
                        )

                        canvas.drawText(
                            "🗑️",
                            itemView.right - 130f,
                            midY + 10f,
                            swipeIconPaint
                        )

                        canvas.drawText(
                            "Delete",
                            itemView.right - 150f,
                            midY + 55f,
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
                ): Float {
                    return 0.35f
                }

                override fun getSwipeEscapeVelocity(
                    defaultValue: Float
                ): Float {
                    return defaultValue * 3f
                }
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

            .setTitle("Delete Habit?")

            .setMessage(
                "\"${habit.name}\" and its " +
                        "${habit.streak}-day streak " +
                        "will be deleted permanently."
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

                    putInt(
                        "habit_id",
                        habit.id
                    )
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

            val sortedHabits =
                habits.sortedBy {
                    it.isCompletedToday
                }

            adapter.submitList(sortedHabits)

            updateScoreCard(habits)

            binding.tvHabitCount.text =
                "${habits.size} habits"

            val isEmpty =
                habits.isEmpty()

            binding.emptyState.visibility =
                if (isEmpty) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            binding.rvHabits.visibility =
                if (isEmpty) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
        }
    }

    // =================================================
    // OBSERVE DASHBOARD
    // =================================================

    private fun observeDashboard() {

        // =============================================
        // CONSISTENCY
        // =============================================

        viewModel.consistencyMessage.observe(
            viewLifecycleOwner
        ) { message ->

            binding.tvScoreLabel.text =
                message
        }

        // =============================================
        // MOTIVATION
        // =============================================

        viewModel.motivationMessage.observe(
            viewLifecycleOwner
        ) { message ->

            binding.tvInsight.text =
                message
        }

        // =============================================
        // DAILY SCORE
        // =============================================

        viewModel.dailyScore.observe(
            viewLifecycleOwner
        ) { score ->

            updateTier(score)
        }

        // =============================================
        // USER PROGRESS
        // =============================================

        viewModel.userProgress.observe(
            viewLifecycleOwner
        ) { progress ->

            progress ?: return@observe

            binding.xpCard.tvLevel.text =
                progress.level.toString()

            binding.xpCard.tvRank.text =
                XPManager.getRankTitle(
                    progress.level
                )

            binding.xpCard.tvXp.text =
                "${progress.xp} XP"

            binding.xpCard.tvGlobalStreak.text =
                "🔥 ${progress.currentStreak}"

            val nextLevelXp =
                XPManager.xpForNextLevel(
                    progress.level
                )

            val currentLevelXp =
                progress.xp % nextLevelXp

            binding.xpCard.xpProgress.max =
                nextLevelXp

            binding.xpCard.xpProgress.progress =
                currentLevelXp
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

        val completed =
            habits.count {
                it.isCompletedToday
            }

        val percentage =
            if (total > 0) {
                (completed * 100) / total
            } else {
                0
            }

        binding.tvScore.text =
            "$completed / $total"

        binding.tvPct.text =
            "$percentage%"

        binding.progressBar.progress =
            percentage

        val colorRes = when {

            percentage >= 85 ->
                R.color.green

            percentage >= 60 ->
                R.color.accent_amber

            percentage >= 30 ->
                R.color.soft_gold

            else ->
                R.color.red
        }

        val color =
            ContextCompat.getColor(
                requireContext(),
                colorRes
            )

        binding.tvPct.setTextColor(color)

        binding.progressBar.progressTintList =
            ColorStateList.valueOf(color)
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

        binding.tvTier.text =
            tier
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