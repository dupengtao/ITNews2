package com.dpt.itnews.api

import com.dpt.itnews.api.cnBlog.CnBlogNewsApis2
import com.dpt.itnews.api.cnBlog.CnBlogNewsClient

/**
 * Created by dupengtao on 17/6/8.
 */
object Retrofits {

    private val SINGLETON = CnBlogNewsClient().cnBlogNewsApis

    fun cnBlogNewsApi(): CnBlogNewsApis2 {
        return SINGLETON
    }

}