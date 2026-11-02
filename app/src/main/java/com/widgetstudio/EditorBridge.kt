package com.widgetstudio

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface

class EditorBridge(private val ctx: Context) {
    private val main = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun save(type: String, json: String) {
        WidgetStore.save(ctx, type, json)
        WidgetRenderer.updateAll(ctx)
    }

    @JavascriptInterface
    fun load(type: String): String = WidgetStore.load(ctx, type)

    @JavascriptInterface
    fun pin(type: String) {
        main.post {
            val cls = if (type == "count") CountdownWidgetProvider::class.java else TextWidgetProvider::class.java
            val mgr = AppWidgetManager.getInstance(ctx)
            if (mgr.isRequestPinAppWidgetSupported) {
                mgr.requestPinAppWidget(ComponentName(ctx, cls), null, null)
            }
        }
    }

}
