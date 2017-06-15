package com.dpt.itnews.list

import com.dpt.itnews.base.BasePresenter
import com.dpt.itnews.base.BaseView
import com.dpt.itnews.data.vo.Article
import com.dpt.itnews.data.vo.News

/**
 * Created by dupengtao on 17/6/8.
 */
interface ListContract {
    interface View : BaseView<Presenter> {
        fun showNews(news: News)
        fun showTopTips(msg: String)
        fun showRefreshing(isShow: Boolean)
        fun openArticle(newId: Int, article: Article?)
    }

    interface Presenter : BasePresenter {

        fun loadRecentList(isFirst: Boolean = true)

        fun jumpArticle(position: Int)

        fun loadNextPage(fistPos: Int, lastPos: Int, itemCount: Int)
    }
}