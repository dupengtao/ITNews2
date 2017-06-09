package com.dpt.itnews.api.cnBlog

import com.dpt.itnews.data.po.NewsList
import io.reactivex.Observable

/**
 * Created by dupengtao on 17/6/7.
 */
interface CnBlogNewsApis {

    @com.dpt.itnews.api.converter.QualifiedTypeConverterFactory.Xml
    @retrofit2.http.GET("recent/paged/{index}/{size}")
    fun recentList(@retrofit2.http.Path("index") index: Int, @retrofit2.http.Path("size") size: Int): Observable<NewsList>


}