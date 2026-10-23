package com.dreamstream.app

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tv = TextView(this)
        tv.text = "DreamStream Ultra"
        tv.textSize = 24f
        tv.setTextColor(Color.WHITE)

        setContentView(tv)
    }
}
