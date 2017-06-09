package com.dpt.itnews.list.ui

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.dpt.itnews.R
import com.dpt.itnews.base.util.anim
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
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var preOffset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list2)

        initToolBar()
        initRecyclerView()
        initSwipeRefresh()

        ListPresenter(this)
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
        val delegateAdapter = DelegateAdapter(layoutManager, true)
        recyclerView.adapter = delegateAdapter
        val adapters = LinkedList<DelegateAdapter.Adapter<*>>()
        newsEntryAdapter = NewsEntryAdapter(this, LinearLayoutHelper(), {
            i ->
            presenter.jumpArticle(i)
        })
        adapters.add(newsEntryAdapter)

        delegateAdapter.setAdapters(adapters)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val itemCount = layoutManager.itemCount
                onScroll(firstVisibleItemPosition, lastVisibleItemPosition, itemCount)
                onScrollOffset(layoutManager.offsetToStart)
            }

        })
    }

    private var toolBarHideRunning = false
    private var toolBarShowRunning = false

    private fun onScrollOffset(offsetToStart: Int) {

        if (preOffset < offsetToStart) {
            if (toolBarHideRunning) {
                return
            }

            toolBar.anim(-toolBar.bottom.toFloat(), true, { toolBarHideRunning = true }, { toolBarHideRunning = false })

        } else {
            if (toolBarShowRunning) {
                return
            }
            toolBar.anim(0f, false, { toolBarShowRunning = true }, { toolBarShowRunning = false })
        }

        preOffset = offsetToStart
    }

    private fun onScroll(firstVisibleItemPosition: Int, lastVisibleItemPosition: Int, itemCount: Int) {
        presenter.loadNextPage(firstVisibleItemPosition, lastVisibleItemPosition, itemCount)
    }

    private fun initToolBar() {
        toolBar = findViewById(R.id.tb_list) as Toolbar
        toolBar.title = getString(R.string.app_name)
        toolBar.setTitleTextColor(Color.WHITE)
        toolBar.inflateMenu(R.menu.main)
        toolBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_up -> {
                }
            }
            true
        }
    }

    private fun initSwipeRefresh() {
        swipeRefreshLayout = findViewById(R.id.srl_list) as SwipeRefreshLayout
        swipeRefreshLayout.setProgressViewOffset(true, 150, 400)
        swipeRefreshLayout.setOnRefreshListener {
            presenter.loadRecentList(false)
        }
    }

    override fun showTopTips(msg: String) {
        Toast.makeText(this, msg, LENGTH_SHORT).show()
    }

    override fun showRefreshing(isShow: Boolean) {
        swipeRefreshLayout.isRefreshing = isShow
    }
}