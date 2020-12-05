package com.view.image.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.view.image.R
import com.view.image.model.DATA_STATUS_LOAD_NORMAL
import com.view.image.model.DATA_STATUS_NETWORK_ERROR
import com.view.image.model.DATA_STATUS_NOR_MORE
import com.view.image.model.HomeData


class HomeDataShowAdapter :
    ListAdapter<HomeData, HomeDataShowAdapter.HomeViewHolder>(DiffCallback) {
    companion object {
        const val NORMAL_VIEW_TYPE = 0
        const val FOOTER_VIEW_TYPE = 1
    }

    var listener: ClickListener? = null
    var footerViewStatus = DATA_STATUS_LOAD_NORMAL     // 用于底部状态改变

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val itemView = if (viewType == FOOTER_VIEW_TYPE)
            LayoutInflater.from(parent.context).inflate(R.layout.refresh_bar, parent, false)
                .also { it ->
                    // 用于设置多行的时候居中
//                    it.layoutParams.width = parent.width
                    when (it.layoutParams) {
                        is StaggeredGridLayoutManager.LayoutParams -> {
                            (it.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan =
                                true
                        }
                        is GridLayoutManager.LayoutParams -> {
                            it.layoutParams.width = parent.width
                        }
                    }
                }
        else
            LayoutInflater.from(parent.context).inflate(R.layout.item_home_data, parent, false)

        return HomeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        if (position == itemCount - 1) {
            with(holder.itemView) {
                when (footerViewStatus) {
                    DATA_STATUS_LOAD_NORMAL -> {
                        holder.itemView.findViewById<ProgressBar>(R.id.footer_bar).visibility =
                            View.VISIBLE
                        holder.itemView.findViewById<TextView>(R.id.footer_textView).text =
                            resources.getString(R.string.normal_load)

                    }
                    DATA_STATUS_NETWORK_ERROR -> {
                        holder.itemView.findViewById<ProgressBar>(R.id.footer_bar).visibility =
                            View.GONE
                        holder.itemView.findViewById<TextView>(R.id.footer_textView).text =
                            resources.getString(R.string.network_error)
                    }
                    DATA_STATUS_NOR_MORE -> {
                        holder.itemView.findViewById<ProgressBar>(R.id.footer_bar).visibility =
                            View.GONE
                        holder.itemView.findViewById<TextView>(R.id.footer_textView).text =
                            resources.getString(R.string.nor_more)
                    }
                }
            }
            return
        } else {
            val imageView: ImageView = holder.itemView.findViewById(R.id.image_view)
            Log.d("imgSrc", getItem(position).imgSrc)
            Glide.with(holder.itemView)
                .load(getItem(position).imgSrc)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(imageView)


            holder.itemView.findViewById<ImageView>(R.id.image_view).setOnClickListener {
                listener?.setOnClickListener(it, getItem(position))
            }
        }

    }

    object DiffCallback : DiffUtil.ItemCallback<HomeData>() {
        override fun areItemsTheSame(oldItem: HomeData, newItem: HomeData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: HomeData, newItem: HomeData): Boolean {
            return oldItem.imgSrc == newItem.imgSrc
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) {
            FOOTER_VIEW_TYPE
        } else
            NORMAL_VIEW_TYPE

    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    interface ClickListener {
        fun setOnClickListener(view: View, data: HomeData)
    }

    fun setOnClickListener(listener: ClickListener) {
        if (this.listener == null)
            this.listener = listener
    }

    class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}




