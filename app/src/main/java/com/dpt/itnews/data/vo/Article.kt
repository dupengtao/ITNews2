package com.dpt.itnews.data.vo

/**
 * Created by dupengtao on 17/6/13.
 */
data class Article(var body : MutableList<ArticleBody> = mutableListOf(), var content: String? = null, var commentCount: Int? = null, var nextNews: Int? = null, var prevNews: Int? = null, var sourceName: String? = null, var submitDate: String? = null, var title: String? = null)