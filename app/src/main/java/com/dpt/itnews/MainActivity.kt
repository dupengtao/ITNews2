package com.dpt.itnews

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dpt.itnews.api.Retrofits
import com.dpt.itnews.list.presenter.ListPresenter

class MainActivity : AppCompatActivity() {

//    lateinit var listPresenter: ListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun run(v: View) {
        println("click event")
//        listPresenter.loadRecentList()
    }
}
