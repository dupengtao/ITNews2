package com.dpt.itnews.article.presenter

import com.dpt.itnews.article.ArticleContract
import com.dpt.itnews.base.util.mapArticle
import com.dpt.itnews.data.po.NewsItem
import com.dpt.itnews.data.source.NewsRepository
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by dupengtao on 17/6/13.
 */
class ArticlePresenter(val view: ArticleContract.View, val repository: NewsRepository = NewsRepository.get()) : ArticleContract.Presenter {

    init {
        view.setPresenter(this)
    }

    override fun loadArticle(newsId: Int) {
        repository.getNewsItem(newsId)
                .map { newsItem: NewsItem ->
                    newsItem.mapArticle()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.show(it)
                })
    }

    override fun subscribe() {

    }

    override fun unSubscribe() {
    }
}