package com.dpt.itnews.settings.upgrade

import android.util.Log
import com.dpt.itnews.BuildConfig
import com.dpt.itnews.data.source.NewsRepository
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by dupengtao on 17/6/20.
 */
class UpgradeHelper {
    companion object{
        val curVersion = BuildConfig.VERSION_CODE
    }

    fun checkVersionInfo(){
        NewsRepository.get().checkNewVersion()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it ->
                    print(it)
                },{
                    it.printStackTrace()
                })
    }
}