package com.anant.disciplinecore.features.focus

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.anant.disciplinecore.R
import com.anant.disciplinecore.core.preferences.PreferenceManager
import com.anant.disciplinecore.core.ui.animations.CardAnimations
import com.anant.disciplinecore.core.ui.components.FocusTimerView
import com.anant.disciplinecore.core.utils.TimeUtils
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class FocusFragment : Fragment() {

    private var countDownTimer: CountDownTimer? = null

    private lateinit var prefs: PreferenceManager

    private lateinit var progressRepository:
            com.anant.disciplinecore
            .data.repository.UserProgressRepository

    private var isRunning = false
    private var isPomodoro = true
    private var isFocusSession = true

    private var sessionCount = 1
    private val maxSessions = 4

    private var focusMinutes = 25
    private var shortBreakMinutes = 5
    private var longBreakMinutes = 15

    private var totalMillis = 25 * 60 * 1000L
    private var remainingMillis = totalMillis

    // Views
    private lateinit var focusTimerView: FocusTimerView

    private lateinit var tvSessionLabel: TextView
    private lateinit var tvSessionCount: TextView

    private lateinit var btnStartPause: Button
    private lateinit var btnReset: Button
    private lateinit var btnSkip: Button

    private lateinit var rgTimerMode: RadioGroup
    private lateinit var layoutCustomTime: View
    private lateinit var etCustomMinutes: EditText

    private lateinit var tvQuote: TextView
    private lateinit var tvQuoteAuthor: TextView
    private lateinit var btnNewQuote: Button

    // Quotes
    private val quotes = listOf(
        Pair(
            "Discipline is choosing between what you want now and what you want most.",
            "Abraham Lincoln"
        ),
        Pair(
            "Focus on being productive instead of busy.",
            "Tim Ferriss"
        ),
        Pair(
            "One day or day one. You decide.",
            "Unknown"
        ),
        Pair(
            "Success is the sum of small efforts repeated day in and day out.",
            "Robert Collier"
        ),
        Pair(
            "Push yourself, because no one else is going to do it for you.",
            "Unknown"
        ),
        Pair(
            "The pain of discipline is far less than the pain of regret.",
            "Unknown"
        )
    )

    private var currentQuoteIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_focus,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        prefs = PreferenceManager(requireContext())

        val db =
            com.anant.disciplinecore
                .data.local.database.HabitDatabase
                .getDatabase(requireContext())

        progressRepository =
            com.anant.disciplinecore
                .data.repository
                .UserProgressRepository(
                    db.userProgressDao()
                )

        loadPreferences()

        bindViews(view)

        setupQuotes()

        setupModeSelector()

        setupButtons()

        updateTimerDisplay()

        CardAnimations.popIn(focusTimerView)
    }

    // =========================================================
    // PREFERENCES
    // =========================================================

    private fun loadPreferences() {

        focusMinutes =
            prefs.getFocusMinutes()

        shortBreakMinutes =
            prefs.getShortBreak()

        longBreakMinutes =
            prefs.getLongBreak()

        totalMillis =
            focusMinutes * 60 * 1000L

        remainingMillis = totalMillis
    }

    // =========================================================
    // BIND VIEWS
    // =========================================================

    private fun bindViews(
        view: View
    ) {

        focusTimerView =
            view.findViewById(R.id.focusTimerView)

        tvSessionLabel =
            view.findViewById(R.id.tvSessionLabel)

        tvSessionCount =
            view.findViewById(R.id.tvSessionCount)

        btnStartPause =
            view.findViewById(R.id.btnStartPause)

        btnReset =
            view.findViewById(R.id.btnReset)

        btnSkip =
            view.findViewById(R.id.btnSkip)

        rgTimerMode =
            view.findViewById(R.id.rgTimerMode)

        layoutCustomTime =
            view.findViewById(R.id.layoutCustomTime)

        etCustomMinutes =
            view.findViewById(R.id.etCustomMinutes)

        tvQuote =
            view.findViewById(R.id.tvQuote)

        tvQuoteAuthor =
            view.findViewById(R.id.tvQuoteAuthor)

        btnNewQuote =
            view.findViewById(R.id.btnNewQuote)
    }

    // =========================================================
    // QUOTES
    // =========================================================

    private fun setupQuotes() {

        currentQuoteIndex =
            quotes.indices.random()

        showQuote(currentQuoteIndex)

        btnNewQuote.setOnClickListener {

            CardAnimations.press(btnNewQuote)

            currentQuoteIndex++

            if (currentQuoteIndex >= quotes.size) {
                currentQuoteIndex = 0
            }

            showQuote(currentQuoteIndex)
        }
    }

    private fun showQuote(
        index: Int
    ) {

        val quote = quotes[index]

        tvQuote.text =
            "\"${quote.first}\""

        tvQuoteAuthor.text =
            "— ${quote.second}"
    }

    // =========================================================
    // MODE SELECTOR
    // =========================================================

    private fun setupModeSelector() {

        rgTimerMode.setOnCheckedChangeListener { _, checkedId ->

            isPomodoro =
                checkedId == R.id.rbPomodoro

            layoutCustomTime.visibility =
                if (isPomodoro)
                    View.GONE
                else
                    View.VISIBLE

            resetTimer()
        }
    }

    // =========================================================
    // BUTTONS
    // =========================================================

    private fun setupButtons() {

        btnStartPause.setOnClickListener {

            CardAnimations.press(btnStartPause)

            if (isRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        btnReset.setOnClickListener {

            CardAnimations.press(btnReset)

            resetTimer()
        }

        btnSkip.setOnClickListener {

            CardAnimations.press(btnSkip)

            skipSession()
        }
    }

    // =========================================================
    // START TIMER
    // =========================================================

    private fun startTimer() {

        // CUSTOM MODE
        if (!isPomodoro && remainingMillis == totalMillis) {

            val mins =
                etCustomMinutes.text.toString()
                    .trim()
                    .toIntOrNull()

            if (mins == null || mins <= 0) {

                Toast.makeText(
                    requireContext(),
                    "Enter valid minutes",
                    Toast.LENGTH_SHORT
                ).show()

                return
            }

            totalMillis = mins * 60 * 1000L
            remainingMillis = totalMillis
        }

        isRunning = true

        btnStartPause.text = "Pause"

        countDownTimer = object : CountDownTimer(
            remainingMillis,
            1000
        ) {

            override fun onTick(
                millisUntilFinished: Long
            ) {

                remainingMillis =
                    millisUntilFinished

                updateTimerDisplay()
            }

            override fun onFinish() {

                remainingMillis = 0L

                updateTimerDisplay()

                isRunning = false

                onSessionFinished()
            }

        }.start()
    }

    // =========================================================
    // PAUSE
    // =========================================================

    private fun pauseTimer() {

        countDownTimer?.cancel()

        isRunning = false

        btnStartPause.text = "Resume"
    }

    // =========================================================
    // RESET
    // =========================================================

    private fun resetTimer() {

        countDownTimer?.cancel()

        isRunning = false

        isFocusSession = true

        sessionCount = 1

        totalMillis =
            if (isPomodoro) {

                focusMinutes * 60 * 1000L

            } else {

                val mins =
                    etCustomMinutes.text.toString()
                        .trim()
                        .toIntOrNull() ?: focusMinutes

                mins * 60 * 1000L
            }

        remainingMillis = totalMillis

        btnStartPause.text = "▶ Start"

        updateTimerDisplay()
    }

    // =========================================================
    // SKIP
    // =========================================================

    private fun skipSession() {

        countDownTimer?.cancel()

        isRunning = false

        onSessionFinished()
    }

    // =========================================================
    // SESSION FINISH
    // =========================================================

    private fun onSessionFinished() {

        // =========================================================
        // TRACK REAL ANALYTICS
        // =========================================================

        if (isFocusSession) {

            viewLifecycleOwner.lifecycleScope.launch {

                progressRepository
                    .onFocusSessionCompleted(
                        focusMinutes
                    )
            }
        }

//            // Add focus minutes
//            prefs.addFocusMinutes(focusMinutes)
//
//            // Update tracked days
//            val trackedDays =
//                prefs.getTotalTrackedDays()
//
//            if (trackedDays == 0) {
//                prefs.saveTotalTrackedDays(1)
//            }
//
//            // Increase completed days
//            prefs.saveCompletedDays(
//                prefs.getCompletedDays() + 1
//            )
//
//            // Increase streak
//            prefs.saveCurrentStreak(
//                prefs.getCurrentStreak() + 1)

//        }

        // =========================================================
        // CUSTOM TIMER MODE
        // =========================================================

        if (!isPomodoro) {

            Toast.makeText(
                requireContext(),
                "Session Complete 🔥",
                Toast.LENGTH_SHORT
            ).show()

            resetTimer()

            return
        }

        // =========================================================
        // POMODORO FLOW
        // =========================================================

        if (isFocusSession) {

            isFocusSession = false

            val longBreak =
                sessionCount % maxSessions == 0

            totalMillis =
                if (longBreak)
                    longBreakMinutes * 60 * 1000L
                else
                    shortBreakMinutes * 60 * 1000L

        } else {

            isFocusSession = true

            if (sessionCount < maxSessions) {
                sessionCount++
            }

            totalMillis =
                focusMinutes * 60 * 1000L
        }

        remainingMillis = totalMillis

        btnStartPause.text = "▶ Start"

        updateTimerDisplay()

        Toast.makeText(
            requireContext(),
            "Focus stats updated ⚡",
            Toast.LENGTH_SHORT
        ).show()
    }

    // =========================================================
    // UI
    // =========================================================

    private fun updateTimerDisplay() {

        focusTimerView.setTimerText(
            TimeUtils.formatTime(
                remainingMillis
            )
        )

        val progress =
            (
                    remainingMillis.toFloat()
                            /
                            totalMillis.toFloat()
                    ) * 100f

        focusTimerView.setProgress(progress)

        if (isPomodoro) {

            tvSessionLabel.text =
                if (isFocusSession)
                    "FOCUS SESSION"
                else
                    "BREAK TIME"

            tvSessionCount.text =
                "Session $sessionCount of $maxSessions"

        } else {

            tvSessionLabel.text =
                "CUSTOM TIMER"

            tvSessionCount.text =
                "Stay locked in 🔒"
        }
    }

    // =========================================================
    // LIFECYCLE
    // =========================================================

    override fun onDestroyView() {

        super.onDestroyView()

        countDownTimer?.cancel()
    }
}