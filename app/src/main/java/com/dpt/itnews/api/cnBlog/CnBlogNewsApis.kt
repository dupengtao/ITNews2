package com.dpt.itnews.api.cnBlog

import com.dpt.itnews.data.po.NewsItem
import com.dpt.itnews.data.po.NewsList
import com.dpt.itnews.data.po.VersionResult
import io.reactivex.Observable
import retrofit2.http.Headers

/**
 * Created by dupengtao on 17/6/20.
 */

interface CnBlogNewsApis {

    @com.dpt.itnews.api.converter.QualifiedTypeConverterFactory.Xml
    @retrofit2.http.GET("recent/paged/{index}/{size}")
    fun recentList(@retrofit2.http.Path("index") index: Int, @retrofit2.http.Path("size") size: Int): Observable<NewsList>

    @com.dpt.itnews.api.converter.QualifiedTypeConverterFactory.Xml
    @retrofit2.http.GET("item/{id}")
    fun item(@retrofit2.http.Path("id") id: Int): Observable<NewsItem>

    @com.dpt.itnews.api.converter.QualifiedTypeConverterFactory.Json
    @Headers("X-LC-Id:kXPyFVwsVvyGNLP5Aunv6aPJ-gzGzoHsz", "X-LC-Key:BoFChIsNcoqidiok7kjW6aNM", "Content-Type:application/json")
    @retrofit2.http.GET
    fun checkVersion(@retrofit2.http.Url url: String): Observable<VersionResult>
}
