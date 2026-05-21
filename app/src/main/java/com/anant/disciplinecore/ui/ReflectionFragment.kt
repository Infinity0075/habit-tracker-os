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
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ReflectionFragment : Fragment() {

    private lateinit var etWin: TextInputEditText
    private lateinit var etReflection: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var layoutPastEntries: LinearLayout

    private val moodViews = mutableListOf<TextView>()
    private var selectedMood = 3 // default: 🙂

    private val PREFS = "journal_prefs"
    private val KEY_ENTRIES = "entries"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_reflection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setupMoodSelector()
        setupSaveButton()
        loadTodayEntry()
        loadPastEntries()
    }

    private fun bindViews(view: View) {
        etWin        = view.findViewById(R.id.etWin)
        etReflection = view.findViewById(R.id.etReflection)
        btnSave      = view.findViewById(R.id.btnSaveJournal)
        layoutPastEntries = view.findViewById(R.id.layoutPastEntries)

        moodViews.add(view.findViewById(R.id.mood1))
        moodViews.add(view.findViewById(R.id.mood2))
        moodViews.add(view.findViewById(R.id.mood3))
        moodViews.add(view.findViewById(R.id.mood4))
        moodViews.add(view.findViewById(R.id.mood5))
    }

    // ── Mood selector ────────────────────────────────────────────
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
        moodViews.forEachIndexed { index, tv ->
            tv.setBackgroundColor(
                if (index + 1 == selectedMood)
                    Color.parseColor("#F59E0B")
                else
                    Color.parseColor("#2A2A35")
            )
        }
    }

    // ── Save ─────────────────────────────────────────────────────
    private fun setupSaveButton() {
        btnSave.setOnClickListener {
            val win        = etWin.text.toString().trim()
            val reflection = etReflection.text.toString().trim()

            if (win.isEmpty() && reflection.isEmpty()) {
                Toast.makeText(requireContext(),
                    "Write something first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveEntry(win, reflection, selectedMood)
            Toast.makeText(requireContext(),
                "✅ Entry saved!", Toast.LENGTH_SHORT).show()
            loadPastEntries()
        }
    }

    // ── SharedPreferences storage ────────────────────────────────
    private fun saveEntry(win: String, reflection: String, mood: Int) {
        val prefs   = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_ENTRIES, "[]")
        val array   = JSONArray(existing)

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Update today's entry if exists, else add new
        var updated = false
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            if (obj.getString("date") == today) {
                obj.put("win", win)
                obj.put("reflection", reflection)
                obj.put("mood", mood)
                updated = true
                break
            }
        }

        if (!updated) {
            val entry = JSONObject().apply {
                put("date", today)
                put("win", win)
                put("reflection", reflection)
                put("mood", mood)
            }
            // Insert at beginning
            val newArray = JSONArray()
            newArray.put(entry)
            for (i in 0 until array.length()) newArray.put(array.get(i))
            prefs.edit().putString(KEY_ENTRIES, newArray.toString()).apply()
            return
        }

        prefs.edit().putString(KEY_ENTRIES, array.toString()).apply()
    }

    private fun loadTodayEntry() {
        val prefs   = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_ENTRIES, "[]")
        val array   = JSONArray(existing)
        val today   = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

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

    // ── Past entries list ────────────────────────────────────────
    private fun loadPastEntries() {
        val prefs   = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_ENTRIES, "[]")
        val array   = JSONArray(existing)
        val container = layoutPastEntries
        container.removeAllViews()
        val density = resources.displayMetrics.density

        if (array.length() == 0) {
            val empty = TextView(requireContext()).apply {
                text      = "No entries yet. Start journaling!"
                textSize  = 13f
                setTextColor(Color.parseColor("#6B6B80"))
            }
            container.addView(empty)
            return
        }

        val moods = listOf("😞","😐","🙂","😄","🔥")
        val displayFmt = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
        val parseFmt   = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (i in 0 until array.length()) {
            val obj  = array.getJSONObject(i)
            val date = try {
                displayFmt.format(parseFmt.parse(obj.getString("date"))!!)
            } catch (e: Exception) { obj.getString("date") }

            val win        = obj.optString("win", "")
            val reflection = obj.optString("reflection", "")
            val mood       = obj.optInt("mood", 3)
            val moodEmoji  = moods.getOrElse(mood - 1) { "🙂" }

            // Card-like row
            val card = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.parseColor("#22222A"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT).also {
                    it.bottomMargin = (10 * density).toInt()
                }
                setPadding(
                    (14 * density).toInt(), (12 * density).toInt(),
                    (14 * density).toInt(), (12 * density).toInt()
                )
            }

            val header = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            }

            val dateView = TextView(requireContext()).apply {
                text      = date
                textSize  = 12f
                setTextColor(Color.parseColor("#F59E0B"))
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val moodView = TextView(requireContext()).apply {
                text     = moodEmoji
                textSize = 18f
            }

            header.addView(dateView)
            header.addView(moodView)
            card.addView(header)

            if (win.isNotEmpty()) {
                val winView = TextView(requireContext()).apply {
                    text      = "🏆 $win"
                    textSize  = 13f
                    setTextColor(Color.parseColor("#F5F5F0"))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT).also {
                        it.topMargin = (6 * density).toInt()
                    }
                }
                card.addView(winView)
            }

            if (reflection.isNotEmpty()) {
                val refView = TextView(requireContext()).apply {
                    text      = "💭 $reflection"
                    textSize  = 13f
                    setTextColor(Color.parseColor("#9B9BAA"))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT).also {
                        it.topMargin = (4 * density).toInt()
                    }
                }
                card.addView(refView)
            }

            container.addView(card)
        }
    }
}