package com.dpt.itnews.settings

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import com.dpt.itnews.R
import com.dpt.itnews.base.util.DayNightHelper
import com.dpt.itnews.settings.upgrade.UpgradeHelper

/**
 * Created by dupengtao on 17/6/20.
 */
class SettingsActivity :Activity(){

    private lateinit var toolBar : Toolbar
    private lateinit var vUpdate : View
    private lateinit var dayNightHelper: DayNightHelper
    private lateinit var upgradeHelper : UpgradeHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        dayNightHelper = DayNightHelper(this)
        initToolBar()
        initItem()
        refreshUI()
    }

    private fun initItem() {
        vUpdate = findViewById(R.id.rl_settings_update)
        upgradeHelper = UpgradeHelper()
        vUpdate.setOnClickListener {
            upgradeHelper.checkVersionInfo()
        }
    }

    private fun refreshUI() {
        refreshStatusBar(dayNightHelper.isDay())
    }



    private fun initToolBar() {
        toolBar = findViewById(R.id.tb_list) as Toolbar
    }

    private fun refreshStatusBar(dayMode: Boolean) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(dayMode){
                window.statusBarColor = resources.getColor(R.color.statusBarColor)
            }else{
                window.statusBarColor = resources.getColor(R.color.statusBarColor_Night)
            }
        }
    }
}