package com.dpt.itnews.list.presenter

import android.util.Log
import com.dpt.itnews.data.po.NewsEntry
import com.dpt.itnews.data.source.NewsRepository
import com.dpt.itnews.data.vo.News
import com.dpt.itnews.data.vo.NewsItemBody
import com.dpt.itnews.list.ListContract
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.lang.StringBuilder

/**
 * Created by dupengtao on 17/6/8.
 */
class ListPresenter(val view: ListContract.View, val newsRepository: NewsRepository = NewsRepository.get()) : ListContract.Presenter {

    private val disposable = CompositeDisposable()
    private var curNews: News = News()

    init {
        view.setPresenter(this)
    }

    override fun subscribe() {

    }

    override fun unSubscribe() {
        disposable.clear()
    }

    override fun loadRecentList(isFirst: Boolean) {

        isFirst.let {
            val subscribe = loadNews()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally {
                        Log.e(ListPresenter::class.java.simpleName, "do Finally")
                    }
                    .subscribe {
                        news: News ->
                        Log.e(ListPresenter::class.java.simpleName, news.toString())
                        curNews = news
                        view.showNews(news)
                    }
            disposable.addAll(subscribe)
        }

    }

    override fun jumpArticle(position: Int) {
        with(curNews.newsList[position]){
            Log.e(ListPresenter::class.java.simpleName,"click item pos = $position article name = $title")
        }
    }

    private fun transformNewsEntry(newsEntry: NewsEntry): NewsItemBody {

        val published = newsEntry.published
        var publishedData: String? = null
        var publishedTime: String? = null
        if (published != null) {
            val ts = published.split("T")
            if (ts.size == 2) {
                publishedData = ts[0]
                publishedTime = ts[1].split("\\+")[0]
            }
        }

        //        var iconUrl = newsEntry.topicIcon
        var iconUrl = if (newsEntry.topicIcon != null) {
            val split = newsEntry.topicIcon?.split("///")
            if (split?.size == 2) {
                val sb = StringBuilder("http://")
                sb.append(split[1])
                sb.toString()
            } else {
                null
            }
        } else {
            null
        }

        val sb = StringBuilder(newsEntry.summary)
        sb.insert(0, "        ")
        val summary = sb.toString()
        return NewsItemBody(newsEntry.id, newsEntry.title, summary, published, iconUrl, newsEntry.sourceName, publishedData, publishedTime, newsEntry.views)
    }

    private fun loadNews(index: Int = 1): Single<News> {
        return newsRepository.getNewsList(index)
                .concatMap { t -> Observable.fromIterable(t.newsEntryList) }
                .map {
                    newsEntry: NewsEntry ->
                    transformNewsEntry(newsEntry)
                }
                .toList()
                .map { t ->
                    val n = News()
                    n.newsList.addAll(t)
                    n
                }


    }

}