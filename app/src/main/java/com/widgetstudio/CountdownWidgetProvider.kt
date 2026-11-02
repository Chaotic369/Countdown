package com.widgetstudio

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

class CountdownWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, mgr: AppWidgetManager, ids: IntArray) {
        ids.forEach { mgr.updateAppWidget(it, WidgetRenderer.buildCountdown(context)) }
    }

    override fun onEnabled(context: Context) {
        WidgetUpdateWorker.schedule(context)
    }
}
