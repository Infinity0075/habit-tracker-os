package com.anant.disciplinecore.features.settings

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.anant.disciplinecore.R
import com.anant.disciplinecore.core.preferences.PreferenceManager
import com.anant.disciplinecore.features.home.HomeViewModel
import com.anant.disciplinecore.worker.MidnightResetReceiver
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class SettingsFragment : Fragment() {

    // ─── ViewModel ────────────────────────────────────────────────────────────
    private val viewModel: HomeViewModel by viewModels()

    // ─── Prefs ────────────────────────────────────────────────────────────────
    private lateinit var prefs: PreferenceManager

    // ─── Profile views ────────────────────────────────────────────────────────
    private lateinit var etUserName: TextInputEditText
    private lateinit var btnSaveName: Button
    private lateinit var tvHeroName: TextView
    private lateinit var tvProfileInitial: TextView

    // ─── Dynamic stats ────────────────────────────────────────────────────────
    private lateinit var tvSettingsStreak: TextView
    private lateinit var tvSettingsFocus: TextView
    private lateinit var tvSettingsScore: TextView

    // ─── Notification views ───────────────────────────────────────────────────
    private lateinit var switchDailyReminder: SwitchMaterial
    private lateinit var switchMidnightReset: SwitchMaterial
    private lateinit var layoutReminderTime: View
    private lateinit var btnPickTime: Button

    // ─── Focus views ──────────────────────────────────────────────────────────
    private lateinit var tvFocusMin: TextView
    private lateinit var tvShortBreakMin: TextView
    private lateinit var tvLongBreakMin: TextView
    private lateinit var tvFocusDuration: TextView
    private lateinit var tvShortBreakDuration: TextView
    private lateinit var tvLongBreakDuration: TextView

    // ─── Reset view ───────────────────────────────────────────────────────────
    private lateinit var btnResetData: Button

    // ─── Pomodoro state ───────────────────────────────────────────────────────
    private var focusMin = 25
    private var shortBreakMin = 5
    private var longBreakMin = 15

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = PreferenceManager(requireContext())

        bindViews(view)
        loadSettings()
        loadDynamicStats()
        setupListeners()
        animateScreenEntrance(view)
    }

    // ─── Bind views ───────────────────────────────────────────────────────────

    private fun bindViews(view: View) {

        etUserName = view.findViewById(R.id.etUserName)
        btnSaveName = view.findViewById(R.id.btnSaveName)
        tvHeroName = view.findViewById(R.id.tvHeroName)
        tvProfileInitial = view.findViewById(R.id.tvProfileInitial)

        // Dynamic stats
        tvSettingsStreak = view.findViewById(R.id.tvSettingsStreak)
        tvSettingsFocus = view.findViewById(R.id.tvSettingsFocus)
        tvSettingsScore = view.findViewById(R.id.tvSettingsScore)

        switchDailyReminder = view.findViewById(R.id.switchDailyReminder)
        switchMidnightReset = view.findViewById(R.id.switchMidnightReset)
        layoutReminderTime = view.findViewById(R.id.layoutReminderTime)
        btnPickTime = view.findViewById(R.id.btnPickTime)

        tvFocusMin = view.findViewById(R.id.tvFocusMin)
        tvShortBreakMin = view.findViewById(R.id.tvShortBreakMin)
        tvLongBreakMin = view.findViewById(R.id.tvLongBreakMin)
        tvFocusDuration = view.findViewById(R.id.tvFocusDuration)
        tvShortBreakDuration = view.findViewById(R.id.tvShortBreakDuration)
        tvLongBreakDuration = view.findViewById(R.id.tvLongBreakDuration)

        btnResetData = view.findViewById(R.id.btnResetData)
    }

    // ─── Load saved settings ──────────────────────────────────────────────────

    private fun loadSettings() {

        val savedName = prefs.getUserName()
        etUserName.setText(savedName)
        updateProfileUI(savedName)

        val reminderEnabled = prefs.isDailyReminderEnabled()
        switchDailyReminder.isChecked = reminderEnabled
        layoutReminderTime.visibility =
            if (reminderEnabled) View.VISIBLE else View.GONE

        switchMidnightReset.isChecked =
            prefs.isMidnightResetEnabled()

        val hour = prefs.getReminderHour()
        val minute = prefs.getReminderMinute()

        btnPickTime.text = formatTime(hour, minute)

        focusMin = prefs.getFocusMinutes()
        shortBreakMin = prefs.getShortBreak()
        longBreakMin = prefs.getLongBreak()

        updatePomodoroUI()
    }

    // ─── Dynamic Stats ────────────────────────────────────────────────────────

    private fun loadDynamicStats() {

        val streak = prefs.getCurrentStreak()

        val totalFocusMinutes = prefs.getTotalFocusMinutes()
        val focusHours = totalFocusMinutes / 60

        val completedDays = prefs.getCompletedDays()
        val totalDays = prefs.getTotalTrackedDays()

        val score =
            if (totalDays == 0) 0
            else (
                    (completedDays.toFloat() /
                            totalDays.toFloat()) * 100
                    ).toInt()

        tvSettingsStreak.text = streak.toString()
        tvSettingsFocus.text = "${focusHours}h"
        tvSettingsScore.text = "$score%"
    }

    // ─── Listeners ────────────────────────────────────────────────────────────

    private fun setupListeners() {

        // Save profile
        btnSaveName.setOnClickListener {

            val name = etUserName.text.toString().trim()

            if (name.isBlank()) {
                showToast("Name can't be empty")
                return@setOnClickListener
            }

            prefs.saveUserName(name)
            updateProfileUI(name)

            pulseView(btnSaveName)

            showToast("Identity updated ✓")
        }

        // Daily reminder toggle
        switchDailyReminder.setOnCheckedChangeListener { _, checked ->

            prefs.setDailyReminderEnabled(checked)

            layoutReminderTime.visibility =
                if (checked) View.VISIBLE else View.GONE

            if (checked) {
                scheduleReminder()
            } else {
                cancelReminder()
            }
        }

        // Midnight reset toggle
        switchMidnightReset.setOnCheckedChangeListener { _, checked ->

            prefs.setMidnightResetEnabled(checked)

            showToast(
                if (checked)
                    "Midnight reset enabled"
                else
                    "Midnight reset disabled"
            )
        }

        // Time picker
        btnPickTime.setOnClickListener {

            val hour = prefs.getReminderHour()
            val minute = prefs.getReminderMinute()

            TimePickerDialog(
                requireContext(),
                { _, h, m ->

                    prefs.saveReminderHour(h)
                    prefs.saveReminderMinute(m)

                    btnPickTime.text = formatTime(h, m)

                    if (switchDailyReminder.isChecked) {
                        scheduleReminder()
                    }
                },
                hour,
                minute,
                false
            ).show()
        }

        // Focus +/-
        view?.findViewById<Button>(R.id.btnFocusPlus)
            ?.setOnClickListener {

                if (focusMin < 60) {
                    focusMin += 5
                    syncPomodoro(it)
                }
            }

        view?.findViewById<Button>(R.id.btnFocusMinus)
            ?.setOnClickListener {

                if (focusMin > 5) {
                    focusMin -= 5
                    syncPomodoro(it)
                }
            }

        // Short break +/-
        view?.findViewById<Button>(R.id.btnShortBreakPlus)
            ?.setOnClickListener {

                if (shortBreakMin < 30) {
                    shortBreakMin += 1
                    syncPomodoro(it)
                }
            }

        view?.findViewById<Button>(R.id.btnShortBreakMinus)
            ?.setOnClickListener {

                if (shortBreakMin > 1) {
                    shortBreakMin -= 1
                    syncPomodoro(it)
                }
            }

        // Long break +/-
        view?.findViewById<Button>(R.id.btnLongBreakPlus)
            ?.setOnClickListener {

                if (longBreakMin < 60) {
                    longBreakMin += 5
                    syncPomodoro(it)
                }
            }

        view?.findViewById<Button>(R.id.btnLongBreakMinus)
            ?.setOnClickListener {

                if (longBreakMin > 5) {
                    longBreakMin -= 5
                    syncPomodoro(it)
                }
            }

        // Reset
        btnResetData.setOnClickListener {
            confirmReset()
        }
    }

    // ─── Profile UI ───────────────────────────────────────────────────────────

    private fun updateProfileUI(name: String) {

        val finalName =
            name.ifBlank { "Discipline Builder" }

        tvHeroName.text = finalName
        tvProfileInitial.text =
            finalName.first().uppercase()
    }

    // ─── Pomodoro UI ──────────────────────────────────────────────────────────

    private fun updatePomodoroUI() {

        tvFocusMin.text = focusMin.toString()
        tvShortBreakMin.text = shortBreakMin.toString()
        tvLongBreakMin.text = longBreakMin.toString()

        tvFocusDuration.text = "$focusMin minutes"
        tvShortBreakDuration.text =
            "$shortBreakMin minutes"

        tvLongBreakDuration.text =
            "$longBreakMin minutes"
    }

    private fun savePomodoroSettings() {

        prefs.saveFocusMinutes(focusMin)
        prefs.saveShortBreak(shortBreakMin)
        prefs.saveLongBreak(longBreakMin)
    }

    private fun syncPomodoro(tappedView: View) {

        updatePomodoroUI()
        savePomodoroSettings()

        pulseView(tappedView)
    }

    // ─── Alarm helpers ────────────────────────────────────────────────────────

    private fun scheduleReminder() {

        val hour = prefs.getReminderHour()
        val minute = prefs.getReminderMinute()

        val calendar = Calendar.getInstance().apply {

            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        val intent =
            Intent(requireContext(), MidnightResetReceiver::class.java).apply {
                action = "DAILY_REMINDER"
            }

        val pendingIntent =
            PendingIntent.getBroadcast(
                requireContext(),
                1001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val alarmManager =
            requireContext().getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        showToast(
            "Reminder set for ${
                formatTime(hour, minute)
            }"
        )
    }

    private fun cancelReminder() {

        val intent =
            Intent(requireContext(), MidnightResetReceiver::class.java).apply {
                action = "DAILY_REMINDER"
            }

        val pendingIntent =
            PendingIntent.getBroadcast(
                requireContext(),
                1001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        (
                requireContext().getSystemService(
                    Context.ALARM_SERVICE
                ) as AlarmManager
                ).cancel(pendingIntent)
    }

    // ─── Reset ────────────────────────────────────────────────────────────────

    private fun confirmReset() {

        AlertDialog.Builder(requireContext())
            .setTitle("Reset DisciplineCore?")
            .setMessage(
                "This permanently deletes habits, streaks, focus sessions, and journal progress. There is no undo."
            )
            .setPositiveButton("Erase Everything") { _, _ ->

                viewModel.deleteAllHabits()

                requireContext()
                    .getSharedPreferences(
                        "journal_prefs",
                        Context.MODE_PRIVATE
                    )
                    .edit()
                    .clear()
                    .apply()

                prefs.clearAll()

                loadSettings()
                loadDynamicStats()

                showToast("All data removed")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ─── Animations ───────────────────────────────────────────────────────────

    private fun animateScreenEntrance(root: View) {

        val container = root as? ViewGroup ?: return

        val scrollChild =
            (container as? android.widget.ScrollView)
                ?.getChildAt(0) as? ViewGroup ?: return

        for (i in 0 until scrollChild.childCount) {

            val child = scrollChild.getChildAt(i)

            child.alpha = 0f
            child.translationY = 28f

            child.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(420)
                .setInterpolator(
                    DecelerateInterpolator(2.2f)
                )
                .setStartDelay((60 + i * 45).toLong())
                .start()
        }
    }

    private fun pulseView(view: View) {

        val scaleX =
            ObjectAnimator.ofFloat(
                view,
                "scaleX",
                1f,
                1.12f,
                1f
            )

        val scaleY =
            ObjectAnimator.ofFloat(
                view,
                "scaleY",
                1f,
                1.12f,
                1f
            )

        AnimatorSet().apply {

            playTogether(scaleX, scaleY)

            duration = 220

            interpolator =
                OvershootInterpolator(2.5f)

        }.start()
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private fun formatTime(
        hour: Int,
        minute: Int
    ): String {

        val amPm =
            if (hour < 12) "AM" else "PM"

        val formattedHour =
            if (hour % 12 == 0) 12 else hour % 12

        return String.format(
            "%d:%02d %s",
            formattedHour,
            minute,
            amPm
        )
    }

    private fun showToast(message: String) {

        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}