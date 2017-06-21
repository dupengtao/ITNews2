package com.dpt.itnews.settings

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.View
import android.widget.Switch
import android.widget.TextView
import com.dpt.itnews.R
import com.dpt.itnews.base.util.DayNight
import com.dpt.itnews.base.util.DayNightHelper
import com.dpt.itnews.base.util.showDayNightAnimation
import com.dpt.itnews.settings.upgrade.UpgradeHelper

/**
 * Created by dupengtao on 17/6/20.
 */
class SettingsActivity :Activity(){

    private lateinit var toolBar : Toolbar
    private lateinit var vUpdate : View
    private lateinit var dayNightHelper: DayNightHelper
    private lateinit var upgradeHelper : UpgradeHelper
    private lateinit var sDayNight: Switch
    private var preDayNight: Boolean =false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        dayNightHelper = DayNightHelper(this)
        initToolBar()
        initItem()
        initTheme()
    }

    private fun initTheme() {

        preDayNight = if (dayNightHelper.isDay()) {
            setTheme(R.style.AppTheme_Day)
            true
        } else {
            setTheme(R.style.AppTheme_Night)
            false
        }

        sDayNight.isChecked = !preDayNight
        refreshUI(preDayNight)
    }

    private fun initItem() {
        vUpdate = findViewById(R.id.rl_settings_update)
        upgradeHelper = UpgradeHelper()
        vUpdate.setOnClickListener {
            upgradeHelper.checkVersionInfo()
        }


        //DayNight
        sDayNight = findViewById(R.id.sDayNight) as Switch
        sDayNight.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked){
                dayNightHelper.setMode(DayNight.Night)
                setTheme(R.style.AppTheme_Night)
            }else{
                dayNightHelper.setMode(DayNight.DAY)
                setTheme(R.style.AppTheme_Day)
            }

            showDayNightAnimation()
            refreshUI(!isChecked)
        }

    }

    private fun refreshUI(isDay: Boolean) {
        refreshStatusBar(isDay)
        val listBackground = TypedValue()
        val listTitleTextColor = TypedValue()
        val toolbarBackground = TypedValue()
        val toolbarTitleColor = TypedValue()

        theme.resolveAttribute(R.attr.listBackground, listBackground, true)
        theme.resolveAttribute(R.attr.listTitleTextColor, listTitleTextColor, true)
        theme.resolveAttribute(R.attr.toolbarBackground, toolbarBackground, true)
        theme.resolveAttribute(R.attr.toolbarTitleColor, toolbarTitleColor, true)

        findViewById(R.id.fl_list).setBackgroundResource(listBackground.resourceId)
        findViewById(R.id.abl_list).setBackgroundResource(toolbarBackground.resourceId)
        toolBar.setTitleTextColor(resources.getColor(toolbarTitleColor.resourceId))
        (findViewById(R.id.tv_settings_update) as TextView).setTextColor(resources.getColor( listTitleTextColor.resourceId))
        (findViewById(R.id.tv_settings_dayNight) as TextView).setTextColor(resources.getColor( listTitleTextColor.resourceId))



        if(isDay){
            toolBar.navigationIcon = resources.getDrawable(R.drawable.ic_back)
//            findViewById(R.id.v_divide1).setBackgroundColor(resources.getColor(R.color.articleToolbarDivide))
        }else{
            toolBar.navigationIcon = resources.getDrawable(R.drawable.ic_back_night)
//            findViewById(R.id.v_divide1).setBackgroundColor(resources.getColor(R.color.articleToolbarDivide_Night))
        }

    }



    private fun initToolBar() {
        toolBar = findViewById(R.id.tb_list) as Toolbar
        toolBar.setNavigationOnClickListener {
            onBackPressed()
        }
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

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("IS_CHANGE",preDayNight != dayNightHelper.isDay())
        setResult(1,intent)
        super.onBackPressed()
    }
}