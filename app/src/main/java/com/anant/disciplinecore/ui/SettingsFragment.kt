package com.anant.disciplinecore.ui

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
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.anant.disciplinecore.R
import com.anant.disciplinecore.viewmodel.HabitViewModel
import com.anant.disciplinecore.worker.MidnightResetReceiver
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class SettingsFragment : Fragment() {

    private val viewModel: HabitViewModel by viewModels()

    // Views
    private lateinit var etUserName: TextInputEditText
    private lateinit var btnSaveName: Button
    private lateinit var switchDailyReminder: SwitchMaterial
    private lateinit var switchMidnightReset: SwitchMaterial
    private lateinit var layoutReminderTime: View
    private lateinit var btnPickTime: Button
    private lateinit var btnResetData: Button

    // Pomodoro views
    private lateinit var tvFocusMin: TextView
    private lateinit var tvShortBreakMin: TextView
    private lateinit var tvLongBreakMin: TextView
    private lateinit var tvFocusDuration: TextView
    private lateinit var tvShortBreakDuration: TextView
    private lateinit var tvLongBreakDuration: TextView

    // Pomodoro values
    private var focusMin      = 25
    private var shortBreakMin = 5
    private var longBreakMin  = 15

    // Prefs
    private val PREFS = "settings_prefs"
    private val KEY_NAME          = "user_name"
    private val KEY_REMINDER      = "daily_reminder"
    private val KEY_REMINDER_HOUR = "reminder_hour"
    private val KEY_REMINDER_MIN  = "reminder_min"
    private val KEY_MIDNIGHT      = "midnight_reset"
    private val KEY_FOCUS         = "focus_min"
    private val KEY_SHORT_BREAK   = "short_break_min"
    private val KEY_LONG_BREAK    = "long_break_min"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        loadSettings()
        setupListeners()
    }

    private fun bindViews(view: View) {
        etUserName            = view.findViewById(R.id.etUserName)
        btnSaveName           = view.findViewById(R.id.btnSaveName)
        switchDailyReminder   = view.findViewById(R.id.switchDailyReminder)
        switchMidnightReset   = view.findViewById(R.id.switchMidnightReset)
        layoutReminderTime    = view.findViewById(R.id.layoutReminderTime)
        btnPickTime           = view.findViewById(R.id.btnPickTime)
        btnResetData          = view.findViewById(R.id.btnResetData)
        tvFocusMin            = view.findViewById(R.id.tvFocusMin)
        tvShortBreakMin       = view.findViewById(R.id.tvShortBreakMin)
        tvLongBreakMin        = view.findViewById(R.id.tvLongBreakMin)
        tvFocusDuration       = view.findViewById(R.id.tvFocusDuration)
        tvShortBreakDuration  = view.findViewById(R.id.tvShortBreakDuration)
        tvLongBreakDuration   = view.findViewById(R.id.tvLongBreakDuration)
    }

    // ── Load saved settings ──────────────────────────────────────
    private fun loadSettings() {
        val prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        etUserName.setText(prefs.getString(KEY_NAME, ""))

        val reminderOn = prefs.getBoolean(KEY_REMINDER, true)
        switchDailyReminder.isChecked  = reminderOn
        layoutReminderTime.visibility  =
            if (reminderOn) View.VISIBLE else View.GONE

        switchMidnightReset.isChecked = prefs.getBoolean(KEY_MIDNIGHT, true)

        val hour = prefs.getInt(KEY_REMINDER_HOUR, 8)
        val min  = prefs.getInt(KEY_REMINDER_MIN, 0)
        btnPickTime.text = formatTime(hour, min)

        focusMin      = prefs.getInt(KEY_FOCUS, 25)
        shortBreakMin = prefs.getInt(KEY_SHORT_BREAK, 5)
        longBreakMin  = prefs.getInt(KEY_LONG_BREAK, 15)
        updatePomodoroUI()
    }

    // ── Listeners ────────────────────────────────────────────────
    private fun setupListeners() {

        // Save name
        btnSaveName.setOnClickListener {
            val name = etUserName.text.toString().trim()
            saveToPrefs(KEY_NAME, name)
            Toast.makeText(requireContext(),
                "✅ Name saved!", Toast.LENGTH_SHORT).show()
        }

        // Daily reminder toggle
        switchDailyReminder.setOnCheckedChangeListener { _, checked ->
            saveToPrefs(KEY_REMINDER, checked)
            layoutReminderTime.visibility =
                if (checked) View.VISIBLE else View.GONE
            if (checked) scheduleReminder() else cancelReminder()
        }

        // Midnight reset toggle
        switchMidnightReset.setOnCheckedChangeListener { _, checked ->
            saveToPrefs(KEY_MIDNIGHT, checked)
            Toast.makeText(requireContext(),
                if (checked) "Midnight reset ON" else "Midnight reset OFF",
                Toast.LENGTH_SHORT).show()
        }

        // Time picker
        btnPickTime.setOnClickListener {
            val prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val hour  = prefs.getInt(KEY_REMINDER_HOUR, 8)
            val min   = prefs.getInt(KEY_REMINDER_MIN, 0)

            TimePickerDialog(requireContext(), { _, h, m ->
                saveToPrefs(KEY_REMINDER_HOUR, h)
                saveToPrefs(KEY_REMINDER_MIN, m)
                btnPickTime.text = formatTime(h, m)
                if (switchDailyReminder.isChecked) scheduleReminder()
            }, hour, min, false).show()
        }

        // Focus +/-
        view?.findViewById<Button>(R.id.btnFocusPlus)?.setOnClickListener {
            if (focusMin < 60) { focusMin += 5; updatePomodoroUI(); savePomodoroSettings() }
        }
        view?.findViewById<Button>(R.id.btnFocusMinus)?.setOnClickListener {
            if (focusMin > 5)  { focusMin -= 5; updatePomodoroUI(); savePomodoroSettings() }
        }

        // Short break +/-
        view?.findViewById<Button>(R.id.btnShortBreakPlus)?.setOnClickListener {
            if (shortBreakMin < 30) { shortBreakMin += 1; updatePomodoroUI(); savePomodoroSettings() }
        }
        view?.findViewById<Button>(R.id.btnShortBreakMinus)?.setOnClickListener {
            if (shortBreakMin > 1)  { shortBreakMin -= 1; updatePomodoroUI(); savePomodoroSettings() }
        }

        // Long break +/-
        view?.findViewById<Button>(R.id.btnLongBreakPlus)?.setOnClickListener {
            if (longBreakMin < 60) { longBreakMin += 5; updatePomodoroUI(); savePomodoroSettings() }
        }
        view?.findViewById<Button>(R.id.btnLongBreakMinus)?.setOnClickListener {
            if (longBreakMin > 5)  { longBreakMin -= 5; updatePomodoroUI(); savePomodoroSettings() }
        }

        // Reset all data
        btnResetData.setOnClickListener { confirmReset() }
    }

    // ── Pomodoro UI ──────────────────────────────────────────────
    private fun updatePomodoroUI() {
        tvFocusMin.text          = focusMin.toString()
        tvShortBreakMin.text     = shortBreakMin.toString()
        tvLongBreakMin.text      = longBreakMin.toString()
        tvFocusDuration.text     = "$focusMin minutes"
        tvShortBreakDuration.text = "$shortBreakMin minutes"
        tvLongBreakDuration.text  = "$longBreakMin minutes"
    }

    private fun savePomodoroSettings() {
        val prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(KEY_FOCUS, focusMin)
            .putInt(KEY_SHORT_BREAK, shortBreakMin)
            .putInt(KEY_LONG_BREAK, longBreakMin)
            .apply()
    }

    // ── Reminder scheduling ──────────────────────────────────────
    private fun scheduleReminder() {
        val prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val hour  = prefs.getInt(KEY_REMINDER_HOUR, 8)
        val min   = prefs.getInt(KEY_REMINDER_MIN, 0)

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, min)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DATE, 1)
        }

        val intent = Intent(requireContext(), MidnightResetReceiver::class.java)
            .apply { action = "DAILY_REMINDER" }

        val pi = PendingIntent.getBroadcast(
            requireContext(), 1001, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val am = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.setRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis,
            AlarmManager.INTERVAL_DAY, pi)

        Toast.makeText(requireContext(),
            "⏰ Reminder set for ${formatTime(hour, min)}", Toast.LENGTH_SHORT).show()
    }

    private fun cancelReminder() {
        val intent = Intent(requireContext(), MidnightResetReceiver::class.java)
            .apply { action = "DAILY_REMINDER" }
        val pi = PendingIntent.getBroadcast(
            requireContext(), 1001, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val am = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pi)
    }

    // ── Reset all data ───────────────────────────────────────────
    private fun confirmReset() {
        AlertDialog.Builder(requireContext())
            .setTitle("Reset All Data")
            .setMessage("This will delete ALL habits, journal entries, and streaks. This cannot be undone.")
            .setPositiveButton("Reset") { _, _ ->
                viewModel.deleteAllHabits()
                requireContext()
                    .getSharedPreferences("journal_prefs", Context.MODE_PRIVATE)
                    .edit().clear().apply()
                Toast.makeText(requireContext(),
                    "🗑️ All data reset.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ── Helpers ──────────────────────────────────────────────────
    private fun formatTime(hour: Int, min: Int): String {
        val amPm  = if (hour < 12) "AM" else "PM"
        val h     = if (hour % 12 == 0) 12 else hour % 12
        return String.format("%d:%02d %s", h, min, amPm)
    }

    private fun saveToPrefs(key: String, value: String) {
        requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(key, value).apply()
    }

    private fun saveToPrefs(key: String, value: Boolean) {
        requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(key, value).apply()
    }

    private fun saveToPrefs(key: String, value: Int) {
        requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putInt(key, value).apply()
    }
}