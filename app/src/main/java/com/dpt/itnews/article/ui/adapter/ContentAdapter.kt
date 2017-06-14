package com.dpt.itnews.article.ui.adapter

import android.content.Context
import android.media.Image
import android.support.v7.widget.DecorContentParent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dpt.itnews.R
import com.dpt.itnews.data.vo.Article
import com.dpt.itnews.data.vo.ArticleConstant

/**
 * Created by dupengtao on 17/6/14.
 */
class ContentAdapter(val context: Context, val layoutHelper: LayoutHelper) : DelegateAdapter.Adapter<ContentAdapter.ContentHolder>() {

    var article = Article()

    override fun onCreateLayoutHelper() = layoutHelper

    override fun onBindViewHolder(holder: ContentHolder, position: Int) {
        val viewType = getItemViewType(position)
        when(viewType){
            ArticleConstant.TEXT_TYPE -> bindStrTypeView(holder,position)
            ArticleConstant.IMG_TYPE -> bindImgTypeView(holder,position)
            ArticleConstant.TEXT_STRONG_TYPE -> bindStrTypeView(holder,position)
            ArticleConstant.TEXT_LIST_ITEM -> bindStrTypeView(holder,position)
        }
    }

    private fun bindImgTypeView(holder: ContentHolder, position: Int) {
        val url = article.body[position].url ?: return

        val iv = holder.itemView.findViewById(R.id.iv) as ImageView

        if(url.endsWith(".gif")){
            Glide.with(iv.context)
                    .load(url)
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontAnimate()
                    .into(iv)
        }else {
            Glide.with(iv.context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .dontAnimate()
                    .into(iv)
        }
    }

    private fun bindStrTypeView(holder: ContentHolder, position: Int) {
        val tv = holder.itemView.findViewById(R.id.tv) as TextView
        tv.text = article.body[position].text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentHolder {
        val resId = when (viewType) {
            ArticleConstant.TEXT_TYPE -> R.layout.item_content_text_article
            ArticleConstant.IMG_TYPE -> R.layout.item_content_img_article
            ArticleConstant.TEXT_STRONG_TYPE -> R.layout.item_content_text_strong_article
            ArticleConstant.TEXT_LIST_ITEM -> R.layout.item_content_text_li_article
            else -> R.layout.item_content_text_article
        }
        return ContentHolder(LayoutInflater.from(context).inflate(resId,parent,false))
    }

    override fun getItemCount() = article.body.size

    override fun getItemViewType(position: Int): Int {
        return article.body[position].type
    }

    inner class ContentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}