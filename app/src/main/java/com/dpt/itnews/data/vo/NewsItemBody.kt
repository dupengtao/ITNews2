package com.dpt.itnews.data.vo

import android.icu.text.RelativeDateTimeFormatter
import com.dpt.news.base.ui.util.RelativeDateFormat
import java.text.SimpleDateFormat

/**
 * Created by dupengtao on 17/6/8.
 */
data class NewsItemBody(val id: Int, val title: String?, val summary: String?, val published: String?, val topicIcon: String?, val sourceName: String?, val publishedDate: String?, val publishedTime: String?, val view: String?) {

    override fun equals(other: Any?): Boolean {

        if (this == other) return true

        if (other == null || this.javaClass != other.javaClass) return false

        val that = other as NewsItemBody

        return id == that.id
    }

    override fun hashCode(): Int {
        return id
    }

    fun formateDate():String{
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = format.parse("$publishedDate $publishedTime")
        return RelativeDateFormat.format(date)
    }
}