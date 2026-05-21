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

    // ── Timer state ──────────────────────────────────────────────
    private var countDownTimer: CountDownTimer? = null
    private var isRunning = false
    private var isPomodoro = true
    private var isFocusSession = true   // true = focus, false = break
    private var sessionCount = 1
    private val maxSessions = 4

    private var totalMillis = 25 * 60 * 1000L
    private var remainingMillis = totalMillis

    // ── Views ────────────────────────────────────────────────────
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

    // ── Quotes ───────────────────────────────────────────────────
    private val quotes = listOf(
        Pair("Discipline is choosing between what you want now and what you want most.", "Abraham Lincoln"),
        Pair("We are what we repeatedly do. Excellence, then, is not an act, but a habit.", "Aristotle"),
        Pair("It's not about having time. It's about making time.", "Unknown"),
        Pair("The secret of getting ahead is getting started.", "Mark Twain"),
        Pair("Focus on being productive instead of busy.", "Tim Ferriss"),
        Pair("You don't have to be great to start, but you have to start to be great.", "Zig Ziglar"),
        Pair("Small disciplines repeated with consistency every day lead to great achievements.", "John Maxwell"),
        Pair("The pain of discipline is far less than the pain of regret.", "Unknown"),
        Pair("One day or day one. You decide.", "Unknown"),
        Pair("Success is the sum of small efforts repeated day in and day out.", "Robert Collier"),
        Pair("Do something today that your future self will thank you for.", "Sean Patrick Flanery"),
        Pair("Push yourself, because no one else is going to do it for you.", "Unknown")
    )
    private var currentQuoteIndex = 0

    // ────────────────────────────────────────────────────────────
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
        setupTimerModeSelector()
        setupTimerControls()
        updateTimerDisplay()
    }

    // ── Bind ─────────────────────────────────────────────────────
    private fun bindViews(view: View) {
        tvTimer        = view.findViewById(R.id.tvTimer)
        tvSessionLabel = view.findViewById(R.id.tvSessionLabel)
        tvSessionCount = view.findViewById(R.id.tvSessionCount)
        timerProgress  = view.findViewById(R.id.timerProgress)
        btnStartPause  = view.findViewById(R.id.btnStartPause)
        btnReset       = view.findViewById(R.id.btnReset)
        btnSkip        = view.findViewById(R.id.btnSkip)
        rgTimerMode    = view.findViewById(R.id.rgTimerMode)
        layoutCustomTime = view.findViewById(R.id.layoutCustomTime)
        etCustomMinutes  = view.findViewById(R.id.etCustomMinutes)
        tvQuote        = view.findViewById(R.id.tvQuote)
        tvQuoteAuthor  = view.findViewById(R.id.tvQuoteAuthor)
        btnNewQuote    = view.findViewById(R.id.btnNewQuote)
    }

    // ── Quotes ───────────────────────────────────────────────────
    private fun setupQuotes() {
        currentQuoteIndex = (quotes.indices).random()
        showQuote(currentQuoteIndex)

        btnNewQuote.setOnClickListener {
            currentQuoteIndex = (currentQuoteIndex + 1) % quotes.size
            showQuote(currentQuoteIndex)
        }
    }

    private fun showQuote(index: Int) {
        val (text, author) = quotes[index]
        tvQuote.text = "\"$text\""
        tvQuoteAuthor.text = "— $author"
    }

    // ── Timer Mode ───────────────────────────────────────────────
    private fun setupTimerModeSelector() {
        rgTimerMode.setOnCheckedChangeListener { _, checkedId ->
            isPomodoro = checkedId == R.id.rbPomodoro
            layoutCustomTime.visibility = if (isPomodoro) View.GONE else View.VISIBLE
            resetTimer()
        }
    }

    // ── Controls ─────────────────────────────────────────────────
    private fun setupTimerControls() {
        btnStartPause.setOnClickListener {
            if (isRunning) pauseTimer() else startTimer()
        }
        btnReset.setOnClickListener { resetTimer() }
        btnSkip.setOnClickListener  { skipSession() }
    }

    // ── Start ────────────────────────────────────────────────────
    private fun startTimer() {
        // If custom mode, read the user's minutes
        if (!isPomodoro) {
            val mins = etCustomMinutes.text.toString().toIntOrNull()
            if (mins == null || mins <= 0) {
                Toast.makeText(requireContext(), "Enter a valid number of minutes", Toast.LENGTH_SHORT).show()
                return
            }
            totalMillis    = mins * 60 * 1000L
            remainingMillis = totalMillis
        }

        isRunning = true
        btnStartPause.text = "⏸  Pause"

        countDownTimer = object : CountDownTimer(remainingMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingMillis = millisUntilFinished
                updateTimerDisplay()
            }
            override fun onFinish() {
                isRunning = false
                remainingMillis = 0
                updateTimerDisplay()
                onSessionFinished()
            }
        }.start()
    }

    // ── Pause ────────────────────────────────────────────────────
    private fun pauseTimer() {
        countDownTimer?.cancel()
        isRunning = false
        btnStartPause.text = "▶  Resume"
    }

    // ── Reset ────────────────────────────────────────────────────
    private fun resetTimer() {
        countDownTimer?.cancel()
        isRunning = false
        isFocusSession = true
        sessionCount   = 1

        totalMillis = if (isPomodoro) 25 * 60 * 1000L else {
            val mins = etCustomMinutes.text.toString().toIntOrNull() ?: 25
            mins * 60 * 1000L
        }
        remainingMillis = totalMillis

        btnStartPause.text = "▶  Start"
        updateTimerDisplay()
    }

    // ── Skip ─────────────────────────────────────────────────────
    private fun skipSession() {
        countDownTimer?.cancel()
        isRunning = false
        onSessionFinished()
    }

    // ── Session finished ─────────────────────────────────────────
    private fun onSessionFinished() {
        if (!isPomodoro) {
            // Custom mode: just reset
            Toast.makeText(requireContext(), "⏱ Timer done! Great work.", Toast.LENGTH_SHORT).show()
            resetTimer()
            return
        }

        if (isFocusSession) {
            // Switch to break
            isFocusSession = false
            val isLongBreak = sessionCount % maxSessions == 0
            totalMillis    = if (isLongBreak) 15 * 60 * 1000L else 5 * 60 * 1000L
            remainingMillis = totalMillis
            val msg = if (isLongBreak) "🛋️ Long break! You earned it." else "☕ Short break time!"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        } else {
            // Switch back to focus
            isFocusSession = true
            if (sessionCount < maxSessions) sessionCount++
            totalMillis    = 25 * 60 * 1000L
            remainingMillis = totalMillis
            Toast.makeText(requireContext(), "🍅 Focus session $sessionCount!", Toast.LENGTH_SHORT).show()
        }

        btnStartPause.text = "▶  Start"
        updateTimerDisplay()
    }

    // ── UI update ────────────────────────────────────────────────
    private fun updateTimerDisplay() {
        val minutes = (remainingMillis / 1000) / 60
        val seconds = (remainingMillis / 1000) % 60
        tvTimer.text = String.format("%02d:%02d", minutes, seconds)

        val progress = ((remainingMillis.toFloat() / totalMillis.toFloat()) * 100).toInt()
        timerProgress.progress = progress

        if (isPomodoro) {
            tvSessionLabel.text  = if (isFocusSession) "FOCUS SESSION" else "BREAK TIME"
            tvSessionCount.text  = "Session $sessionCount of $maxSessions"
        } else {
            tvSessionLabel.text  = "CUSTOM TIMER"
            tvSessionCount.text  = "Stay locked in 🔒"
        }
    }

    // ── Cleanup ──────────────────────────────────────────────────
    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }
}