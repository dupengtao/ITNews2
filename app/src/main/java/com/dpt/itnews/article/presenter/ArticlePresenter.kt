package com.dpt.itnews.article.presenter

import android.util.Log
import com.dpt.itnews.article.ArticleContract
import com.dpt.itnews.base.util.mapArticle
import com.dpt.itnews.data.po.NewsItem
import com.dpt.itnews.data.source.NewsRepository
import com.dpt.itnews.data.vo.Article
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by dupengtao on 17/6/13.
 */
class ArticlePresenter(val view: ArticleContract.View, val repository: NewsRepository = NewsRepository.get()) : ArticleContract.Presenter {

    private var curArticle: Article = Article()

    init {
        view.setPresenter(this)
    }


    override fun loadArticle(newsId: Int, article: Article?) {

        if(article!=null){
            curArticle = article
            view.show(curArticle)
            return
        }

        repository.getNewsItem(newsId)
                .map { newsItem: NewsItem ->
                    newsItem.mapArticle()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    curArticle = it
                    view.show(it)
                })
    }



    override fun itemClick(position: Int) {
        Log.e("dpt","position = ${position}")
        val bodyItem = curArticle.body[position-1]
        Log.e("dpt",bodyItem.toString())
        if (bodyItem.url != null) {
            view.showPhoto(bodyItem.url)
        }
    }

    override fun subscribe() {

    }

    override fun unSubscribe() {
    }
}