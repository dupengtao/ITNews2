package com.dpt.itnews.settings.upgrade

import android.util.Log
import com.dpt.itnews.BuildConfig
import com.dpt.itnews.data.po.UpgradeInfo
import com.dpt.itnews.data.source.NewsRepository
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by dupengtao on 17/6/20.
 */
class UpgradeHelper {
    companion object {
        val curVersion = BuildConfig.VERSION_CODE
    }

    fun checkVersionInfo(hasUpdateAction: (UpgradeInfo) -> Unit, noUpdateAction: () -> Unit) {
        NewsRepository.get().checkNewVersion()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val vCode = it.results[0].apkVersion
                    if (vCode != null) {
                        if (vCode > BuildConfig.VERSION_CODE) {
                            hasUpdateAction.invoke(it.results[0])
                            return@subscribe
                        }
                    }
                    noUpdateAction.invoke()
                }, {
                    it.printStackTrace()
                    noUpdateAction.invoke()
                })
    }
}