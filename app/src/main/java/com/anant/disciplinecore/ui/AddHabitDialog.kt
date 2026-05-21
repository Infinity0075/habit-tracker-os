package com.anant.disciplinecore.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.anant.disciplinecore.data.Habit
import com.anant.disciplinecore.databinding.DialogAddHabitBinding
import com.anant.disciplinecore.viewmodel.HabitViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddHabitDialog : BottomSheetDialogFragment() {

    private var _binding: DialogAddHabitBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HabitViewModel by activityViewModels()

    // ── Emoji options ─────────────────────────────────────────────
    private val emojiOptions = listOf(
        "⚡", "💪", "📚", "🧘", "🏃", "💧", "🥗", "😴",
        "🎯", "✍️", "🧠", "🎵", "🚴", "🌅", "🧹", "📵"
    )
    private var selectedEmoji = "⚡"

    // ── Category options ──────────────────────────────────────────
    data class Category(val label: String, val emoji: String)

    private val categories = listOf(
        Category("General",  "🗂️"),
        Category("Health",   "❤️"),
        Category("Fitness",  "🏋️"),
        Category("Mind",     "🧠"),
        Category("Work",     "💼"),
        Category("Finance",  "💰"),
        Category("Social",   "👥"),
        Category("Creative", "🎨")
    )
    private var selectedCategory = "General"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddHabitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEmojiGrid()
        setupCategoryChips()
        setupButtons()
    }

    // ── Emoji grid ────────────────────────────────────────────────
    private fun setupEmojiGrid() {
        binding.emojiGrid.removeAllViews()

        emojiOptions.forEach { emoji ->
            val tv = TextView(requireContext()).apply {
                text     = emoji
                textSize = 24f
                gravity  = Gravity.CENTER
                setPadding(20, 20, 20, 20)
                background = if (emoji == selectedEmoji) selectedBg() else null
                setOnClickListener {
                    selectedEmoji = emoji
                    setupEmojiGrid()
                }
            }

            val params = GridLayout.LayoutParams().apply {
                width      = 0
                height     = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
            binding.emojiGrid.addView(tv, params)
        }
    }

    // ── Category chips ────────────────────────────────────────────
    private fun setupCategoryChips() {
        val container = binding.layoutCategories
        container.removeAllViews()
        val density = resources.displayMetrics.density

        categories.forEach { category ->
            val isSelected = category.label == selectedCategory

            val chip = TextView(requireContext()).apply {
                text     = "${category.emoji} ${category.label}"
                textSize = 12f
                gravity  = Gravity.CENTER
                setPadding(
                    (14 * density).toInt(), (8 * density).toInt(),
                    (14 * density).toInt(), (8 * density).toInt()
                )
                setTextColor(
                    if (isSelected) Color.parseColor("#0F0F11")
                    else Color.parseColor("#F5F5F0")
                )
                background = GradientDrawable().apply {
                    cornerRadius = 50f * density
                    setColor(
                        if (isSelected) Color.parseColor("#F59E0B")
                        else Color.parseColor("#2A2A35")
                    )
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.marginEnd = (8 * density).toInt() }

                setOnClickListener {
                    selectedCategory = category.label
                    setupCategoryChips()
                }
            }
            container.addView(chip)
        }
    }

    // ── Buttons ───────────────────────────────────────────────────
    private fun setupButtons() {
        binding.btnAdd.setOnClickListener {
            val name = binding.etHabitName.text.toString().trim()

            if (name.isEmpty()) {
                binding.etHabitName.error = "Enter habit name"
                return@setOnClickListener
            }

            viewModel.insert(
                Habit(
                    name     = name,
                    emoji    = selectedEmoji,
                    category = selectedCategory
                )
            )
            dismiss()
        }

        binding.btnCancel.setOnClickListener { dismiss() }
    }

    // ── Helper: selected background ───────────────────────────────
    private fun selectedBg() = GradientDrawable().apply {
        setColor(0x33F59E0B.toInt())
        cornerRadius = 16f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}