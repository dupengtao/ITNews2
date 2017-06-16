package com.dpt.itnews.article.ui

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dpt.itnews.R
import com.github.chrisbanes.photoview.PhotoView

/**
 * Created by dupengtao on 17/6/16.
 */
class PhotoActivity :Activity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        val photoView = findViewById(R.id.pv) as PhotoView
        val imgUrl = intent.getStringExtra("IMG_URL")
        Glide.with(this)
                .load(imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(photoView)
    }
}