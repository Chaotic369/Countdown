package com.widgetstudio

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.widget.RemoteViews
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object WidgetRenderer {

    fun updateAll(ctx: Context) {
        val mgr = AppWidgetManager.getInstance(ctx)
        val cd = mgr.getAppWidgetIds(ComponentName(ctx, CountdownWidgetProvider::class.java))
        if (cd.isNotEmpty()) mgr.updateAppWidget(cd, buildCountdown(ctx))
        val tx = mgr.getAppWidgetIds(ComponentName(ctx, TextWidgetProvider::class.java))
        if (tx.isNotEmpty()) mgr.updateAppWidget(tx, buildText(ctx))
    }

    private fun cfg(ctx: Context, type: String): JSONObject =
        try { JSONObject(WidgetStore.load(ctx, type).ifEmpty { "{}" }) } catch (e: Exception) { JSONObject() }

    private fun color(s: String?, def: Int): Int =
        try { Color.parseColor(s) } catch (e: Exception) { def }

    private fun style(ctx: Context, rv: RemoteViews, c: JSONObject) {
        val base = color(c.optString("bg", "#000000"), Color.BLACK)
        val alpha = (c.optInt("bgOpacity", 30).coerceIn(0, 100) * 255 / 100)
        val bg = if (c.optBoolean("bgOn", true)) (alpha shl 24) or (base and 0xFFFFFF) else Color.TRANSPARENT
        rv.setInt(R.id.root, "setBackgroundColor", bg)
        val d = ctx.resources.displayMetrics.density
        val p = (c.optInt("pad", 16) * d).toInt()
        rv.setViewPadding(R.id.root, p, p, p, p)
    }

    fun buildText(ctx: Context): RemoteViews {
        val c = cfg(ctx, "text")
        val rv = RemoteViews(ctx.packageName, R.layout.widget_text)
        style(ctx, rv, c)
        rv.setTextViewText(R.id.text, c.optString("text", "Widget Studio"))
        rv.setTextColor(R.id.text, color(c.optString("color", "#ffffff"), Color.WHITE))
        rv.setTextViewTextSize(R.id.text, TypedValue.COMPLEX_UNIT_SP, c.optInt("size", 34).coerceIn(10, 60).toFloat() * 0.7f)
        return rv
    }

    fun buildCountdown(ctx: Context): RemoteViews {
        val c = cfg(ctx, "count")
        val rv = RemoteViews(ctx.packageName, R.layout.widget_countdown)
        style(ctx, rv, c)
        val col = color(c.optString("color", "#ffffff"), Color.WHITE)
        val size = c.optInt("size", 44).coerceIn(10, 80).toFloat()

        val showTitle = c.optBoolean("showTitle", true) && c.optString("label").isNotEmpty()
        rv.setViewVisibility(R.id.title, if (showTitle) android.view.View.VISIBLE else android.view.View.GONE)
        rv.setTextViewText(R.id.title, c.optString("label"))
        rv.setTextColor(R.id.title, col)
        rv.setTextViewTextSize(R.id.title, TypedValue.COMPLEX_UNIT_SP, (size * 0.32f).coerceAtLeast(11f))

        rv.setTextViewText(R.id.time, countdownText(c))
        rv.setTextColor(R.id.time, col)
        rv.setTextViewTextSize(R.id.time, TypedValue.COMPLEX_UNIT_SP, size * 0.5f)
        return rv
    }

    private fun countdownText(c: JSONObject): String {
        val u = c.optJSONObject("units") ?: JSONObject().put("days", true).put("hours", true).put("minutes", true)
        val labels = c.optBoolean("showLabels", true)
        val sep = if (c.optBoolean("seps", true)) " : " else " "
        val target = try { LocalDateTime.parse(c.optString("target")) } catch (e: Exception) { null }
        var cur = LocalDateTime.now()
        val order = listOf(
            "years" to ChronoUnit.YEARS, "months" to ChronoUnit.MONTHS, "weeks" to ChronoUnit.WEEKS,
            "days" to ChronoUnit.DAYS, "hours" to ChronoUnit.HOURS, "minutes" to ChronoUnit.MINUTES,
            "seconds" to ChronoUnit.SECONDS
        )
        val parts = ArrayList<String>()
        for ((name, unit) in order) {
            if (!u.optBoolean(name, false)) continue
            var v = 0L
            if (target != null && target.isAfter(cur)) {
                v = unit.between(cur, target)
                cur = cur.plus(v, unit)
            }
            val num = v.toString().padStart(2, '0')
            parts.add(if (labels) num + name[0].uppercaseChar() else num)
        }
        return if (parts.isEmpty()) "--" else parts.joinToString(sep)
    }

}
