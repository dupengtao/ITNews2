package com.dpt.itnews.list.ui

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.dpt.itnews.R
import com.dpt.itnews.article.ui.ArticleView
import com.dpt.itnews.base.util.scrollAnim
import com.dpt.itnews.base.widget.CustomBottomSheetBehavior
import com.dpt.itnews.data.vo.Article
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
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var presenter: ListContract.Presenter
    private lateinit var newsEntryAdapter: NewsEntryAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var articleView: ArticleView
    private lateinit var processBar: ProgressBar
    private lateinit var ivLayer: ImageView
    private lateinit var articleBehavior: CustomBottomSheetBehavior<ArticleView>

    private var preOffset = 0
    private var isLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list2)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)

        initToolBar()
        initRecyclerView()
        initSwipeRefresh()
        initArticleView()
        initProgressBar()
        initOtherView()

        ListPresenter(this)
    }

    private fun initOtherView() {
       ivLayer = findViewById(R.id.iv_layer) as ImageView
    }

    private fun initProgressBar() {
        processBar = findViewById(R.id.pb_list) as ProgressBar
    }

    private fun initArticleView() {
        articleView = findViewById(R.id.rl_article_root) as ArticleView
        articleBehavior = CustomBottomSheetBehavior.from(articleView)
        articleView.setBottomSheetBehavior(articleBehavior){
            ivLayer.alpha = it
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isLoaded) {
            presenter.loadRecentList()
            isLoaded = true
        }
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
            presenter.jumpArticle(it)
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
            appBarLayout.scrollAnim(-toolBar.bottom.toFloat(), true, { toolBarHideRunning = true }, { toolBarHideRunning = false })

        } else {
            if (toolBarShowRunning) {
                return
            }
            appBarLayout.scrollAnim(0f, false, { toolBarShowRunning = true }, { toolBarShowRunning = false })
        }

        preOffset = offsetToStart
    }

    private fun onScroll(firstVisibleItemPosition: Int, lastVisibleItemPosition: Int, itemCount: Int) {
        presenter.loadNextPage(firstVisibleItemPosition, lastVisibleItemPosition, itemCount)
    }

    private fun initToolBar() {
        appBarLayout = findViewById(R.id.abl_list) as AppBarLayout
        toolBar = findViewById(R.id.tb_list) as Toolbar
        toolBar.title = getString(R.string.app_name)
        toolBar.inflateMenu(R.menu.main)
        toolBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_up -> {
                    recyclerView.smoothScrollToPosition(0)
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

    override fun openArticle(newId: Int, article: Article?) {
        articleView.loadNews(newId, article)
        articleBehavior.state = CustomBottomSheetBehavior.STATE_EXPANDED
    }

    override fun refreshProcess(times: Int) {
        processBar.progress = times
        if (times == 0) {
            processBar.visibility = View.GONE
            processBar.progress = 0
        } else {
            processBar.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if (articleBehavior.state != CustomBottomSheetBehavior.STATE_COLLAPSED) {
            articleBehavior.state = CustomBottomSheetBehavior.STATE_COLLAPSED
            return
        }
        super.onBackPressed()
    }
}