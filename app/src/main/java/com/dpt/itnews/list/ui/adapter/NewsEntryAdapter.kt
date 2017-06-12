package com.dpt.itnews.list.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.dpt.itnews.R
import com.dpt.itnews.data.vo.News

/**
 * Created by dupengtao on 17/6/9.
 */
class NewsEntryAdapter(private val context: Context, private val layoutHelper: LayoutHelper, val itemClick: (Int) -> Unit) : DelegateAdapter.Adapter<NewsEntryAdapter.ItemHolder>() {


    var news: News = News()

    override fun onCreateLayoutHelper() = layoutHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_entry_list, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        with(news.newsList[position]) {
            holder.tvTitle.text = title
            holder.tvSummary.text = summary
            holder.tvDate.text = "$id • $sourceName • $view 人阅读 • ${formatDate()}"
        }
    }

    override fun getItemCount() = news.newsList.size


    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle = itemView.findViewById(R.id.tv_title) as TextView
        val tvSummary = itemView.findViewById(R.id.tv_summary) as TextView
        val ivIcon = itemView.findViewById(R.id.iv) as ImageView
        val tvDate = itemView.findViewById(R.id.tv_date) as TextView

        init {
            itemView.setOnClickListener { itemClick.invoke(adapterPosition) }
        }



    }
}