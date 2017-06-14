package com.dpt.itnews.article

import com.dpt.itnews.base.BasePresenter
import com.dpt.itnews.base.BaseView
import com.dpt.itnews.data.vo.Article

/**
 * Created by dupengtao on 17/6/13.
 */
interface ArticleContract {

    interface View : BaseView<Presenter> {
        fun show(article: Article)
    }

    interface Presenter : BasePresenter {

        fun loadArticle(newsId: Int)
    }
}