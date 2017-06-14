package com.dpt.itnews.article.presenter

import android.util.Log
import com.dpt.itnews.article.ArticleContract
import com.dpt.itnews.data.po.NewsItem
import com.dpt.itnews.data.source.NewsRepository
import com.dpt.itnews.data.vo.Article
import com.dpt.itnews.data.vo.ArticleBody
import com.dpt.itnews.data.vo.ArticleConstant
import io.reactivex.android.schedulers.AndroidSchedulers
import org.jsoup.Jsoup
import java.lang.StringBuilder

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
                    val article = Article()
                    val bodyFragment = Jsoup.parseBodyFragment(newsItem.content)
                    val elements = bodyFragment.body().children().select("*")
                    for (element in elements) {
                        if (element.`is`("p")) {
                            val text = element.text()
                            when {
                                element.children().`is`("strong") -> {
                                    article.body.add(ArticleBody(ArticleConstant.TEXT_STRONG_TYPE, text = text))
                                }
                                element.children().`is`("img") -> {
                                    val img = element.child(0)
                                    if (img != null) {
                                        val sb = StringBuilder(img.attr("src"))
                                        sb.insert(0, "https:")
                                        article.body.add(ArticleBody(ArticleConstant.IMG_TYPE, url = sb.toString()))
                                    }
                                }
                                else -> {
                                    val sb = StringBuilder(text)
                                    sb.insert(0, "        ")
                                    article.body.add(ArticleBody(ArticleConstant.TEXT_TYPE, text = sb.toString()))
                                }
                            }
                        } else if (element.`is`("li")) {
                            val sb = StringBuffer(element.text())
                            sb.insert(0, "• ")
                            article.body.add(ArticleBody(ArticleConstant.TEXT_LIST_ITEM, text = sb.toString()))
                        }
                    }

                    article.commentCount = newsItem.commentCount
                    article.content = newsItem.content
                    article.nextNews = newsItem.nextNews
                    article.prevNews = newsItem.prevNews
                    article.sourceName = newsItem.sourceName
                    article.submitDate = newsItem.submitDate
                    article.title = newsItem.title

                    article
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