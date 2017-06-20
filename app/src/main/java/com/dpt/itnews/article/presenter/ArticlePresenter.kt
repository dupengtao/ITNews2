package com.dpt.itnews.article.presenter

import android.content.Context
import android.content.Intent
import com.dpt.itnews.article.ArticleContract
import com.dpt.itnews.base.util.mapArticle
import com.dpt.itnews.data.po.NewsItem
import com.dpt.itnews.data.source.NewsRepository
import com.dpt.itnews.data.vo.Article
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable


/**
 * Created by dupengtao on 17/6/13.
 */
class ArticlePresenter(val view: ArticleContract.View, val repository: NewsRepository = NewsRepository.get()) : ArticleContract.Presenter {

    private val disposable = CompositeDisposable()
    private var curArticle: Article = Article()
    private var curNewsId: Int = 0


    init {
        view.setPresenter(this)
    }

    override fun loadArticle(newsId: Int, article: Article?) {

        if (article != null) {
            curArticle = article
            curNewsId = newsId
            view.show(curArticle)
            return
        }

        val subscribe = repository.getNewsItem(newsId)
                .map { newsItem: NewsItem ->
                    newsItem.mapArticle()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    curNewsId = newsId
                    curArticle = it
                    view.show(it)
                })
        disposable.addAll(subscribe)
    }


    override fun itemClick(position: Int) {
        val bodyItem = curArticle.body[position - 1]
        if (bodyItem.url != null) {
            view.showPhoto(bodyItem.url)
        }
    }

    override fun subscribe() {

    }

    override fun unSubscribe() {
        disposable.clear()
    }

    override fun shareArticle(context:Context) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, "「${curArticle.title}」http://news.cnblogs.com/n/$curNewsId")
        shareIntent.type = "text/plain"
        context.startActivity(Intent.createChooser(shareIntent, "分享到"))
    }
}