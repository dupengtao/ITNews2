package com.dpt.itnews.base.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.support.v7.widget.Toolbar
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator


/**
 * Created by dupengtao on 17/6/9.
 */

fun Toolbar.anim(translationY: Float,isShow: Boolean = true, startAction: () -> Unit, endAction: () -> Unit) {



    this.animate()
            .translationY(translationY)
            .setInterpolator(if(isShow) AccelerateInterpolator() else DecelerateInterpolator())
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