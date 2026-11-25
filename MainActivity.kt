package com.example.cardscore

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {
    private data class Round(val previousA: Int, val previousB: Int, val description: String)

    private var scoreA = 0
    private var scoreB = 0
    private var target = 50
    private var playerCount = 4
    private var teamA = "Team A"
    private var teamB = "Team B"
    private var addMode = true
    private val rounds = mutableListOf<Round>()

    private lateinit var scoreAView: TextView
    private lateinit var scoreBView: TextView
    private lateinit var teamAView: TextView
    private lateinit var teamBView: TextView
    private lateinit var targetView: TextView
    private lateinit var statusView: TextView
    private lateinit var historyView: TextView
    private lateinit var playerView: TextView
    private lateinit var amountInput: EditText
    private lateinit var modeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(12, 31, 52)
        window.navigationBarColor = Color.rgb(12, 31, 52)
        buildScreen()
    }

    private fun buildScreen() {
        val scroll = ScrollView(this)
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 18, 18, 24)
            setBackgroundColor(Color.rgb(242, 246, 250))
        }
        scroll.addView(page)
        setContentView(scroll)

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(16, 18, 16, 18)
            background = rounded(Color.rgb(12, 31, 52), 22)
        }
        addText(header, "♠  CARD SCORE  ♥", 25, Color.WHITE, true, Gravity.CENTER)
        addText(header, "কার্ড গেমের স্কোর রাখুন সহজে", 13, Color.rgb(205, 220, 235), false, Gravity.CENTER)
        targetView = addText(header, "জয়ের টার্গেট: $target", 16, Color.rgb(255, 220, 120), true, Gravity.CENTER)
        page.addView(header, marginParams())

        val scoreRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        createScoreCard(scoreRow, true)
        createScoreCard(scoreRow, false)
        page.addView(scoreRow, marginParams())

        addText(page, "স্কোর যোগ / বিয়োগ", 19, Color.rgb(20, 43, 67), true)
        modeButton = Button(this).apply {
            isAllCaps = false
            setOnClickListener {
                addMode = !addMode
                updateMode()
            }
        }
        page.addView(modeButton, marginParams())

        amountInput = EditText(this).apply {
            hint = "পয়েন্ট লিখুন (যেমন ৫)"
            inputType = InputType.TYPE_CLASS_NUMBER
            textSize = 18f
            setPadding(14, 8, 14, 8)
            background = rounded(Color.WHITE, 12)
        }
        page.addView(amountInput, marginParams())

        val scoreButtons = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        actionButton(scoreButtons, "Team A-তে দিন", Color.rgb(22, 119, 86)) { applyScore(true) }
        actionButton(scoreButtons, "Team B-তে দিন", Color.rgb(36, 103, 166)) { applyScore(false) }
        page.addView(scoreButtons, marginParams())

        statusView = addText(page, "খেলা শুরু করুন", 16, Color.rgb(22, 119, 86), true, Gravity.CENTER)
        statusView.setPadding(4, 10, 4, 10)

        addText(page, "গেম সেটিংস", 19, Color.rgb(20, 43, 67), true)
        val settings = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(Color.WHITE, 16)
            setPadding(10, 8, 10, 8)
        }
        actionButton(settings, "টিমের নাম পরিবর্তন", Color.rgb(70, 83, 102)) { renameTeams() }
        actionButton(settings, "জয়ের টার্গেট পরিবর্তন", Color.rgb(70, 83, 102)) {
            askPositiveNumber("জয়ের টার্গেট", target) {
                target = it
                refresh()
            }
        }
        playerView = addText(settings, "খেলোয়াড় সংখ্যা: $playerCount জন", 14, Color.DKGRAY, true)
        actionButton(settings, "খেলোয়াড় সংখ্যা পরিবর্তন", Color.rgb(70, 83, 102)) {
            askPositiveNumber("মোট খেলোয়াড় সংখ্যা", playerCount) {
                playerCount = it
                playerView.text = "খেলোয়াড় সংখ্যা: $playerCount জন"
            }
        }
        page.addView(settings, marginParams())

        addText(page, "রাউন্ড হিস্ট্রি", 19, Color.rgb(20, 43, 67), true)
        historyView = addText(page, "এখনো কোনো রাউন্ড যোগ হয়নি", 14, Color.DKGRAY, false)
        historyView.setPadding(14, 12, 14, 12)
        historyView.background = rounded(Color.WHITE, 14)

        val bottomButtons = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        actionButton(bottomButtons, "শেষ স্কোর Undo", Color.rgb(190, 111, 34)) { undo() }
        actionButton(bottomButtons, "নতুন গেম", Color.rgb(177, 55, 55)) { confirmNewGame() }
        page.addView(bottomButtons, marginParams())

        updateMode()
        refresh()
    }

    private fun createScoreCard(parent: LinearLayout, isA: Boolean) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(8, 18, 8, 18)
            background = rounded(
                if (isA) Color.rgb(22, 119, 86) else Color.rgb(36, 103, 166), 18
            )
        }
        val params = LinearLayout.LayoutParams(0, 155, 1f).apply {
            setMargins(4, 12, 4, 16)
        }
        parent.addView(card, params)

        val name = addText(card, if (isA) teamA else teamB, 17, Color.WHITE, true, Gravity.CENTER)
        val score = addText(card, "0", 38, Color.WHITE, true, Gravity.CENTER)
        addText(card, "CURRENT SCORE", 10, Color.rgb(225, 238, 245), false, Gravity.CENTER)

        if (isA) {
            teamAView = name
            scoreAView = score
        } else {
            teamBView = name
            scoreBView = score
        }
    }

    private fun applyScore(isA: Boolean) {
        val points = amountInput.text.toString().toIntOrNull()
        if (points == null || points <= 0) {
            Toast.makeText(this, "১ বা তার বেশি পয়েন্ট লিখুন", Toast.LENGTH_SHORT).show()
            return
        }

        val previousA = scoreA
        val previousB = scoreB
        val delta = if (addMode) points else -points
        val oldScore = if (isA) scoreA else scoreB
        val newScore = (oldScore + delta).coerceAtLeast(0)

        if (newScore == oldScore) {
            Toast.makeText(this, "স্কোর ০-এর নিচে হতে পারবে না", Toast.LENGTH_SHORT).show()
            return
        }

        if (isA) scoreA = newScore else scoreB = newScore
        val teamName = if (isA) teamA else teamB
        val operation = if (addMode) "+$points" else "-$points"
        rounds.add(
            Round(
                previousA,
                previousB,
                "$teamName $operation  →  $scoreA : $scoreB"
            )
        )
        amountInput.text.clear()
        refresh()
    }

    private fun refresh() {
        scoreAView.text = scoreA.toString()
        scoreBView.text = scoreB.toString()
        teamAView.text = teamA
        teamBView.text = teamB
        targetView.text = "জয়ের টার্গেট: $target"

        statusView.text = when {
            scoreA >= target && scoreA > scoreB -> "🏆 $teamA বিজয়ী!"
            scoreB >= target && scoreB > scoreA -> "🏆 $teamB বিজয়ী!"
            scoreA >= target && scoreB >= target && scoreA == scoreB -> "দুই দল সমান — খেলা চলবে"
            else -> "খেলা চলছে  •  $teamA: $scoreA  |  $teamB: $scoreB"
        }

        historyView.text = if (rounds.isEmpty()) {
            "এখনো কোনো রাউন্ড যোগ হয়নি"
        } else {
            rounds.mapIndexed { index, round -> "${index + 1}. ${round.description}" }
                .asReversed().joinToString("\n")
        }
    }

    private fun undo() {
        if (rounds.isEmpty()) {
            Toast.makeText(this, "Undo করার মতো স্কোর নেই", Toast.LENGTH_SHORT).show()
            return
        }
        val lastRound = rounds.removeAt(rounds.lastIndex)
        scoreA = lastRound.previousA
        scoreB = lastRound.previousB
        refresh()
    }

    private fun confirmNewGame() {
        AlertDialog.Builder(this)
            .setTitle("নতুন গেম শুরু করবেন?")
            .setMessage("বর্তমান স্কোর ও রাউন্ড হিস্ট্রি মুছে যাবে।")
            .setPositiveButton("হ্যাঁ, শুরু করুন") { _, _ ->
                scoreA = 0
                scoreB = 0
                rounds.clear()
                refresh()
            }
            .setNegativeButton("বাতিল", null)
            .show()
    }

    private fun renameTeams() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 8, 22, 0)
        }
        val inputA = EditText(this).apply {
            setText(teamA)
            hint = "Team A নাম"
        }
        val inputB = EditText(this).apply {
            setText(teamB)
            hint = "Team B নাম"
        }
        layout.addView(inputA)
        layout.addView(inputB)

        AlertDialog.Builder(this)
            .setTitle("টিমের নাম")
            .setView(layout)
            .setPositiveButton("সংরক্ষণ") { _, _ ->
                teamA = inputA.text.toString().trim().ifBlank { "Team A" }
                teamB = inputB.text.toString().trim().ifBlank { "Team B" }
                refresh()
            }
            .setNegativeButton("বাতিল", null)
            .show()
    }

    private fun askPositiveNumber(title: String, current: Int, onSave: (Int) -> Unit) {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            setText(current.toString())
        }
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(input)
            .setPositiveButton("সংরক্ষণ") { _, _ ->
                val value = input.text.toString().toIntOrNull()
                if (value != null && value > 0) {
                    onSave(value)
                } else {
                    Toast.makeText(this, "১ বা তার বেশি সংখ্যা দিন", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("বাতিল", null)
            .show()
    }

    private fun updateMode() {
        modeButton.text = if (addMode) {
            "বর্তমান মোড: যোগ (+)  •  পরিবর্তন করুন"
        } else {
            "বর্তমান মোড: বিয়োগ (−)  •  পরিবর্তন করুন"
        }
    }

    private fun addText(
        parent: LinearLayout,
        value: String,
        size: Int,
        color: Int,
        bold: Boolean = false,
        gravity: Int = Gravity.START
    ): TextView {
        val textView = TextView(this).apply {
            text = value
            textSize = size.toFloat()
            setTextColor(color)
            this.gravity = gravity
            if (bold) setTypeface(null, Typeface.BOLD)
            setPadding(5, 7, 5, 7)
        }
        parent.addView(textView)
        return textView
    }

    private fun actionButton(
        parent: LinearLayout,
        label: String,
        color: Int,
        action: () -> Unit
    ) {
        val button = Button(this).apply {
            text = label
            setTextColor(Color.WHITE)
            background = rounded(color, 12)
            isAllCaps = false
            setOnClickListener { action() }
        }

        val params = if (parent.orientation == LinearLayout.HORIZONTAL) {
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        } else {
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        params.setMargins(3, 4, 3, 4)
        parent.addView(button, params)
    }

    private fun marginParams() = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(0, 5, 0, 10)
    }

    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = radius.toFloat()
    }
}
