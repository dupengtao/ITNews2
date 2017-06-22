package com.dpt.itnews.settings

import com.dpt.itnews.base.BasePresenter
import com.dpt.itnews.base.BaseView
import com.dpt.itnews.data.po.UpgradeInfo

/**
 * Created by dupengtao on 17/6/22.
 */
interface SettingsContract {

    interface View : BaseView<Presenter> {
        fun showUpdate(updateInfo : UpgradeInfo)
        fun showMsg(msg: String)
    }

    interface Presenter : BasePresenter {

        fun checkVersion()
    }
}