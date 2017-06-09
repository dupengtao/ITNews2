package com.dpt.itnews.api.cnBlog

import com.dpt.itnews.api.converter.QualifiedTypeConverterFactory
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

/**
 * Created by dupengtao on 17/6/7.
 */
class CnBlogNewsClient {

    lateinit var cnBlogNewsApis: CnBlogNewsApis

    init {
        initCnBlogNewsApi()
    }

    companion object {

        private val BASE_URL = "http://wcf.open.cnblogs.com/news/"
    }


    private fun initCnBlogNewsApi() {
        val okHttpClientBuilder = OkHttpClient.Builder()
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        okHttpClientBuilder.addInterceptor(httpLoggingInterceptor)

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(QualifiedTypeConverterFactory(GsonConverterFactory.create(), SimpleXmlConverterFactory.create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(okHttpClientBuilder.build())
                .build()

        cnBlogNewsApis = retrofit.create(CnBlogNewsApis::class.java)

    }
}