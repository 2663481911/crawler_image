package com.view.image.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.view.image.R


class GalleryAdapter : ListAdapter<String, GalleryAdapter.GalleryViewHolder>(DiffCallback) {
    private var listener: ClickListener? = null

    interface ClickListener {
        fun setOnClickListener(view: View, position: Int)
    }

    fun setOnClickListener(listener: ClickListener) {
        if (this.listener == null)
            this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.gallery_item, parent, false)

        return GalleryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val imageView = holder.itemView.findViewById<ImageView>(R.id.imageView)
        Glide.with(holder.itemView)
            .load(getItem(position))
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(imageView)

        imageView.setOnClickListener {
            listener?.setOnClickListener(it, position)
        }

    }

    object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }


    class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

