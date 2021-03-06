package com.dpt.itnews.article.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.dpt.itnews.R
import com.dpt.itnews.article.ArticleContract
import com.dpt.itnews.article.presenter.ArticlePresenter
import com.dpt.itnews.article.ui.adapter.ContentAdapter
import com.dpt.itnews.article.ui.adapter.TitleAdapter
import com.dpt.itnews.base.util.RecyclerViewHelper
import com.dpt.itnews.base.widget.CustomBottomSheetBehavior
import com.dpt.itnews.data.vo.Article
import java.util.*

/**
 * Created by dupengtao on 17/6/13.
 */
class ArticleView(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs), ArticleContract.View {

    private var titleAdapter: TitleAdapter
    private var contentAdapter: ContentAdapter
    private lateinit var presenter: ArticleContract.Presenter
    private var behavior: CustomBottomSheetBehavior<ArticleView>? = null
    private var toolbar: Toolbar
    private var appBarLayout: AppBarLayout

    private lateinit var recyclerView: RecyclerView

    init {
        View.inflate(context, R.layout.view_article_content, this)
        var statusBarHeight = 0
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resId)
        }
        translationY = statusBarHeight.toFloat()

        appBarLayout = findViewById(R.id.abl_article) as AppBarLayout
        toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.setNavigationOnClickListener { behavior?.state = CustomBottomSheetBehavior.STATE_COLLAPSED }
        toolbar.inflateMenu(R.menu.article_menu)
        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.action_article_up -> {
                    recyclerView.smoothScrollToPosition(0)
                }
                R.id.action_article_share ->{
                    presenter.shareArticle(getContext())
                }
            }
            true
        }

        recyclerView = findViewById(R.id.rv_article) as RecyclerView
        val layoutManager = VirtualLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        val viewPool = RecyclerView.RecycledViewPool()
        viewPool.setMaxRecycledViews(0, 5)
        recyclerView.recycledViewPool = viewPool
        //
        val delegateAdapter = DelegateAdapter(layoutManager, true)
        recyclerView.adapter = delegateAdapter
        val adapters = LinkedList<DelegateAdapter.Adapter<*>>()
        //title
        val titleLayoutHelper = LinearLayoutHelper()
        titleAdapter = TitleAdapter(context, titleLayoutHelper)
        adapters.add(titleAdapter)

        //content
        var articleContentLayoutHelper = LinearLayoutHelper()
        articleContentLayoutHelper.setDividerHeight(15)
        contentAdapter = ContentAdapter(context, articleContentLayoutHelper) {
            presenter.itemClick(it)
        }
        adapters.add(contentAdapter)
        delegateAdapter.setAdapters(adapters)

        ArticlePresenter(this)
    }

    override fun setPresenter(p: ArticleContract.Presenter) {
        presenter = p
        presenter.subscribe()
    }

    fun loadNews(id: Int, article: Article?) {
        if (article != null) {
//            Log.e("dpt", article.toString())
            presenter.loadArticle(id,article)
        } else {
            presenter.loadArticle(newsId = id)
        }
    }

    override fun show(article: Article) {
        titleAdapter.article = article
        contentAdapter.article = article
        titleAdapter.notifyDataSetChanged()
        contentAdapter.notifyDataSetChanged()
    }

    override fun showPhoto(url: String) {
        val intent = Intent(context, PhotoActivity::class.java)
        intent.putExtra("IMG_URL", url)
        context.startActivity(intent)
    }

    private lateinit var parentView: ViewGroup

    fun setBottomSheetBehavior(bottomSheetBehavior: CustomBottomSheetBehavior<ArticleView>, onSide: (Float) -> Unit) {

        parentView = parent as ViewGroup
        bottomSheetBehavior.setBottomSheetCallback(object : CustomBottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.e("Bottom Sheet Behaviour", "STATE_COLLAPSED")
                        show(Article())
                        presenter.unSubscribe()
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> Log.e("Bottom Sheet Behaviour", "STATE_DRAGGING")
                    BottomSheetBehavior.STATE_EXPANDED -> Log.e("Bottom Sheet Behaviour", "STATE_EXPANDED")
                    BottomSheetBehavior.STATE_HIDDEN -> Log.e("Bottom Sheet Behaviour", "STATE_HIDDEN")
                    BottomSheetBehavior.STATE_SETTLING -> Log.e("Bottom Sheet Behaviour", "STATE_SETTLING")
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                Log.e("dpt", "slideOffset = $slideOffset")
                onSide.invoke(slideOffset)
            }
        })
        behavior = bottomSheetBehavior
    }

    fun refreshUI(isDay: Boolean) {
        val toolbarArticleBackground = TypedValue()
        val articleTitleColor = TypedValue()
        val articleBackground = TypedValue()
        context.theme.resolveAttribute(R.attr.toolbarArticleBackground, toolbarArticleBackground, true)
        context.theme.resolveAttribute(R.attr.articleTitleColor, articleTitleColor, true)
        context.theme.resolveAttribute(R.attr.articleBackground, articleBackground, true)
        appBarLayout.setBackgroundResource(toolbarArticleBackground.resourceId)
        this.setBackgroundResource(articleBackground.resourceId)

        if (isDay) {
            toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_article_cancel)
            toolbar.menu.findItem(R.id.action_article_share).icon = resources.getDrawable(R.drawable.ic_share)
            toolbar.menu.findItem(R.id.action_article_up).icon = resources.getDrawable(R.drawable.ic_up_arrow_angle)
            findViewById(R.id.v_toolbar_divide).setBackgroundColor(resources.getColor(R.color.articleToolbarDivide))
        } else {
            toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_article_cancel_night)
            toolbar.menu.findItem(R.id.action_article_share).icon = resources.getDrawable(R.drawable.ic_share_night)
            toolbar.menu.findItem(R.id.action_article_up).icon = resources.getDrawable(R.drawable.ic_up_arrow_angle_night)
            findViewById(R.id.v_toolbar_divide).setBackgroundColor(resources.getColor(R.color.articleToolbarDivide_Night))
        }


        RecyclerViewHelper.clearCache(recyclerView)
    }
}