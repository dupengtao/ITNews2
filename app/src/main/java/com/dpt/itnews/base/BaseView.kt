package com.dpt.itnews.base

/**
 * Created by dupengtao on 17/6/8.
 */
interface BaseView<T> {

    fun setPresenter(presenter: T)
}