package com.dpt.itnews.api.cnBlog;

import com.dpt.itnews.data.po.NewsItem;
import com.dpt.itnews.data.po.NewsList;
import com.dpt.itnews.data.po.VersionUpgradeInfo;
import io.reactivex.Observable;
import retrofit2.http.Headers;

/**
 * Created by dupengtao on 17/6/20.
 */

public interface CnBlogNewsApis2 {

    @com.dpt.itnews.api.converter.QualifiedTypeConverterFactory.Xml
    @retrofit2.http.GET("recent/paged/{index}/{size}")
    Observable<NewsList> recentList(@retrofit2.http.Path("index")int index, @retrofit2.http.Path("size")int size);

    @com.dpt.itnews.api.converter.QualifiedTypeConverterFactory.Xml
    @retrofit2.http.GET("item/{id}")
    Observable<NewsItem> item(@retrofit2.http.Path("id") int id);

    @com.dpt.itnews.api.converter.QualifiedTypeConverterFactory.Json
    @Headers({"X-LC-Id:kXPyFVwsVvyGNLP5Aunv6aPJ-gzGzoHsz","X-LC-Key:BoFChIsNcoqidiok7kjW6aNM","Content-Type:application/json"})
    @retrofit2.http.GET
    Observable<VersionUpgradeInfo> checkVersion(@retrofit2.http.Url String url);
}
