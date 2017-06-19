package com.dpt.itnews.base

import android.app.Application
import android.content.Context
import com.dpt.itnews.base.analytics.Analytics
import com.google.android.gms.analytics.ExceptionParser
import com.google.android.gms.analytics.ExceptionReporter
import com.google.android.gms.analytics.Tracker


/**
 * Created by dupengtao on 17/6/16.
 */
class NewsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val tracker = Analytics.getDefaultTracker(this)
        if (tracker != null) {
            initExceptionHandler(tracker)
        }
    }

    private fun initExceptionHandler(tracker: Tracker) {
        val customReportHandler = AnalyticsExceptionReporter(
                tracker,
                Thread.getDefaultUncaughtExceptionHandler(),
                this)
        Thread.setDefaultUncaughtExceptionHandler(customReportHandler)
    }


    inner class AnalyticsExceptionParser() : ExceptionParser {

        override fun getDescription(p0: String?, t: Throwable?): String {
            if (t == null)
                return ""
            val sb = StringBuilder()
            sb.append(t.toString())
            sb.append("\n")
            for (element in t.stackTrace) {
                sb.append(element.toString())
                sb.append("\n")
            }
            return sb.toString()
        }
    }

    //Tracker var1, UncaughtExceptionHandler var2, Context var3
    inner class AnalyticsExceptionReporter(val v1: Tracker, val v2: Thread.UncaughtExceptionHandler, val v3: Context) : ExceptionReporter(v1, v2, v3) {
        init {
            exceptionParser = AnalyticsExceptionParser()
        }
        override fun uncaughtException(p0: Thread?, p1: Throwable?) {
            super.uncaughtException(p0, p1)
        }
    }
}