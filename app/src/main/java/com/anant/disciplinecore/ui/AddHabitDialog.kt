package com.anant.disciplinecore.ui

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
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

    private val emojiOptions = listOf(
        "⚡", "💪", "📚", "🧘", "🏃", "💧", "🥗", "😴",
        "🎯", "✍️", "🧠", "🎵", "🚴", "🌅", "🧹", "📵"
    )

    private var selectedEmoji = "⚡"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DialogAddHabitBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        setupEmojiGrid()

        binding.btnAdd.setOnClickListener {

            val name =
                binding.etHabitName.text
                    .toString()
                    .trim()

            if (name.isEmpty()) {

                binding.etHabitName.error =
                    "Enter habit name"

                return@setOnClickListener
            }

            val habit = Habit(
                name = name,
                emoji = selectedEmoji
            )

            viewModel.insert(habit)

            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupEmojiGrid() {

        binding.emojiGrid.removeAllViews()

        emojiOptions.forEach { emoji ->

            val tv = TextView(requireContext()).apply {

                text = emoji

                textSize = 24f

                gravity = Gravity.CENTER

                setPadding(20, 20, 20, 20)

                background =
                    if (emoji == selectedEmoji) {

                        GradientDrawable().apply {

                            setColor(0x33F59E0B.toInt())

                            cornerRadius = 16f
                        }

                    } else {
                        null
                    }

                setOnClickListener {

                    selectedEmoji = emoji

                    setupEmojiGrid()
                }
            }

            val params =
                GridLayout.LayoutParams().apply {

                    width = 0

                    height =
                        GridLayout.LayoutParams.WRAP_CONTENT

                    columnSpec =
                        GridLayout.spec(
                            GridLayout.UNDEFINED,
                            1f
                        )
                }

            binding.emojiGrid.addView(tv, params)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}