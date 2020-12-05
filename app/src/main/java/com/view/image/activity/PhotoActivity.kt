package com.view.image.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.get
import androidx.core.view.size
import androidx.viewpager2.widget.ViewPager2
import com.view.image.R
import com.view.image.adapter.PagerPhotoAdapter
import com.view.image.databinding.ActivityPhotoBinding
import com.view.image.fileUtil.ImageFile.makeImg
import com.view.image.fileUtil.ImageFile.saveImg
import com.view.image.fileUtil.ImageFile.shareImg
import com.view.image.model.SAVE_ALL_IMG
import com.view.image.model.SAVE_FILE
import java.util.*
import kotlin.concurrent.thread

class PhotoActivity : AppCompatActivity() {
    lateinit var binding: ActivityPhotoBinding
    var indexPosition: Int = 0
    lateinit var urlList: List<String>
    lateinit var name: String

    companion object {
        fun actionStart(context: Context, urlList: ArrayList<String>, pos: Int, name: String) {
            Intent(context, PhotoActivity::class.java).apply {
                putStringArrayListExtra("urlList", urlList)
                putExtra("pos", pos)
                putExtra("name", name)
                context.startActivity(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        urlList = intent.getStringArrayListExtra("urlList")!!
        name = intent.getStringExtra("name").toString()
        urlList.let {
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
                            ActivityCompat.requestPermissions(this@PhotoActivity,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), SAVE_FILE)
                        }
                        R.id.shave_img -> {
                            thread {
                                val path = saveImg(this@PhotoActivity, urlList[indexPosition], name)
                                shareImg(this@PhotoActivity, path, name)
                            }
                        }
                        R.id.make_img -> {
                            makeImg(this@PhotoActivity, urlList[indexPosition])
                        }

                        R.id.save_all_img -> {
                            ActivityCompat.requestPermissions(this@PhotoActivity,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), SAVE_ALL_IMG)
                        }

                        R.id.back -> finish()
                    }
                    return false
                }
            })
            popupMenu.show()
        }


        // 添加小圆点
        for (i in urlList.indices) {
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            SAVE_FILE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    thread {
                        val path = saveImg(this@PhotoActivity, urlList[indexPosition], name)
                        runOnUiThread {
                            Toast.makeText(this@PhotoActivity, path, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "拒绝权限", Toast.LENGTH_SHORT).show()
                }
            }

            SAVE_ALL_IMG -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    thread {
                        for (url in urlList) {
                            saveImg(this@PhotoActivity, url, name)
                        }
                    }
                } else {
                    Toast.makeText(this, "拒绝权限", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}