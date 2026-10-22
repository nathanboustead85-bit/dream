package com.dreamstream.app

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {

    private val prefs by lazy {
        getSharedPreferences("dreamstream", Context.MODE_PRIVATE)
    }

    private val bg = Color.rgb(3, 7, 13)
    private val panel = Color.rgb(10, 20, 34)
    private val cyan = Color.rgb(0, 221, 245)
    private val muted = Color.rgb(139, 167, 194)

    private lateinit var root: LinearLayout

    private var server = ""
    private var user = ""
    private var pass = ""
    private var auto = true

    private val channels = listOf(
        "BBC One",
        "BBC Two",
        "ITV1",
        "Channel 4",
        "Sky Sports News",
        "TNT Sports 1",
        "BT Sport",
        "ESPN"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        server = prefs.getString("server", "http://vpn-esxtrapro.info:80") ?: ""
        user = prefs.getString("username", "") ?: ""
        auto = prefs.getBoolean("auto", true)

        login()
    }

    private fun page(title: String, back: Boolean = true): LinearLayout {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
            setBackgroundColor(bg)
        }

        val scrollView = ScrollView(this)
        scrollView.addView(root)

        setContentView(scrollView)

        if (back) {
            button("‹ $title", false) {
                home()
            }
        } else {
            root.addView(text(title, 24, Color.WHITE, true))
        }

        return root
    }

    private fun text(
        value: String,
        size: Int,
        color: Int = Color.WHITE,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size.toFloat()
            setTextColor(color)

            if (bold) {
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }

            setPadding(0, dp(6), 0, dp(6))
        }
    }

    private fun button(
        label: String,
        primary: Boolean = true,
        action: () -> Unit
    ) {
        val button = Button(this).apply {
            text = label
            textSize = 14f
            setTextColor(if (primary) bg else Color.WHITE)

            backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    if (primary) cyan else panel
                )

            setOnClickListener { action() }
        }

        root.addView(button, lp(10))
    }

    private fun field(
        hint: String,
        value: String,
        type: Int = InputType.TYPE_CLASS_TEXT
    ): EditText {

        return EditText(this).apply {
            this.hint = hint
            setText(value)

            inputType = type

            // FIXED
            setSingleLine(true)

            textSize = 15f
            setTextColor(Color.WHITE)
            setHintTextColor(muted)

            setPadding(
                dp(12),
                dp(8),
                dp(12),
                dp(8)
            )

            background =
                android.graphics.drawable.GradientDrawable().apply {
                    setColor(panel)
                    cornerRadius = dp(10).toFloat()
                    setStroke(dp(1), Color.rgb(36, 67, 94))
                }
        }
    }

    private fun login() {
        page("", false)

        root.addView(text("▶", 48, cyan, true).apply {
            gravity = Gravity.CENTER
        })

        root.addView(text("DreamStream", 28, Color.WHITE, true).apply {
            gravity = Gravity.CENTER
        })

        root.addView(text(
            "U L T R A • YOUR WORLD. STREAMED.",
            11,
            cyan
        ).apply {
            gravity = Gravity.CENTER
        })

        val serverField = field(
            "Portal (Server Address)",
            server,
            InputType.TYPE_TEXT_VARIATION_URI
        )

        val userField = field("Username", user)

        val passField = field(
            "Password",
            pass,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        )

        root.addView(serverField, lp(10))
        root.addView(userField, lp(10))
        root.addView(passField, lp(10))

        val rememberBox = CheckBox(this).apply {
            text = "Remember server & username"
            isChecked = auto
            setTextColor(muted)
        }

        root.addView(rememberBox, lp(8))

        button("CONTINUE") {

            server = serverField.text.toString().trim()
            user = userField.text.toString().trim()
            pass = passField.text.toString()

            auto = rememberBox.isChecked

            prefs.edit()
                .putString("server", server)
                .putString("username", user)
                .putBoolean("auto", auto)
                .apply()

            Toast.makeText(
                this,
                "Demo mode: provider authentication is not connected",
                Toast.LENGTH_LONG
            ).show()

            home()
        }
    }

    private fun home() {
        page("DreamStream Ultra", false)

        button("Live TV", false) {
            live()
        }

        button("Movies", false) {
            library("Movies")
        }

        button("Series", false) {
            library("Series")
        }

        button("Favourites", false) {
            library("Favourites")
        }

        button("Settings", false) {
            settings()
        }
    }

    private fun live() {
        page("Live TV")

        channels.forEach { channel ->
            button(channel, false) {
                player(channel)
            }
        }
    }

    private fun library(name: String) {
        page(name)

        val items =
            if (name == "Movies") {
                listOf(
                    "Action",
                    "Adventure",
                    "Comedy",
                    "Drama"
                )
            } else {
                listOf(
                    "Popular",
                    "Recently Added",
                    "Documentaries"
                )
            }

        items.forEach {
            button(it, false) {
                player(it)
            }
        }
    }

    private fun player(name: String) {
        page(name)

        root.addView(
            text(
                "PLAYER PREVIEW\n\nNo stream URL loaded",
                18,
                cyan,
                true
            )
        )
    }

    private fun settings() {
        page("Settings")

        val cb = CheckBox(this).apply {
            text = "Remember server & username"
            isChecked = auto
            setTextColor(Color.WHITE)

            setOnCheckedChangeListener { _, checked ->
                auto = checked

                prefs.edit()
                    .putBoolean("auto", checked)
                    .apply()
            }
        }

        root.addView(cb, lp(8))

        button("Change Server", false) {
            login()
        }
    }

    private fun lp(top: Int = 0): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = dp(top)
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
