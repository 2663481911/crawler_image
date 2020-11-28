package com.view.image.activity

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.size
import androidx.viewpager2.widget.ViewPager2
import com.view.image.R
import com.view.image.adapter.PagerPhotoAdapter
import com.view.image.databinding.ActivityPhotoBinding
import java.util.*

class PhotoActivity : AppCompatActivity() {
    lateinit var binding: ActivityPhotoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val urlList: ArrayList<String>? = intent.getStringArrayListExtra("urlList")
        urlList?.let {
            binding.viewPage.adapter = PagerPhotoAdapter(it)
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
                for (i in 0 until binding.indicatorView.size) {
                    binding.indicatorView[i].setBackgroundResource(R.drawable.indicator)
                }
                binding.indicatorView[position].setBackgroundResource(R.drawable.indicator_cur)
            }
        })

        binding.viewPage.setCurrentItem(intent.getIntExtra("pos", 0), false)
    }
}