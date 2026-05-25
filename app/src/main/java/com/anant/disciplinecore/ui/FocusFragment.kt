package com.anant.disciplinecore.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.anant.disciplinecore.R

class FocusFragment : Fragment() {

    private var countDownTimer: CountDownTimer? = null

    private var isRunning = false
    private var isPomodoro = true
    private var isFocusSession = true

    private var sessionCount = 1
    private val maxSessions = 4

    private var totalMillis = 25 * 60 * 1000L
    private var remainingMillis = totalMillis

    // Views
    private lateinit var tvTimer: TextView
    private lateinit var tvSessionLabel: TextView
    private lateinit var tvSessionCount: TextView
    private lateinit var timerProgress: ProgressBar

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
        Pair("Discipline is choosing between what you want now and what you want most.", "Abraham Lincoln"),
        Pair("Focus on being productive instead of busy.", "Tim Ferriss"),
        Pair("One day or day one. You decide.", "Unknown"),
        Pair("Success is the sum of small efforts repeated day in and day out.", "Robert Collier"),
        Pair("Push yourself, because no one else is going to do it for you.", "Unknown"),
        Pair("The pain of discipline is far less than the pain of regret.", "Unknown")
    )

    private var currentQuoteIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_focus, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        setupQuotes()
        setupModeSelector()
        setupButtons()

        updateTimerDisplay()
    }

    private fun bindViews(view: View) {

        tvTimer = view.findViewById(R.id.tvTimer)
        tvSessionLabel = view.findViewById(R.id.tvSessionLabel)
        tvSessionCount = view.findViewById(R.id.tvSessionCount)
        timerProgress = view.findViewById(R.id.timerProgress)

        btnStartPause = view.findViewById(R.id.btnStartPause)
        btnReset = view.findViewById(R.id.btnReset)
        btnSkip = view.findViewById(R.id.btnSkip)

        rgTimerMode = view.findViewById(R.id.rgTimerMode)
        layoutCustomTime = view.findViewById(R.id.layoutCustomTime)
        etCustomMinutes = view.findViewById(R.id.etCustomMinutes)

        tvQuote = view.findViewById(R.id.tvQuote)
        tvQuoteAuthor = view.findViewById(R.id.tvQuoteAuthor)
        btnNewQuote = view.findViewById(R.id.btnNewQuote)
    }

    // ------------------------------------------------
    // Quotes
    // ------------------------------------------------

    private fun setupQuotes() {

        currentQuoteIndex = quotes.indices.random()
        showQuote(currentQuoteIndex)

        btnNewQuote.setOnClickListener {

            currentQuoteIndex++

            if (currentQuoteIndex >= quotes.size) {
                currentQuoteIndex = 0
            }

            showQuote(currentQuoteIndex)
        }
    }

    private fun showQuote(index: Int) {

        val quote = quotes[index]

        tvQuote.text = "\"${quote.first}\""
        tvQuoteAuthor.text = "— ${quote.second}"
    }

    // ------------------------------------------------
    // Timer Mode
    // ------------------------------------------------

    private fun setupModeSelector() {

        rgTimerMode.setOnCheckedChangeListener { _, checkedId ->

            isPomodoro = checkedId == R.id.rbPomodoro

            layoutCustomTime.visibility =
                if (isPomodoro) View.GONE else View.VISIBLE

            resetTimer()
        }
    }

    // ------------------------------------------------
    // Buttons
    // ------------------------------------------------

    private fun setupButtons() {

        btnStartPause.setOnClickListener {

            if (isRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        btnReset.setOnClickListener {
            resetTimer()
        }

        btnSkip.setOnClickListener {
            skipSession()
        }
    }

    // ------------------------------------------------
    // Start Timer
    // ------------------------------------------------

    private fun startTimer() {

        // CUSTOM MODE
        if (!isPomodoro && remainingMillis == totalMillis) {

            val mins =
                etCustomMinutes.text.toString().trim().toIntOrNull()

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

            override fun onTick(millisUntilFinished: Long) {

                remainingMillis = millisUntilFinished

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

    // ------------------------------------------------
    // Pause
    // ------------------------------------------------

    private fun pauseTimer() {

        countDownTimer?.cancel()

        isRunning = false

        btnStartPause.text = "Resume"
    }

    // ------------------------------------------------
    // Reset
    // ------------------------------------------------

    private fun resetTimer() {

        countDownTimer?.cancel()

        isRunning = false

        isFocusSession = true
        sessionCount = 1

        totalMillis =
            if (isPomodoro) {
                25 * 60 * 1000L
            } else {

                val mins =
                    etCustomMinutes.text.toString()
                        .trim()
                        .toIntOrNull() ?: 25

                mins * 60 * 1000L
            }

        remainingMillis = totalMillis

        btnStartPause.text = "Start"

        updateTimerDisplay()
    }

    // ------------------------------------------------
    // Skip
    // ------------------------------------------------

    private fun skipSession() {

        countDownTimer?.cancel()

        isRunning = false

        onSessionFinished()
    }

    // ------------------------------------------------
    // Session Finish
    // ------------------------------------------------

    private fun onSessionFinished() {

        if (!isPomodoro) {

            Toast.makeText(
                requireContext(),
                "Session Complete 🔥",
                Toast.LENGTH_SHORT
            ).show()

            resetTimer()

            return
        }

        if (isFocusSession) {

            isFocusSession = false

            val longBreak =
                sessionCount % maxSessions == 0

            totalMillis =
                if (longBreak)
                    15 * 60 * 1000L
                else
                    5 * 60 * 1000L

        } else {

            isFocusSession = true

            if (sessionCount < maxSessions) {
                sessionCount++
            }

            totalMillis = 25 * 60 * 1000L
        }

        remainingMillis = totalMillis

        btnStartPause.text = "Start"

        updateTimerDisplay()
    }

    // ------------------------------------------------
    // UI
    // ------------------------------------------------

    private fun updateTimerDisplay() {

        val totalSeconds = remainingMillis / 1000

        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        tvTimer.text =
            String.format("%02d:%02d", minutes, seconds)

        val progress =
            ((remainingMillis.toFloat() / totalMillis.toFloat()) * 100).toInt()

        timerProgress.progress = progress

        if (isPomodoro) {

            tvSessionLabel.text =
                if (isFocusSession)
                    "FOCUS SESSION"
                else
                    "BREAK TIME"

            tvSessionCount.text =
                "Session $sessionCount of $maxSessions"

        } else {

            tvSessionLabel.text = "CUSTOM TIMER"
            tvSessionCount.text = "Stay locked in 🔒"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }
}