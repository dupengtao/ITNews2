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
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.lang.StringBuilder
import java.text.SimpleDateFormat

/**
 * Created by dupengtao on 17/6/8.
 */
class ListPresenter(val view: ListContract.View, val newsRepository: NewsRepository = NewsRepository.get()) : ListContract.Presenter {

    private val disposable = CompositeDisposable()
    private var curNews: News = News()
    private var isLoadingNextPager = false
    private var curPageIndex = 1

    init {
        view.setPresenter(this)
    }

    override fun subscribe() {

    }

    override fun unSubscribe() {
        disposable.clear()
    }

    override fun loadNextPage(fistPos: Int, lastPos: Int, itemCount: Int) {
        if (itemCount - lastPos > 15) return

        if (isLoadingNextPager) return

        isLoadingNextPager = true

        val subscribe = Observable.zip(Observable.just(curNews), loadNews(curPageIndex + 1).toObservable(), BiFunction<News, News, News> { oldNews, news ->
            oldNews.newsList.addAll(news.newsList)
            oldNews
        }).observeOn(AndroidSchedulers.mainThread()).doFinally {
            isLoadingNextPager = false
        }.subscribe({ news ->
            curNews = news
            view.showNews(news)
            curPageIndex += 1
        }, {
            it.printStackTrace()
        })
        disposable.add(subscribe)

    }

    override fun loadRecentList(isFirst: Boolean) {

        if (isFirst) {
            val subscribe = loadNews(size = 50).observeOn(AndroidSchedulers.mainThread()).doFinally {
                Log.e(ListPresenter::class.java.simpleName, "do Finally")
            }.subscribe { news: News ->
                Log.e(ListPresenter::class.java.simpleName, "news list size = ${news.newsList.size}")
                curNews = news
                view.showNews(news)
                view.showTopTips("首页已更新")
                curPageIndex = 1
            }
            disposable.addAll(subscribe)
        } else {
            val subscribe = loadNews().toObservable().concatMap {
                Observable.fromIterable(it.newsList)
            }.filter {
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val listTopDate = format.parse("${curNews.newsList.first().publishedDate} ${curNews.newsList.first().publishedTime}")
                val curDate = format.parse("${it.publishedDate} ${it.publishedTime}")
                curDate >listTopDate
            }.toList().observeOn(AndroidSchedulers.mainThread()).doOnEvent { increasedItems, _ ->
                Log.e(ListPresenter::class.java.simpleName, "show top tips ${increasedItems.size}")

                view.showTopTips(if(increasedItems.size == 0) "已经是最新内容" else "${increasedItems.size}条新内容")
            }.observeOn(Schedulers.io()).zipWith(Single.just(curNews), BiFunction<List<NewsItemBody>, News, News> { increasedItems, curNews ->
                if (increasedItems.isEmpty()) curNews
                curNews.newsList.addAll(0, increasedItems)
                curNews
            }).observeOn(AndroidSchedulers.mainThread()).doFinally {
                view.showRefreshing(false)
            }.subscribe({
                view.showNews(it)
            }, {
                it.printStackTrace()
            })
            disposable.add(subscribe)
        }


    }

    override fun jumpArticle(position: Int) {
        with(curNews.newsList[position]) {
            Log.e(ListPresenter::class.java.simpleName, "click item pos = $position article name = $title")
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

    private fun loadNews(index: Int = 1, size: Int = 30): Single<News> {
        return newsRepository.getNewsList(index, size).concatMap { t -> Observable.fromIterable(t.newsEntryList) }.map { newsEntry: NewsEntry ->
            transformNewsEntry(newsEntry)
        }.toList().map { t ->
            val n = News()
            n.newsList.addAll(t)
            n
        }
    }
}