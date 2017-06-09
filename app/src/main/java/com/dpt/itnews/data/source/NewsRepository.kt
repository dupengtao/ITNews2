package com.dpt.itnews.data.source

import com.dpt.itnews.api.Retrofits

/**
 * Created by dupengtao on 17/6/8.
 */
class NewsRepository private constructor() {

    companion object {
        fun get(): NewsRepository {
            return Inner.instance
        }
    }

    private object Inner {
        val instance = NewsRepository()
    }

    fun getNewsList(index: Int, size: Int = 30) = Retrofits.cnBlogNewsApi().recentList(index, size)
}