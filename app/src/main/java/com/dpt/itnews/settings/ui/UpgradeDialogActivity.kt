package com.dpt.itnews.settings.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.dpt.itnews.BuildConfig
import com.dpt.itnews.R

/**
 * Created by dupengtao on 17/6/22.
 */
class UpgradeDialogActivity :Activity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upgrade)
        val v = intent.getIntExtra("VERSION",BuildConfig.VERSION_CODE+1)
        val d= intent.getStringExtra("DESCRIPTION")

        (findViewById(R.id.tv_version) as TextView).text = "最新版本: $v"
        (findViewById(R.id.tv_description) as TextView).text = d

        findViewById(R.id.btn_cancel_upgrade).setOnClickListener {
            val result = Intent()
            result.putExtra("UPGRADE_RESULT",false)
            setResult(2,result)
            finish()
        }

        findViewById(R.id.btn_upgrade).setOnClickListener {
            val result = Intent()
            result.putExtra("UPGRADE_RESULT",true)
            result.putExtra("URL",intent.getStringExtra("URL"))
            setResult(2,result)
            finish()
        }

    }
}