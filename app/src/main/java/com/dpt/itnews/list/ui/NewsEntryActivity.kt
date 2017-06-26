package com.dpt.itnews.list.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.dpt.itnews.R
import com.dpt.itnews.article.ui.ArticleView
import com.dpt.itnews.base.util.*
import com.dpt.itnews.base.widget.CustomBottomSheetBehavior
import com.dpt.itnews.data.vo.Article
import com.dpt.itnews.data.vo.News
import com.dpt.itnews.list.ListContract
import com.dpt.itnews.list.presenter.ListPresenter
import com.dpt.itnews.list.ui.adapter.NewsEntryAdapter
import com.dpt.itnews.settings.ui.SettingsActivity
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
    private lateinit var errorView: View
    private lateinit var articleBehavior: CustomBottomSheetBehavior<ArticleView>
    private lateinit var dayNightHelper: DayNightHelper
    private var hasError: Boolean = false
    private var preOffset = 0
    private var isLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list2)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
        initToolBar()
        initRecyclerView()
        initSwipeRefresh()
        initArticleView()
        initProgressBar()
        initOtherView()
        initErrorView()
        dayNightHelper = DayNightHelper(this)
        initTheme()

        ListPresenter(this)
    }


    private fun initTheme() {
        val isDay = if (dayNightHelper.isDay()) {
            setTheme(R.style.AppTheme_Day)
            true
        } else {
            setTheme(R.style.AppTheme_Night)
            false
        }

        refreshUI(isDay)
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
        articleView.setBottomSheetBehavior(articleBehavior) {
            ivLayer.alpha = it
        }
    }

    private fun initErrorView() {
        errorView = findViewById(R.id.rl_error)
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
        errorView.visibility = View.GONE
        newsEntryAdapter.news = news
        newsEntryAdapter.notifyDataSetChanged()
        hasError = false
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.rv_article) as RecyclerView
        val layoutManager = VirtualLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val delegateAdapter = DelegateAdapter(layoutManager, true)
        recyclerView.adapter = delegateAdapter
        val adapters = LinkedList<DelegateAdapter.Adapter<*>>()
        newsEntryAdapter = NewsEntryAdapter(this, LinearLayoutHelper()) {
            presenter.jumpArticle(it)
        }
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
                R.id.action_change_theme -> {
                    showDayNightAnimation()
                    val isDay = if (dayNightHelper.isDay()) {
                        item.isChecked = true
                        setTheme(R.style.AppTheme_Night)
                        dayNightHelper.setMode(DayNight.Night)
                        false
                    } else {
                        item.isChecked = false
                        setTheme(R.style.AppTheme_Day)
                        dayNightHelper.setMode(DayNight.DAY)
                        true
                    }
                    refreshUI(isDay)
                }
                R.id.action_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivityForResult(intent, 1)
                }
            }
            true
        }

    }

    private fun refreshUI(isDayMode: Boolean) {
        val listBackground = TypedValue()
        val listTitleTextColor = TypedValue()
        val listItemBackground = TypedValue()
        val listItemDivideColor = TypedValue()
        val listSummaryTextColor = TypedValue()
        val listDateTextColor = TypedValue()
        val toolbarBackground = TypedValue()
        val toolbarTitleColor = TypedValue()

        theme.resolveAttribute(R.attr.listBackground, listBackground, true)
        theme.resolveAttribute(R.attr.listTitleTextColor, listTitleTextColor, true)
        theme.resolveAttribute(R.attr.listItemBackground, listItemBackground, true)
        theme.resolveAttribute(R.attr.listItemDivideColor, listItemDivideColor, true)
        theme.resolveAttribute(R.attr.listSummaryTextColor, listSummaryTextColor, true)
        theme.resolveAttribute(R.attr.listDateTextColor, listDateTextColor, true)
        theme.resolveAttribute(R.attr.toolbarBackground, toolbarBackground, true)
        theme.resolveAttribute(R.attr.toolbarTitleColor, toolbarTitleColor, true)

        //toolbar
        appBarLayout.setBackgroundResource(toolbarBackground.resourceId)
        toolBar.setTitleTextColor(resources.getColor(toolbarTitleColor.resourceId))
        val changeMode = toolBar.menu.findItem(R.id.action_change_theme)
        val up = toolBar.menu.findItem(R.id.action_up)
        val settings = toolBar.menu.findItem(R.id.action_settings)
        refreshStatusBar(isDayMode)
        if (isDayMode) {
            toolBar.overflowIcon = resources.getDrawable(R.drawable.ic_toolbar_menu)
            up.icon = resources.getDrawable(R.drawable.ic_up_arrow_angle)
            settings.icon = resources.getDrawable(R.drawable.ic_settings)
            changeMode.isChecked = false
        } else {
            toolBar.overflowIcon = resources.getDrawable(R.drawable.ic_toolbar_menu_night)
            up.icon = resources.getDrawable(R.drawable.ic_up_arrow_angle_night)
            settings.icon = resources.getDrawable(R.drawable.ic_settings_night)
            changeMode.isChecked = true
        }
        //list background
        findViewById(R.id.fl_list).setBackgroundResource(listBackground.resourceId)

        //recyclerView
        val childCount = recyclerView.childCount
        for (index in 0..childCount) {
            val childView = recyclerView.getChildAt(index)
            if (childView != null) {
                childView.findViewById(R.id.ll_box_list).setBackgroundResource(listItemBackground.resourceId)
                (childView.findViewById(R.id.tv_title) as TextView).setTextColor(resources.getColor(listTitleTextColor.resourceId))
                (childView.findViewById(R.id.tv_summary) as TextView).setTextColor(resources.getColor(listSummaryTextColor.resourceId))
                (childView.findViewById(R.id.tv_date) as TextView).setTextColor(resources.getColor(listDateTextColor.resourceId))
                childView.findViewById(R.id.iv_divide).setBackgroundResource(listItemDivideColor.resourceId)
            }
        }

        RecyclerViewHelper.clearCache(recyclerView)

        //progressBar
        if (isDayMode) {
            processBar.progressDrawable.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY)
        } else {
            processBar.progressDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
        }

        //articleView
        articleView.refreshUI(isDayMode)
    }

    private fun refreshStatusBar(dayMode: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (dayMode) {
                window.statusBarColor = resources.getColor(R.color.statusBarColor)
            } else {
                window.statusBarColor = resources.getColor(R.color.statusBarColor_Night)
            }
        }
    }

    private fun initSwipeRefresh() {
        swipeRefreshLayout = findViewById(R.id.srl_list) as SwipeRefreshLayout
        swipeRefreshLayout.setProgressViewOffset(true, 150, 400)
        swipeRefreshLayout.setOnRefreshListener {
            if (hasError) {
                presenter.loadRecentList(true)
            } else {
                presenter.loadRecentList(false)
            }
        }
    }

    override fun showTopTips(msg: String, type: Int) {
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

    override fun showError() {
        hasError = true
        errorView.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        if (articleBehavior.state != CustomBottomSheetBehavior.STATE_COLLAPSED) {
            articleBehavior.state = CustomBottomSheetBehavior.STATE_COLLAPSED
            return
        }
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val isChange = data?.getBooleanExtra("IS_CHANGE", false)
        if (isChange is Boolean && isChange) {
            initTheme()
        }
        Log.e("dpt", "isChange = $isChange")
    }
}