package com.widgetstudio

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

class TextWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, mgr: AppWidgetManager, ids: IntArray) {
        ids.forEach { mgr.updateAppWidget(it, WidgetRenderer.buildText(context)) }
    }

    override fun onEnabled(context: Context) {
        WidgetUpdateWorker.schedule(context)
    }
}
