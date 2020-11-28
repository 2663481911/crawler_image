package com.view.image.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.view.image.R


class PagerPhotoAdapter(private val urlList: List<String>) :
    RecyclerView.Adapter<PagePhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagePhotoViewHolder {
        // 添加控件
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.photo_view_item, parent, false)
        return PagePhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PagePhotoViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(urlList[position])
            .into(holder.itemView.findViewById(R.id.photo_view))
    }

    override fun getItemCount(): Int {
        return urlList.size
    }
}

class PagePhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)