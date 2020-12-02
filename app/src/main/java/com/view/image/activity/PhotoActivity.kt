package com.view.image.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.size
import androidx.viewpager2.widget.ViewPager2
import com.view.image.R
import com.view.image.adapter.PagerPhotoAdapter
import com.view.image.databinding.ActivityPhotoBinding
import com.view.image.fileUtil.ImageFile.makeImg
import com.view.image.fileUtil.ImageFile.saveImg
import com.view.image.fileUtil.ImageFile.shareImg
import java.util.*
import kotlin.concurrent.thread

class PhotoActivity : AppCompatActivity() {
    lateinit var binding: ActivityPhotoBinding
    var indexPosition: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val urlList: ArrayList<String>? = intent.getStringArrayListExtra("urlList")
        val name = intent.getStringExtra("name").toString()
        urlList?.let {
            binding.viewPage.adapter = PagerPhotoAdapter(it)
        }

        binding.bigMenu.setOnClickListener {
            // View当前PopupMenu显示的相对View的位置
            val popupMenu = PopupMenu(this, it)
            // menu布局
            popupMenu.menuInflater.inflate(R.menu.photo_img_menu, popupMenu.menu)
            // menu的item点击事件
            popupMenu.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener,
                PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {
                    when (item.itemId) {
                        R.id.save_img -> {
                            thread {
                                val path =
                                    saveImg(this@PhotoActivity, urlList!![indexPosition], name)
                                runOnUiThread {
                                    Toast.makeText(this@PhotoActivity, path, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                        R.id.shave_img -> {
                            thread {
                                val path =
                                    saveImg(this@PhotoActivity, urlList!![indexPosition], name)
                                shareImg(this@PhotoActivity, path, name)
                            }
                        }
                        R.id.make_img -> {
                            makeImg(this@PhotoActivity, urlList!![indexPosition])
                        }
                        R.id.back -> finish()
                    }
                    return false
                }
            })
            popupMenu.show()
        }


        // 添加小圆点
        for (i in 0 until urlList!!.size) {
            val imageView = ImageView(this)
            imageView.layoutParams = ViewGroup.LayoutParams(20, 20)
            imageView.setPadding(20, 0, 20, 0)
            imageView.setBackgroundResource(R.drawable.indicator)
            binding.indicatorView.addView(imageView)
        }

        // 改变小圆点
        binding.viewPage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                indexPosition = position
                for (i in 0 until binding.indicatorView.size) {
                    binding.indicatorView[i].setBackgroundResource(R.drawable.indicator)
                }
                binding.indicatorView[position].setBackgroundResource(R.drawable.indicator_cur)
            }
        })

        // 点击图片的位置
        binding.viewPage.setCurrentItem(intent.getIntExtra("pos", 0), false)
    }


}