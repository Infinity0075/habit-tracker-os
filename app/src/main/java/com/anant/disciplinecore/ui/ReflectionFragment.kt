package com.anant.disciplinecore.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.anant.disciplinecore.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ReflectionFragment : Fragment() {

    // ── Views (lateinit only for views guaranteed in both old + new XML) ──
    private lateinit var etWin: TextInputEditText
    private lateinit var etReflection: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var layoutPastEntries: LinearLayout
    private val moodViews = mutableListOf<TextView>()

    // ── Optional views — only present in the new XML ──────────────
    // Using nullable so the fragment compiles & runs even if you haven't
    // swapped the layout file yet.
    private var tvMoodLabel: TextView?   = null
    private var tvJournalDate: TextView? = null
    private var tvStreakCount: TextView? = null
    private var tvEntryCount: TextView?  = null

    // ── State ─────────────────────────────────────────────────────
    private var selectedMood = 3

    // ── Singletons ────────────────────────────────────────────────
    companion object {
        private const val PREFS       = "journal_prefs"
        private const val KEY_ENTRIES = "entries"

        val MOOD_LABELS = listOf("Rough day", "Meh", "Feeling okay", "Pretty good", "On fire!")
        val MOOD_EMOJIS = listOf("😞", "😐", "🙂", "😄", "🔥")

        // Created once — not per-render
        val FMT_STORE   = SimpleDateFormat("yyyy-MM-dd",   Locale.getDefault())
        val FMT_DISPLAY = SimpleDateFormat("EEE, dd MMM",  Locale.getDefault())
        val FMT_HEADER  = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
    }

    // ─────────────────────────────────────────────────────────────
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_reflection, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setupHeader()
        setupMoodSelector()
        setupSaveButton()
        loadTodayEntry()
        loadPastEntries()
    }

    // ── Bind ──────────────────────────────────────────────────────
    private fun bindViews(view: View) {
        etWin             = view.findViewById(R.id.etWin)
        etReflection      = view.findViewById(R.id.etReflection)
        btnSave           = view.findViewById(R.id.btnSaveJournal)
        layoutPastEntries = view.findViewById(R.id.layoutPastEntries)

        // Optional — null-safe, won't crash on old XML
        tvMoodLabel   = view.findViewById(R.id.tvMoodLabel)
        tvJournalDate = view.findViewById(R.id.tvJournalDate)
        tvStreakCount = view.findViewById(R.id.tvStreakCount)
        tvEntryCount  = view.findViewById(R.id.tvEntryCount)

        moodViews += listOf(
            view.findViewById(R.id.mood1),
            view.findViewById(R.id.mood2),
            view.findViewById(R.id.mood3),
            view.findViewById(R.id.mood4),
            view.findViewById<TextView>(R.id.mood5)
        )
    }

    // ── Header ────────────────────────────────────────────────────
    private fun setupHeader() {
        tvJournalDate?.text = FMT_HEADER.format(Date())
        updateStreakBadge()
    }

    /** Counts consecutive days with entries ending on today. */
    private fun updateStreakBadge() {
        val entries = loadAllEntries()
        val dates = (0 until entries.length())
            .map { entries.getJSONObject(it).getString("date") }
            .toSet()

        var streak = 0
        val cal = Calendar.getInstance()
        while (true) {
            if (dates.contains(FMT_STORE.format(cal.time))) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else break
        }
        tvStreakCount?.text = streak.toString()
    }

    // ── Mood ──────────────────────────────────────────────────────
    private fun setupMoodSelector() {
        moodViews.forEachIndexed { index, tv ->
            tv.setOnClickListener {
                selectedMood = index + 1
                updateMoodUI()
            }
        }
        updateMoodUI()
    }

    private fun updateMoodUI() {
        // Check whether the new drawables exist; fall back to color if not
        val hasNewDrawables = try {
            resources.getDrawable(R.drawable.bg_mood_selected, null)
            true
        } catch (e: Exception) { false }

        moodViews.forEachIndexed { index, tv ->
            val isSelected = index + 1 == selectedMood
            if (hasNewDrawables) {
                tv.setBackgroundResource(
                    if (isSelected) R.drawable.bg_mood_selected
                    else            R.drawable.bg_mood_unselected
                )
            } else {
                // Fallback for old XML without drawables
                tv.setBackgroundColor(
                    if (isSelected) Color.parseColor("#3D2E0A")
                    else            Color.parseColor("#2A2A35")
                )
            }
            tv.animate()
                .scaleX(if (isSelected) 1.15f else 1f)
                .scaleY(if (isSelected) 1.15f else 1f)
                .setDuration(150)
                .start()
        }
        tvMoodLabel?.text = MOOD_LABELS.getOrElse(selectedMood - 1) { "Feeling okay" }
    }

    // ── Save ──────────────────────────────────────────────────────
    private fun setupSaveButton() {
        btnSave.setOnClickListener {
            val win        = etWin.text.toString().trim()
            val reflection = etReflection.text.toString().trim()

            if (win.isEmpty() && reflection.isEmpty()) {
                Snackbar.make(requireView(), "Write something first ✍️", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.parseColor("#2A2825"))
                    .setTextColor(Color.parseColor("#FAFAF8"))
                    .show()
                return@setOnClickListener
            }

            saveEntry(win, reflection, selectedMood)

            Snackbar.make(requireView(), "Entry saved ✅", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.parseColor("#1E2A1E"))
                .setTextColor(Color.parseColor("#4ADE80"))
                .show()

            updateStreakBadge()
            loadPastEntries()
        }
    }

    // ── Persistence ───────────────────────────────────────────────
    private fun prefs() =
        requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun loadAllEntries(): JSONArray = try {
        JSONArray(prefs().getString(KEY_ENTRIES, "[]"))
    } catch (e: Exception) { JSONArray() }

    private fun saveEntry(win: String, reflection: String, mood: Int) {
        val array = loadAllEntries()
        val today = FMT_STORE.format(Date())

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            if (obj.getString("date") == today) {
                obj.put("win", win)
                obj.put("reflection", reflection)
                obj.put("mood", mood)
                prefs().edit().putString(KEY_ENTRIES, array.toString()).apply()
                return
            }
        }

        val newArray = JSONArray().apply {
            put(JSONObject().apply {
                put("date", today)
                put("win", win)
                put("reflection", reflection)
                put("mood", mood)
            })
            for (i in 0 until array.length()) put(array.get(i))
        }
        prefs().edit().putString(KEY_ENTRIES, newArray.toString()).apply()
    }

    private fun loadTodayEntry() {
        val today = FMT_STORE.format(Date())
        val array = loadAllEntries()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            if (obj.getString("date") == today) {
                etWin.setText(obj.optString("win", ""))
                etReflection.setText(obj.optString("reflection", ""))
                selectedMood = obj.optInt("mood", 3)
                updateMoodUI()
                break
            }
        }
    }

    // ── Past entries ──────────────────────────────────────────────
    private fun loadPastEntries() {
        val today   = FMT_STORE.format(Date())
        val density = resources.displayMetrics.density
        layoutPastEntries.removeAllViews()

        val past = (0 until loadAllEntries().length())
            .map { loadAllEntries().getJSONObject(it) }
            .filter { it.getString("date") != today }

        tvEntryCount?.text = "${past.size} ${if (past.size == 1) "entry" else "entries"}"

        if (past.isEmpty()) {
            layoutPastEntries.addView(TextView(requireContext()).apply {
                text = "No past entries yet.\nStart writing — your future self will thank you."
                textSize = 13f
                setTextColor(Color.parseColor("#57534E"))
                setLineSpacing(0f, 1.5f)
                setPadding(0, (8 * density).toInt(), 0, (8 * density).toInt())
            })
            return
        }

        past.forEach { obj -> layoutPastEntries.addView(makeEntryCard(obj, density)) }
    }

    private fun makeEntryCard(obj: JSONObject, density: Float): View {
        val dateStr = try {
            FMT_DISPLAY.format(FMT_STORE.parse(obj.getString("date"))!!)
        } catch (e: Exception) { obj.getString("date") }

        val win        = obj.optString("win", "")
        val reflection = obj.optString("reflection", "")
        val mood       = obj.optInt("mood", 3)

        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            // Use new drawable if available, else plain color
            try {
                setBackgroundResource(R.drawable.bg_entry_card)
            } catch (e: Exception) {
                setBackgroundColor(Color.parseColor("#242018"))
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = (10 * density).toInt() }
            setPadding(
                (16 * density).toInt(), (14 * density).toInt(),
                (16 * density).toInt(), (14 * density).toInt()
            )
        }

        // Header row
        val header = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity     = android.view.Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        header.addView(TextView(requireContext()).apply {
            text     = dateStr
            textSize = 12f
            setTextColor(Color.parseColor("#A8956A"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        header.addView(TextView(requireContext()).apply {
            text     = "${MOOD_EMOJIS.getOrElse(mood - 1) { "🙂" }}  ${MOOD_LABELS.getOrElse(mood - 1) { "Okay" }}"
            textSize = 11f
            setTextColor(Color.parseColor("#78716C"))
        })
        card.addView(header)

        // Thin divider
        card.addView(View(requireContext()).apply {
            setBackgroundColor(Color.parseColor("#2C2825"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (1 * density).toInt()
            ).also { it.topMargin = (10 * density).toInt(); it.bottomMargin = (10 * density).toInt() }
        })

        if (win.isNotEmpty()) card.addView(TextView(requireContext()).apply {
            text = "🏆  $win"
            textSize = 13f
            setTextColor(Color.parseColor("#FAFAF8"))
            setLineSpacing(0f, 1.4f)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = if (reflection.isNotEmpty()) (6 * density).toInt() else 0 }
        })

        if (reflection.isNotEmpty()) card.addView(TextView(requireContext()).apply {
            text = "💭  $reflection"
            textSize = 13f
            setTextColor(Color.parseColor("#A09898"))
            setLineSpacing(0f, 1.4f)
        })

        return card
    }
}