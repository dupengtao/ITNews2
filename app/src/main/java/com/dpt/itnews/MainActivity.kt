package com.dpt.itnews

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class MainActivity : AppCompatActivity() {

//    lateinit var listPresenter: ListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun run(v: View) {
        println("click event")
//        listPresenter.loadRecentList()

//        ArticlePresenter().loadArticle(571504)
    }
}
