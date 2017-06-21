package com.dpt.itnews.data.source

import com.dpt.itnews.api.Retrofits
import com.dpt.itnews.data.po.NewsItem
import com.dpt.itnews.data.po.NewsList
import com.dpt.itnews.data.po.VersionUpgradeInfo
import io.reactivex.Observable

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

    fun getNewsList(index: Int, size: Int = 20) = Retrofits.cnBlogNewsApi().recentList(index, size)

    fun getNewsItem(newsId: Int): Observable<NewsItem> = Retrofits.cnBlogNewsApi().item(newsId)


    fun checkNewVersion() =  Retrofits.cnBlogNewsApi().checkVersion("https://api.leancloud.cn/1.1/classes/version?order=-createdAt&limit=1")

}