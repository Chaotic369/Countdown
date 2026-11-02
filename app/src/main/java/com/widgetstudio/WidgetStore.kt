package com.widgetstudio

import android.content.Context

object WidgetStore {
    private const val PREFS = "widget_studio"

    fun save(ctx: Context, type: String, json: String) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("cfg_$type", json).apply()
    }

    fun load(ctx: Context, type: String): String =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("cfg_$type", "") ?: ""
}
