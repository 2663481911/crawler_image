package com.view.image.photo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.view.image.R
import com.view.image.fileUtil.ImageFile

class PhotoAdapter(private val urlList: List<String>) :
    RecyclerView.Adapter<PagePhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagePhotoViewHolder {
        // 添加控件
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo_view, parent, false)
        return PagePhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PagePhotoViewHolder, position: Int) {
        ImageFile.showImg(holder.itemView, holder.imageView, urlList[position])
//        Glide.with(holder.itemView)
//            .load(urlList[position])
//            .into(holder.itemView.findViewById(R.id.photo_view))
    }

    override fun getItemCount(): Int {
        return urlList.size
    }
}

class PagePhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView = itemView.findViewById(R.id.photo_view)
}