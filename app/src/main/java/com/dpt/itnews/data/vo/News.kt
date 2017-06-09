package com.dpt.itnews.data.vo

import java.util.*

/**
 * Created by dupengtao on 17/6/8.
 */
class News {
    val newsList : MutableList<NewsItemBody> = LinkedList()
    override fun toString(): String {
        return "News(newsList=$newsList)"
    }

}