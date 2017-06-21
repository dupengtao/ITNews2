package com.dpt.itnews.base.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v7.widget.Toolbar
import android.text.Html
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.dpt.itnews.data.po.NewsItem
import com.dpt.itnews.data.vo.Article
import com.dpt.itnews.data.vo.ArticleBody
import com.dpt.itnews.data.vo.ArticleConstant
import org.jsoup.Jsoup
import java.lang.StringBuilder


/**
 * Created by dupengtao on 17/6/9.
 */

fun View.scrollAnim(translationY: Float, isShow: Boolean = true, startAction: () -> Unit, endAction: () -> Unit) {


    this.animate()
            .translationY(translationY)
            .setInterpolator(if (isShow) {
                AccelerateInterpolator()
            } else DecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    endAction.invoke()
                }

                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationStart(animation)
                    startAction.invoke()
                }
            })
            .start()
}

fun String?.fromHtml() = if (this == null) this else Html.fromHtml(this).toString()

fun NewsItem.mapArticle(): Article {
    val article = Article()
    val bodyFragment = Jsoup.parseBodyFragment(this.content)
    val elements = bodyFragment.body().children().select("*")
    for (element in elements) {
        if (element.`is`("p")) {
            val text = element.text()
            when {
                element.children().`is`("strong") -> {
                    article.body.add(ArticleBody(ArticleConstant.TEXT_STRONG_TYPE, text = text))
                }
                element.children().`is`("img") -> {
                    val img = element.child(0)
                    if (img != null) {
                        val sb = StringBuilder(img.attr("src"))
                        val url = if(sb.startsWith("http")){
                            sb.toString()
                        }else{
                            sb.insert(0, "https:")
                            sb.toString()
                        }
                        sb.insert(0, "https:")
                        article.body.add(ArticleBody(ArticleConstant.IMG_TYPE, url = url))
                    }
                }
                else -> {
                    val sb = StringBuilder(text)
                    sb.insert(0, "        ")
                    article.body.add(ArticleBody(ArticleConstant.TEXT_TYPE, text = sb.toString()))
                }
            }
        } else if (element.`is`("li")) {
            val sb = StringBuffer(element.text())
            sb.insert(0, "• ")
            article.body.add(ArticleBody(ArticleConstant.TEXT_LIST_ITEM, text = sb.toString()))
        }
    }

    article.commentCount = this.commentCount
    article.content = this.content
    article.nextNews = this.nextNews
    article.prevNews = this.prevNews
    article.sourceName = this.sourceName
    article.submitDate = this.submitDate
    article.title = this.title

    return article
}


/**
 * 获取一个 View 的缓存视图

 * @param view
 * *
 * @return
 */
private fun getCacheBitmapFromView(view: View): Bitmap? {
    val drawingCacheEnabled = true
    view.isDrawingCacheEnabled = drawingCacheEnabled
    view.buildDrawingCache(drawingCacheEnabled)
    val drawingCache = view.drawingCache
    val bitmap: Bitmap?
    if (drawingCache != null) {
        bitmap = Bitmap.createBitmap(drawingCache)
        view.isDrawingCacheEnabled = false
    } else {
        bitmap = null
    }
    return bitmap
}

/**
 * 展示一个切换动画
 */
fun Activity.showDayNightAnimation() {
    val decorView = this.window.decorView
    val cacheBitmap = getCacheBitmapFromView(decorView)
    if (decorView is ViewGroup && cacheBitmap != null) {
        val view = View(this)
        view.setBackgroundDrawable(BitmapDrawable(this.resources, cacheBitmap))
        val layoutParam = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        decorView.addView(view, layoutParam)
        val objectAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
        objectAnimator.duration = 300
        objectAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                decorView.removeView(view)
            }
        })
        objectAnimator.start()
    }
}
