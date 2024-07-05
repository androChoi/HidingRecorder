package com.ando.hidingrecorder

import android.content.Context

class Preference(private val context: Context) {
    private val prefs =
        context.getSharedPreferences("hiding_recorder", Context.MODE_PRIVATE)

    var playCount : Int
        get() = prefs.getInt("playCount", 0)
        set(value) = prefs.edit().putInt("playCount", value).apply()
}