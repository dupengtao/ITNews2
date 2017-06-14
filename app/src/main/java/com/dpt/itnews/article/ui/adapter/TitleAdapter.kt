package com.dpt.itnews.article.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.dpt.itnews.R
import com.dpt.itnews.base.util.fromHtml
import com.dpt.itnews.data.vo.Article

/**
 * Created by dupengtao on 17/6/13.
 */
class TitleAdapter(val context:Context,val layoutHelper: LayoutHelper) : DelegateAdapter.Adapter<TitleAdapter.TitleHolder>() {

    var article = Article()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleHolder {

        return TitleHolder(LayoutInflater.from(context).inflate(R.layout.item_title_article,parent,false))
    }

    override fun getItemCount() = if(article.title.isNullOrEmpty()) 0 else 1

    override fun onCreateLayoutHelper() = layoutHelper

    override fun onBindViewHolder(holder: TitleHolder, pos: Int) {
        holder.tvTitle.text = article.title.fromHtml()
    }

    inner class TitleHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        val tvTitle = itemView.findViewById(R.id.tv) as TextView
    }
}



