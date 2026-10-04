package com.footpredict

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var et1: EditText
    private lateinit var et2: EditText
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var tvScore: TextView
    private lateinit var tvVerdict: TextView
    private lateinit var tvDetail1: TextView
    private lateinit var tvDetail2: TextView
    private lateinit var tvNote: TextView
    private lateinit var tvHistory: TextView
    private lateinit var prefs: SharedPreferences

    // ─── Couleurs ────────────────────────────────
    private val C_BG      = Color.parseColor("#0a0e1a")
    private val C_CARD    = Color.parseColor("#141b2d")
    private val C_INPUT   = Color.parseColor("#0d1322")
    private val C_ACCENT  = Color.parseColor("#00e08a")
    private val C_ACCENT2 = Color.parseColor("#3ba0ff")
    private val C_TEXT    = Color.parseColor("#e8eefc")
    private val C_MUTED   = Color.parseColor("#8b98b8")
    private val C_GOLD    = Color.parseColor("#ffd166")
    private val C_BORDER  = Color.parseColor("#2c3854")
    private val C_RED     = Color.parseColor("#ff5d6c")

    // ─── Couleurs par élément ─────────────────────
    private val C_FEU   = Color.parseColor("#ff5d3a")
    private val C_AIR   = Color.parseColor("#7fc4ff")
    private val C_TERRE = Color.parseColor("#c19a6b")
    private val C_EAU   = Color.parseColor("#3ba0ff")

    // ─── Données Abjad ───────────────────────────
    private val ABJAD = mapOf(
        'A' to 1,'B' to 2,'C' to 20,'D' to 4,'E' to 5,'F' to 80,'G' to 3,
        'H' to 5,'I' to 10,'J' to 3,'K' to 20,'L' to 30,'M' to 40,'N' to 50,
        'O' to 6,'P' to 2,'Q' to 100,'R' to 200,'S' to 60,'T' to 400,'U' to 6,
        'V' to 6,'W' to 6,'X' to 60,'Y' to 10,'Z' to 7
    )

    private data class Planet(
        val name: String,
        val star: String,
        val nature: String,
        val symbol: String,   // ✅ symbole pour décor
        val element: String   // ✅ élément pour couleur
    )

    private val PLANETS = mapOf(
        1 to Planet("Soleil",   "Regulus",    "Jupiter/Mars",    "☀️", "feu"),
        2 to Planet("Lune",     "Sirius",     "Jupiter/Saturne", "🌙", "eau"),
        3 to Planet("Jupiter",  "Aldebaran",  "Mars/Jupiter",    "🪐", "air"),
        4 to Planet("Uranus",   "Algol",      "Saturne/Jupiter", "⚡", "terre"),
        5 to Planet("Mercure",  "Rigel",      "Jupiter/Saturne", "☿", "air"),
        6 to Planet("Venus",    "Capella",    "Mars/Mercure",    "♀", "eau"),
        7 to Planet("Neptune",  "Procyon",    "Mercure/Mars",    "♆", "eau"),
        8 to Planet("Saturne",  "Altair",     "Mars/Jupiter",    "♄", "terre"),
        9 to Planet("Mars",     "Betelgeuse", "Mars/Mercure",    "♂", "feu")
    )

    private val STAR_WINDOWS = mapOf(
        "Regulus" to (11 to 13), "Sirius" to (23 to 1), "Aldebaran" to (5 to 7),
        "Algol" to (18 to 20), "Rigel" to (20 to 22), "Capella" to (21 to 23),
        "Procyon" to (3 to 5), "Altair" to (1 to 3), "Betelgeuse" to (14 to 16)
    )

    private val DAYS = arrayOf(
        "Lundi" to "Lune", "Mardi" to "Mars", "Mercredi" to "Mercure",
        "Jeudi" to "Jupiter", "Vendredi" to "Venus", "Samedi" to "Saturne",
        "Dimanche" to "Soleil"
    )

    private val HARMONY = mapOf(
        "Jupiter/Mars" to listOf("Jupiter","Mars"),
        "Jupiter/Saturne" to listOf("Jupiter","Saturne"),
        "Mars/Jupiter" to listOf("Mars","Jupiter"),
        "Saturne/Jupiter" to listOf("Saturne","Jupiter"),
        "Mars/Mercure" to listOf("Mars","Mercure"),
        "Mercure/Mars" to listOf("Mercure","Mars")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("footpredict", Context.MODE_PRIVATE)

        try {
            val scroll = ScrollView(this)
            scroll.setBackgroundColor(C_BG)

            val root = LinearLayout(this)
            root.orientation = LinearLayout.VERTICAL
            root.setPadding(dp(40), dp(50), dp(40), dp(50))
            scroll.addView(root)

            // ═══ EN-TÊTE DÉCORÉ ═══
            val title = TextView(this)
            title.text = "⚽ FootPredict ⚽"
            title.textSize = 26f
            title.setTextColor(C_ACCENT)
            title.gravity = Gravity.CENTER
            title.setTypeface(null, Typeface.BOLD)
            root.addView(title)

            val sub = TextView(this)
            sub.text = "✦ Numerologie Abjad + Etoiles ✦"
            sub.textSize = 12f
            sub.setTextColor(C_GOLD)
            sub.gravity = Gravity.CENTER
            sub.setPadding(0, dp(6), 0, dp(25))
            root.addView(sub)

            // ═══ CARTE SAISIE ═══
            val card = LinearLayout(this)
            card.orientation = LinearLayout.VERTICAL
            card.setBackground(roundedBg(C_CARD, 24))
            card.setPadding(dp(25), dp(25), dp(25), dp(25))

            card.addView(makeLabel("🌟 EQUIPE 1 - DOMICILE"))
            et1 = makeInput("Ex : Suisse")
            card.addView(et1)

            val vs = TextView(this)
            vs.text = "⚔️  VS  ⚔️"
            vs.textSize = 14f
            vs.setTextColor(C_GOLD)
            vs.gravity = Gravity.CENTER
            vs.setPadding(0, dp(14), 0, dp(14))
            card.addView(vs)

            card.addView(makeLabel("🌟 EQUIPE 2 - EXTERIEUR"))
            et2 = makeInput("Ex : Slovenie")
            card.addView(et2)

            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            row.setPadding(0, dp(18), 0, 0)

            val colDate = LinearLayout(this)
            colDate.orientation = LinearLayout.VERTICAL
            colDate.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            colDate.addView(makeLabel("📅 DATE"))
            etDate = makeInput("")
            val cal = Calendar.getInstance()
            etDate.setText(String.format(Locale.US, "%04d-%02d-%02d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)))
            colDate.addView(etDate)
            row.addView(colDate)

            val sp = View(this)
            sp.layoutParams = LinearLayout.LayoutParams(dp(20), 1)
            row.addView(sp)

            val colTime = LinearLayout(this)
            colTime.orientation = LinearLayout.VERTICAL
            colTime.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            colTime.addView(makeLabel("🕐 HEURE"))
            etTime = makeInput("")
            etTime.setText("20:00")
            colTime.addView(etTime)
            row.addView(colTime)
            card.addView(row)

            val btn = Button(this)
            btn.text = "🔮  PREDIRE LE SCORE  🔮"
            btn.textSize = 15f
            btn.setTypeface(null, Typeface.BOLD)
            btn.setBackground(roundedBg(C_ACCENT, 16))
            btn.setTextColor(Color.parseColor("#04140d"))
            val btnLp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            btnLp.topMargin = dp(25)
            btn.layoutParams = btnLp
            btn.setPadding(dp(20), dp(28), dp(20), dp(28))
            btn.setOnClickListener { predict() }
            card.addView(btn)

            root.addView(card)

            // ═══ RÉSULTAT ═══
            val resultCard = LinearLayout(this)
            resultCard.orientation = LinearLayout.VERTICAL
            resultCard.setBackground(roundedBgDecorated(C_CARD, 24))
            resultCard.setPadding(dp(25), dp(25), dp(25), dp(25))
            val rlp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            rlp.topMargin = dp(20)
            resultCard.layoutParams = rlp

            val sepTop = TextView(this)
            sepTop.text = "✦ ✧ ✦ ✧ ✦"
            sepTop.textSize = 14f
            sepTop.setTextColor(C_GOLD)
            sepTop.gravity = Gravity.CENTER
            sepTop.setPadding(0, 0, 0, dp(10))
            resultCard.addView(sepTop)

            tvScore = TextView(this)
            tvScore.text = "0  —  0"
            tvScore.textSize = 52f
            tvScore.setTypeface(null, Typeface.BOLD)
            tvScore.setTextColor(C_ACCENT)
            tvScore.gravity = Gravity.CENTER
            tvScore.setPadding(0, dp(10), 0, dp(10))
            resultCard.addView(tvScore)

            tvVerdict = TextView(this)
            tvVerdict.text = "Entre 2 equipes et clique"
            tvVerdict.textSize = 14f
            tvVerdict.setTextColor(C_ACCENT2)
            tvVerdict.gravity = Gravity.CENTER
            tvVerdict.setPadding(0, 0, 0, dp(18))
            resultCard.addView(tvVerdict)

            val sepMid = TextView(this)
            sepMid.text = "❋ ❋ ❋ ❋ ❋"
            sepMid.textSize = 12f
            sepMid.setTextColor(C_GOLD)
            sepMid.gravity = Gravity.CENTER
            sepMid.setPadding(0, 0, 0, dp(10))
            resultCard.addView(sepMid)

            tvDetail1 = makeDetail()
            resultCard.addView(tvDetail1)
            tvDetail2 = makeDetail()
            resultCard.addView(tvDetail2)

            tvNote = TextView(this)
            tvNote.textSize = 12f
            tvNote.setTextColor(C_GOLD)
            tvNote.setPadding(dp(15), dp(15), dp(15), dp(5))
            resultCard.addView(tvNote)

            val sepBot = TextView(this)
            sepBot.text = "✦ ✧ ✦ ✧ ✦"
            sepBot.textSize = 12f
            sepBot.setTextColor(C_GOLD)
            sepBot.gravity = Gravity.CENTER
            sepBot.setPadding(0, dp(10), 0, 0)
            resultCard.addView(sepBot)

            root.addView(resultCard)

            // ═══ HISTORIQUE ═══
            val histCard = LinearLayout(this)
            histCard.orientation = LinearLayout.VERTICAL
            histCard.setBackground(roundedBg(C_CARD, 24))
            histCard.setPadding(dp(25), dp(22), dp(25), dp(22))
            val hlp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            hlp.topMargin = dp(20)
            histCard.layoutParams = hlp

            val histTitle = TextView(this)
            histTitle.text = "📜  HISTORIQUE  (illimite)"
            histTitle.textSize = 13f
            histTitle.setTextColor(C_GOLD)
            histTitle.setTypeface(null, Typeface.BOLD)
            histTitle.setPadding(0, 0, 0, dp(12))
            histCard.addView(histTitle)

            tvHistory = TextView(this)
            tvHistory.textSize = 12f
            tvHistory.setTextColor(C_TEXT)
            tvHistory.setLineSpacing(dp(3).toFloat(), 1f)
            histCard.addView(tvHistory)

            val clearBtn = Button(this)
            clearBtn.text = "🗑️  Effacer l'historique"
            clearBtn.textSize = 12f
            clearBtn.setBackground(roundedBg(Color.parseColor("#2a1a24"), 12))
            clearBtn.setTextColor(C_RED)
            val clp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            clp.topMargin = dp(15)
            clearBtn.layoutParams = clp
            clearBtn.setPadding(dp(20), dp(15), dp(20), dp(15))
            clearBtn.setOnClickListener {
                prefs.edit().remove("history").apply()
                refreshHistory()
            }
            histCard.addView(clearBtn)

            root.addView(histCard)

            setContentView(scroll)
            refreshHistory()

        } catch (e: Throwable) {
            val tv = TextView(this)
            tv.text = "ERREUR:\n" + e.toString() + "\n\n" +
                      e.stackTrace.take(6).joinToString("\n") { it.toString() }
            tv.setTextColor(C_RED)
            tv.textSize = 12f
            tv.setPadding(dp(30), dp(100), dp(30), dp(30))
            setContentView(tv)
        }
    }

    // ═══ Helpers UI ═══
    private fun roundedBg(color: Int, radius: Int): GradientDrawable {
        val g = GradientDrawable()
        g.setColor(color)
        g.cornerRadius = dp(radius).toFloat()
        g.setStroke(dp(1), C_BORDER)
        return g
    }

    // ✅ Fond décoré pour le résultat (bordure dorée épaisse)
    private fun roundedBgDecorated(color: Int, radius: Int): GradientDrawable {
        val g = GradientDrawable()
        g.setColor(color)
        g.cornerRadius = dp(radius).toFloat()
        g.setStroke(dp(2), C_GOLD)
        return g
    }

    private fun makeLabel(t: String): TextView {
        val tv = TextView(this)
        tv.text = t
        tv.textSize = 10f
        tv.setTextColor(C_MUTED)
        tv.setTypeface(null, Typeface.BOLD)
        tv.setPadding(dp(4), dp(15), 0, dp(6))
        return tv
    }

    private fun makeInput(hint: String): EditText {
        val et = EditText(this)
        et.hint = hint
        et.setTextColor(C_TEXT)
        et.setHintTextColor(Color.parseColor("#5a6884"))
        et.setBackground(roundedBg(C_INPUT, 12))
        et.setPadding(dp(22), dp(26), dp(22), dp(26))
        et.textSize = 15f
        et.setSingleLine(true)
        return et
    }

    private fun makeDetail(): TextView {
        val tv = TextView(this)
        tv.textSize = 12f
        tv.setTextColor(C_TEXT)
        tv.setPadding(dp(18), dp(15), dp(18), dp(15))
        tv.setBackground(roundedBg(C_INPUT, 12))
        tv.setLineSpacing(dp(4).toFloat(), 1f)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.topMargin = dp(8)
        tv.layoutParams = lp
        return tv
    }

    // ═══ Utilitaires ═══
    private fun normalize(s: String): String {
        val sb = StringBuilder()
        for (c in s.uppercase()) {
            when (c) {
                'À','Á','Â','Ã','Ä','Å' -> sb.append('A')
                'È','É','Ê','Ë' -> sb.append('E')
                'Ì','Í','Î','Ï' -> sb.append('I')
                'Ò','Ó','Ô','Õ','Ö' -> sb.append('O')
                'Ù','Ú','Û','Ü' -> sb.append('U')
                'Ç' -> sb.append('C')
                'Ñ' -> sb.append('N')
                'Ý','Ÿ' -> sb.append('Y')
                in 'A'..'Z' -> sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun reduceDigit(n: Int): Int {
        var x = n
        while (x > 9) x = x.toString().map { it - '0' }.sum()
        return x
    }

    private fun fmtAMPM(h: Int): String {
        val hh = ((h % 24) + 24) % 24
        return when {
            hh == 0 -> "12:00 AM"
            hh < 12 -> "$hh:00 AM"
            hh == 12 -> "12:00 PM"
            else -> "${hh - 12}:00 PM"
        }
    }

    private fun inWin(h: Int, s: Int, e: Int): Boolean =
        if (s <= e) h in s until e else (h >= s || h < e)

    private fun distWin(h: Int, s: Int, e: Int): Int {
        if (inWin(h, s, e)) return 0
        val dS = minOf(Math.abs(h - s), 24 - Math.abs(h - s))
        val dE = minOf(Math.abs(h - e), 24 - Math.abs(h - e))
        return minOf(dS, dE)
    }

    private fun elementColor(el: String): Int = when (el) {
        "feu" -> C_FEU
        "air" -> C_AIR
        "terre" -> C_TERRE
        else -> C_EAU
    }

    private data class Info(
        val team: String, val total: Int, val digit: Int,
        val planet: Planet, val starStart: Int, val starEnd: Int,
        val pStart: Int, val pEnd: Int,
        val distance: Int, val inWin: Boolean,
        var score: Int
    )

    // ═══ PRÉDICTION ═══
    private fun predict() {
        try {
            val t1 = et1.text.toString().trim()
            val t2 = et2.text.toString().trim()
            val ds = etDate.text.toString().trim()
            val ts = etTime.text.toString().trim()

            if (t1.isEmpty() || t2.isEmpty()) {
                Toast.makeText(this, "Entre les 2 equipes", Toast.LENGTH_SHORT).show()
                return
            }

            val dp0 = ds.split("-")
            if (dp0.size != 3) {
                Toast.makeText(this, "Date invalide (AAAA-MM-JJ)", Toast.LENGTH_SHORT).show()
                return
            }
            val y = dp0[0].toInt(); val mo = dp0[1].toInt(); val da = dp0[2].toInt()

            val tp = ts.split(":")
            if (tp.size != 2) {
                Toast.makeText(this, "Heure invalide (HH:MM)", Toast.LENGTH_SHORT).show()
                return
            }
            val matchHour = tp[0].toInt()

            // ✅ DÉTERMINISME : cal.clear() + heure fixe à midi
            val cal = Calendar.getInstance()
            cal.clear()
            cal.set(y, mo - 1, da, 12, 0, 0)
            val dow = cal.get(Calendar.DAY_OF_WEEK)
            val dayNum = if (dow == Calendar.SUNDAY) 7 else dow - 1
            val dayPlanet = DAYS[dayNum - 1].second

            val infos = mutableListOf<Info>()
            for (team in listOf(t1, t2)) {
                val up = normalize(team)
                val total = up.mapNotNull { ABJAD[it] }.sum()
                val digit = if (total == 0) 0 else reduceDigit(total)
                val p = PLANETS[digit] ?: Planet("?", "?", "?", "❓", "eau")
                val w = STAR_WINDOWS[p.star] ?: (0 to 0)
                val shift = digit + dayNum
                val ps = (w.first + shift) % 24
                val pe = (w.second + shift) % 24
                val dist = distWin(matchHour, ps, pe)
                var sc = maxOf(0, 5 - dist)
                val iw = inWin(matchHour, ps, pe)
                val bs = if (HARMONY[p.nature]?.contains(dayPlanet) == true) 1 else 0
                sc = minOf(5, sc + (if (iw) 1 else 0) + bs)
                infos.add(Info(team, total, digit, p, w.first, w.second, ps, pe, dist, iw, sc))
            }

            val i1 = infos[0]; val i2 = infos[1]
            var note = ""

            if (i1.planet.star == i2.planet.star && i1.planet.star != "?") {
                if (i1.inWin && i2.inWin) {
                    i1.score = minOf(5, i1.score + 2); i2.score = minOf(5, i2.score + 2)
                    note = "⭐ Meme etoile active pour les 2 - bonus double"
                } else if (i1.inWin) {
                    i1.score = minOf(5, i1.score + 2); i2.score = maxOf(0, i2.score - 1)
                    note = "⭐ Meme etoile, " + i1.team + " seule dans la fenetre"
                } else if (i2.inWin) {
                    i2.score = minOf(5, i2.score + 2); i1.score = maxOf(0, i1.score - 1)
                    note = "⭐ Meme etoile, " + i2.team + " seule dans la fenetre"
                } else {
                    i1.score = 0; i2.score = 0
                    note = "⭐ Meme etoile, aucune dans la fenetre - 0-0"
                }
            } else {
                if (i1.distance > 6 && i2.distance > 6) {
                    i1.score = minOf(1, i1.score); i2.score = minOf(1, i2.score)
                    note = "🌫️ Aucune etoile active - match ferme"
                } else if (i1.distance <= 3 && i2.distance > 6) {
                    i1.score = minOf(5, i1.score + 2); i2.score = 0
                    note = "✨ " + i1.team + " soutenue par son etoile"
                } else if (i2.distance <= 3 && i1.distance > 6) {
                    i2.score = minOf(5, i2.score + 2); i1.score = 0
                    note = "✨ " + i2.team + " soutenue par son etoile"
                } else if (Math.abs(i1.distance - i2.distance) <= 1) {
                    note = "⚖️ Etoiles equidistantes - match serre"
                }
            }

            i1.score = maxOf(0, minOf(5, i1.score))
            i2.score = maxOf(0, minOf(5, i2.score))

            // ✅ Score décoré
            tvScore.text = "${i1.planet.symbol} ${i1.score}  —  ${i2.score} ${i2.planet.symbol}"

            // ✅ Score avec couleur selon élément dominant
            val dominantElement = if (i1.score >= i2.score) i1.planet.element else i2.planet.element
            tvScore.setTextColor(elementColor(dominantElement))

            tvVerdict.text = when {
                i1.score > i2.score -> "🏆 Victoire de " + i1.team
                i2.score > i1.score -> "🏆 Victoire de " + i2.team
                else -> "🤝 Match nul"
            }
            tvDetail1.text = detail(i1)
            tvDetail2.text = detail(i2)
            tvNote.text = note

            saveHistory(i1.team, i2.team, ds, ts, i1.score, i2.score,
                        i1.planet.symbol, i2.planet.symbol)
            refreshHistory()

        } catch (e: Throwable) {
            tvScore.text = "Erreur"
            tvVerdict.text = e.toString()
        }
    }

    // ✅ Détail décoré avec symbole étoile + planète
    private fun detail(i: Info): String {
        val sb = StringBuilder()
        val elemSymbol = when (i.planet.element) {
            "feu" -> "🔥"
            "air" -> "💨"
            "terre" -> "🌍"
            else -> "💧"
        }
        sb.append("${i.planet.symbol} ${i.team} ${elemSymbol}\n")
        sb.append("━━━━━━━━━━━━━━━\n")
        sb.append("🔢 Abjad : ${i.total} ➜ ${i.digit}\n")
        sb.append("🪐 Planete : ${i.planet.name}\n")
        sb.append("⭐ Etoile : ${i.planet.star}\n")
        sb.append("🌌 Fenetre : ${fmtAMPM(i.starStart)} – ${fmtAMPM(i.starEnd)}\n")
        sb.append("🕐 Chance perso : ${fmtAMPM(i.pStart)} – ${fmtAMPM(i.pEnd)}\n")
        sb.append("📏 Ecart match : ${i.distance}h\n")
        sb.append("🎯 Score : ${i.score}")
        return sb.toString()
    }

    // ═══ HISTORIQUE ILLIMITÉ ═══
    private fun saveHistory(t1: String, t2: String, ds: String, ts: String,
                            s1: Int, s2: Int, sym1: String, sym2: String) {
        val now = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(Date())
        val entry = "$now|$t1|$t2|$ds|$ts|$s1|$s2|$sym1|$sym2"
        val existing = prefs.getString("history", "") ?: ""
        val lines = existing.split("\n").filter { it.isNotEmpty() }.toMutableList()

        // Déduplication : même équipes + même date + même heure
        val key = "$t1|$t2|$ds|$ts"
        lines.removeAll { line ->
            val p = line.split("|")
            p.size >= 5 && "${p[1]}|${p[2]}|${p[3]}|${p[4]}" == key
        }
        lines.add(0, entry)

        // ✅ PAS DE LIMITE — on garde TOUT
        prefs.edit().putString("history", lines.joinToString("\n")).apply()
    }

    private fun refreshHistory() {
        val h = prefs.getString("history", "") ?: ""
        if (h.isEmpty()) {
            tvHistory.text = "Aucune prediction enregistree."
            return
        }
        val lines = h.split("\n").filter { it.isNotEmpty() }
        val sb = StringBuilder()
        sb.append("Total : ${lines.size} predictions\n")
        sb.append("━━━━━━━━━━━━━━━\n")
        // Afficher les 20 dernières (mais tout est gardé en mémoire)
        for (line in lines.take(20)) {
            val p = line.split("|")
            if (p.size >= 9) {
                sb.append("🕒 ${p[0]}\n")
                sb.append("${p[7]} ${p[1]}  vs  ${p[2]} ${p[8]}\n")
                sb.append("📅 ${p[3]} à ${p[4]}\n")
                sb.append("🎯 Score : ${p[5]} – ${p[6]}\n")
                sb.append("✦ ✧ ✦ ✧ ✦\n")
            }
        }
        if (lines.size > 20) {
            sb.append("… et ${lines.size - 20} autres predictions")
        }
        tvHistory.text = sb.toString()
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
