package com.anant.disciplinecore.features.reflection

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.anant.disciplinecore.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ReflectionFragment : Fragment() {

    // ─── Required views ───────────────────────────────────────────────────────
    private lateinit var etWin: TextInputEditText
    private lateinit var etReflection: TextInputEditText
    private lateinit var btnSave: MaterialButton
    private lateinit var layoutPastEntries: LinearLayout
    private val moodViews = mutableListOf<TextView>()

    // ─── Optional views (null-safe; won't crash if XML ever rolls back) ───────
    private var tvMoodLabel: TextView?   = null
    private var tvJournalDate: TextView? = null
    private var tvStreakCount: TextView? = null
    private var tvEntryCount: TextView?  = null

    // ─── State ────────────────────────────────────────────────────────────────
    private var selectedMood = 3

    // ─── Constants ────────────────────────────────────────────────────────────
    companion object {
        private const val PREFS       = "journal_prefs"
        private const val KEY_ENTRIES = "entries"

        val MOOD_LABELS = listOf("Rough day", "Meh", "Feeling okay", "Pretty good", "On fire!")
        val MOOD_EMOJIS = listOf("😞", "😐", "🙂", "😄", "🔥")

        val FMT_STORE   = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val FMT_DISPLAY = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
        val FMT_HEADER  = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
    }

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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
        animateEntrance(view)
    }

    // ─── Bind ─────────────────────────────────────────────────────────────────

    private fun bindViews(view: View) {
        etWin             = view.findViewById(R.id.etWin)
        etReflection      = view.findViewById(R.id.etReflection)
        btnSave           = view.findViewById(R.id.btnSaveJournal)
        layoutPastEntries = view.findViewById(R.id.layoutPastEntries)

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

    // ─── Header ───────────────────────────────────────────────────────────────

    private fun setupHeader() {
        tvJournalDate?.text = FMT_HEADER.format(Date())
        updateStreakBadge()
    }

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

    // ─── Mood ─────────────────────────────────────────────────────────────────

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
            val isSelected = index + 1 == selectedMood

            tv.setBackgroundResource(
                if (isSelected) R.drawable.bg_mood_selected
                else            R.drawable.bg_mood_unselected
            )

            tv.animate()
                .scaleX(if (isSelected) 1.18f else 1f)
                .scaleY(if (isSelected) 1.18f else 1f)
                .setDuration(180)
                .setInterpolator(OvershootInterpolator(2f))
                .start()
        }
        tvMoodLabel?.text = MOOD_LABELS.getOrElse(selectedMood - 1) { "Feeling okay" }
    }

    // ─── Save ─────────────────────────────────────────────────────────────────

    private fun setupSaveButton() {
        btnSave.setOnClickListener {
            val win        = etWin.text.toString().trim()
            val reflection = etReflection.text.toString().trim()

            if (win.isEmpty() && reflection.isEmpty()) {
                Snackbar.make(requireView(), "Write something first ✍️", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.parseColor("#1E1E14"))
                    .setTextColor(Color.parseColor("#F59E0B"))
                    .show()
                return@setOnClickListener
            }

            saveEntry(win, reflection, selectedMood)
            animateSaveButton()

            Snackbar.make(requireView(), "Entry saved ✅", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.parseColor("#0F1F0F"))
                .setTextColor(Color.parseColor("#4ADE80"))
                .show()

            updateStreakBadge()
            loadPastEntries()
        }
    }

    private fun animateSaveButton() {
        btnSave.animate()
            .scaleX(0.96f)
            .scaleY(0.96f)
            .setDuration(100)
            .withEndAction {
                btnSave.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .setInterpolator(OvershootInterpolator(2.5f))
                    .start()
            }.start()
    }

    // ─── Persistence ──────────────────────────────────────────────────────────

    private fun prefs() =
        requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun loadAllEntries(): JSONArray = try {
        JSONArray(prefs().getString(KEY_ENTRIES, "[]"))
    } catch (e: Exception) {
        JSONArray()
    }

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

    // ─── Past entries ─────────────────────────────────────────────────────────

    private fun loadPastEntries() {
        val today   = FMT_STORE.format(Date())
        val dp      = resources.displayMetrics.density
        layoutPastEntries.removeAllViews()

        val all  = loadAllEntries()
        val past = (0 until all.length())
            .map { all.getJSONObject(it) }
            .filter { it.getString("date") != today }

        val count = past.size
        tvEntryCount?.text = "$count ${if (count == 1) "entry" else "entries"}"

        if (past.isEmpty()) {
            layoutPastEntries.addView(makeEmptyState(dp))
            return
        }

        past.forEachIndexed { index, obj ->
            val card = makeEntryCard(obj, dp)
            card.alpha = 0f
            card.translationY = 16f
            layoutPastEntries.addView(card)
            card.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(340)
                .setInterpolator(DecelerateInterpolator(2f))
                .setStartDelay((index * 55).toLong())
                .start()
        }
    }

    // ─── Empty state view ─────────────────────────────────────────────────────

    private fun makeEmptyState(dp: Float): View {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity     = Gravity.CENTER
            setPadding(0, (24 * dp).toInt(), 0, (8 * dp).toInt())

            addView(TextView(requireContext()).apply {
                text      = "📖"
                textSize  = 32f
                gravity   = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            })
            addView(TextView(requireContext()).apply {
                text      = "No past entries yet."
                textSize  = 14f
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setTextColor(Color.parseColor("#E5E7EB"))
                typeface  = android.graphics.Typeface.DEFAULT_BOLD
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.topMargin = (10 * dp).toInt() }
            })
            addView(TextView(requireContext()).apply {
                text      = "Start writing — your future self will thank you."
                textSize  = 13f
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setTextColor(Color.parseColor("#6B7280"))
                setLineSpacing(0f, 1.5f)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.topMargin = (6 * dp).toInt() }
            })
        }
    }

    // ─── Entry card builder ───────────────────────────────────────────────────

    private fun makeEntryCard(obj: JSONObject, dp: Float): View {
        val dateStr = try {
            FMT_DISPLAY.format(FMT_STORE.parse(obj.getString("date"))!!)
        } catch (e: Exception) { obj.getString("date") }

        val win        = obj.optString("win", "")
        val reflection = obj.optString("reflection", "")
        val mood       = obj.optInt("mood", 3)
        val moodEmoji  = MOOD_EMOJIS.getOrElse(mood - 1) { "🙂" }
        val moodLabel  = MOOD_LABELS.getOrElse(mood - 1) { "Okay" }

        // ── Outer card ───────────────────────────────────────────────────────
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            try {
                setBackgroundResource(R.drawable.bg_entry_card)
            } catch (e: Exception) {
                setBackgroundColor(Color.parseColor("#0D0D10"))
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = (12 * dp).toInt() }
            setPadding(
                (18 * dp).toInt(), (16 * dp).toInt(),
                (18 * dp).toInt(), (16 * dp).toInt()
            )
        }

        // ── Header row: date | mood chip ─────────────────────────────────────
        val header = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity     = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        header.addView(TextView(requireContext()).apply {
            text      = dateStr
            textSize  = 11f
            letterSpacing = 0.04f
            setTextColor(Color.parseColor("#F59E0B"))
            layoutParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )
        })

        // Mood chip
        header.addView(LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity     = Gravity.CENTER_VERTICAL
            try {
                setBackgroundResource(R.drawable.bg_settings_chip)
            } catch (e: Exception) {
                setBackgroundColor(Color.parseColor("#1A1A20"))
            }
            setPadding(
                (8 * dp).toInt(), (3 * dp).toInt(),
                (8 * dp).toInt(), (3 * dp).toInt()
            )

            addView(TextView(requireContext()).apply {
                text     = moodEmoji
                textSize = 11f
            })
            addView(TextView(requireContext()).apply {
                text     = "  $moodLabel"
                textSize = 11f
                setTextColor(Color.parseColor("#9CA3AF"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            })
        })

        card.addView(header)

        // ── Hairline divider ─────────────────────────────────────────────────
        card.addView(View(requireContext()).apply {
            setBackgroundColor(Color.parseColor("#12FFFFFF"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (1 * dp).toInt()
            ).also {
                it.topMargin    = (12 * dp).toInt()
                it.bottomMargin = (12 * dp).toInt()
            }
        })

        // ── Win row ──────────────────────────────────────────────────────────
        if (win.isNotEmpty()) {
            card.addView(TextView(requireContext()).apply {
                text = "🏆  $win"
                textSize = 14f
                setTextColor(Color.parseColor("#F3F4F6"))
                setLineSpacing(0f, 1.45f)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also {
                    if (reflection.isNotEmpty()) it.bottomMargin = (8 * dp).toInt()
                }
            })
        }

        // ── Reflection row ───────────────────────────────────────────────────
        if (reflection.isNotEmpty()) {
            card.addView(TextView(requireContext()).apply {
                text = "💭  $reflection"
                textSize = 13f
                setTextColor(Color.parseColor("#9CA3AF"))
                setLineSpacing(0f, 1.45f)
            })
        }

        return card
    }

    // ─── Entrance animation ───────────────────────────────────────────────────

    private fun animateEntrance(root: View) {
        val scrollChild = (root as? androidx.core.widget.NestedScrollView)
            ?.getChildAt(0) as? ViewGroup ?: return

        for (i in 0 until scrollChild.childCount) {
            val child = scrollChild.getChildAt(i)
            child.alpha = 0f
            child.translationY = 24f
            child.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setInterpolator(DecelerateInterpolator(2f))
                .setStartDelay((50 + i * 50).toLong())
                .start()
        }
    }
}