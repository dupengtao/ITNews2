package com.dpt.itnews.base.analytics

import android.app.Application
import com.dpt.itnews.R
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker

/**
 * Created by dupengtao on 17/6/16.
 */
object Analytics {
    var tracker: Tracker? = null

    fun getDefaultTracker(app: Application): Tracker? {
        if (tracker == null) {
            val analytics = GoogleAnalytics.getInstance(app)
            tracker = analytics.newTracker(R.xml.app_tracker);
        }
        return tracker
    }
}