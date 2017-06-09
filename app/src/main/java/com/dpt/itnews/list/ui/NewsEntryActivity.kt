package com.dpt.itnews.list.ui

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.dpt.itnews.R
import com.dpt.itnews.data.vo.News
import com.dpt.itnews.list.ListContract
import com.dpt.itnews.list.presenter.ListPresenter
import com.dpt.itnews.list.ui.adapter.NewsEntryAdapter
import java.util.*

/**
 * Created by dupengtao on 17/6/9.
 */
class NewsEntryActivity : Activity(), ListContract.View {

    private lateinit var toolBar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var presenter: ListContract.Presenter
    private lateinit var newsEntryAdapter: NewsEntryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list2)

        initToolBar()
        initRecyclerView()
        initSwipeRefresh()

        ListPresenter(this)
    }

    private fun initSwipeRefresh() {

    }

    override fun onResume() {
        super.onResume()
        presenter.loadRecentList()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unSubscribe()
    }

    override fun setPresenter(p: ListContract.Presenter) {
        presenter = p
    }

    override fun showNews(news: News) {
        newsEntryAdapter.news = news
        newsEntryAdapter.notifyDataSetChanged()
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.rv_article) as RecyclerView
        val layoutManager = VirtualLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val delegateAdapter = DelegateAdapter(layoutManager,true)
        recyclerView.adapter = delegateAdapter
        val adapters = LinkedList<DelegateAdapter.Adapter<*>>()
        newsEntryAdapter = NewsEntryAdapter(this,LinearLayoutHelper(),{
            i ->  presenter.jumpArticle(i)
        })
        adapters.add(newsEntryAdapter)

        delegateAdapter.setAdapters(adapters)
    }

    private fun initToolBar() {
        toolBar = findViewById(R.id.tb_list) as Toolbar
        toolBar.title = getString(R.string.app_name)
        toolBar.setTitleTextColor(Color.WHITE)
        toolBar.inflateMenu(R.menu.main)
        toolBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_up -> {}
            }
            true
        }
    }

}