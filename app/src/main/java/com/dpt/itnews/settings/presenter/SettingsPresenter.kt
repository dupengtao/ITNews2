package com.dpt.itnews.settings.presenter

import com.dpt.itnews.BuildConfig
import com.dpt.itnews.data.source.NewsRepository
import com.dpt.itnews.settings.SettingsContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by dupengtao on 17/6/22.
 */
class SettingsPresenter (val view: SettingsContract.View,val newsRepository: NewsRepository = NewsRepository.get()): SettingsContract.Presenter{

    init {
        view.setPresenter(this)
    }

    private val disposable = CompositeDisposable()

    override fun subscribe() {

    }

    override fun unSubscribe() {
        disposable.clear()
    }

    override fun checkVersion() {
        NewsRepository.get().checkNewVersion()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val vCode = it.results[0].apkVersion
                    if (vCode != null) {
                        if (vCode > BuildConfig.VERSION_CODE) {
                            view.showUpdate(it.results[0])
                            return@subscribe
                        }
                    }
                    view.showMsg("您的应用为最新版本")
                }, {
                    it.printStackTrace()
                    view.showMsg("您的应用为最新版本")
                })
    }
}