package com.dpt.itnews.list.presenter

import android.util.Log
import com.dpt.itnews.base.util.mapArticle
import com.dpt.itnews.data.po.NewsEntry
import com.dpt.itnews.data.po.NewsItem
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
    private var times = 0
    private var loadNextPageError: Boolean =false

    init {
        view.setPresenter(this)
    }

    override fun subscribe() {

    }

    override fun unSubscribe() {
        disposable.clear()
    }

    override fun loadNextPage(fistPos: Int, lastPos: Int, itemCount: Int) {

        if (itemCount == 0) return

        if(loadNextPageError) return

        if (itemCount - lastPos > 8) return

        if (isLoadingNextPager) return

        isLoadingNextPager = true

        Log.e(ListPresenter::class.java.simpleName, "loadNextPage index = ${curPageIndex + 1}")

        val subscribe = Observable.zip(Observable.just(curNews), loadNews(curPageIndex + 1).toObservable(), BiFunction<News, News, News> { oldNews, news ->
            oldNews.newsList.addAll(news.newsList)
            oldNews
        }).observeOn(AndroidSchedulers.mainThread()).doFinally {
            isLoadingNextPager = false
        }.subscribe({ news ->
            curNews = news
            view.showNews(news)
            view.refreshProcess(0)
            curPageIndex += 1
        }, {
            it.printStackTrace()
            view.showTopTips("似乎出现了问题...\n无法加载下一页数据了\n下拉刷新试试")
            loadNextPageError = true
        })
        disposable.add(subscribe)

    }

    override fun loadRecentList(isFirst: Boolean) {

        if (isFirst) {
            val subscribe = loadNews(size = 15)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally {
                        view.showRefreshing(false)
                    }
                    .subscribe({ news: News ->
                        Log.e(ListPresenter::class.java.simpleName, "news list size = ${news.newsList.size}")
                        curNews = news
                        view.showNews(news)
                        view.showTopTips("首页已更新")
                        view.refreshProcess(0)
                        curPageIndex = 1
                        loadNextPageError = false
                    }, {
                        curPageIndex = 1
                        view.showError()
                    })
            disposable.addAll(subscribe)
        } else {
            val subscribe = loadNews().toObservable()
                    .concatMap {
                        Observable.fromIterable(it.newsList)
                    }
                    .filter {
                        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val listTopDate = format.parse("${curNews.newsList.first().publishedDate} ${curNews.newsList.first().publishedTime}")
                        val curDate = format.parse("${it.publishedDate} ${it.publishedTime}")
                        curDate > listTopDate
                    }
                    .toList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnEvent { increasedItems, _ ->
                        Log.e(ListPresenter::class.java.simpleName, "show top tips ${increasedItems.size}")
                        view.showTopTips(if (increasedItems.size == 0) "已经是最新内容" else "${increasedItems.size}条新内容")
                    }
                    .observeOn(Schedulers.io())
                    .zipWith(Single.just(curNews), BiFunction<List<NewsItemBody>, News, News> { increasedItems, curNews ->
                        if (increasedItems.isEmpty()) {
                            curNews
                        } else {
                            curNews.newsList.addAll(0, increasedItems)
                            curNews
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally {
                        view.showRefreshing(false)
                    }
                    .subscribe({
                        view.refreshProcess(0)
                        view.showNews(it)
                        loadNextPageError = false
                    }, {
                        view.showTopTips("似乎出现了问题...\n无法加载最新数据了\n下拉刷新试试")
                        it.printStackTrace()
                    })
            disposable.add(subscribe)
        }


    }

    override fun jumpArticle(position: Int) {
        val newsBody = curNews.newsList[position]
        view.openArticle(newsBody.id, newsBody.article)
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

    private fun loadNews(index: Int = 1, size: Int = 15): Single<News> {
        times = 0
        return newsRepository.getNewsList(index, size)
                .concatMap { t -> Observable.fromIterable(t.newsEntryList) }
                .map { newsEntry: NewsEntry ->
                    transformNewsEntry(newsEntry)
                }
                .concatMap {
                    newsRepository.getNewsItem(it.id)
                            .zipWith(Observable.just(it), BiFunction<NewsItem, NewsItemBody, NewsItemBody> { t1, t2 ->
                                t2.article = t1.mapArticle()
                                t2
                            })
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    view.refreshProcess(++times)
                }
                .observeOn(Schedulers.io())
                .toList()
                .map { t ->
                    val n = News()
                    n.newsList.addAll(t)
                    n
                }
    }

    private fun loadNews2(index: Int = 1, size: Int = 20) {
        newsRepository.getNewsList(index, size)
                .concatMap { t -> Observable.fromIterable(t.newsEntryList) }
                .map { newsEntry: NewsEntry ->
                    transformNewsEntry(newsEntry)
                }
                .concatMap {
                    newsRepository.getNewsItem(it.id)
                            .zipWith(Observable.just(it), BiFunction<NewsItem, NewsItemBody, NewsItemBody> { t1, t2 ->
                                t2.article = t1.mapArticle()
                                t2
                            })
                }


    }
}