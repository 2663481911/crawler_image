package com.view.image.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.view.image.adapter.GalleryAdapter
import com.view.image.analyzeRule.AnalyzeRule
import com.view.image.analyzeRule.RuleUtil
import com.view.image.databinding.ActivityGalleryBinding
import com.view.image.model.*

class GalleryActivity : AppCompatActivity() {
    lateinit var binding: ActivityGalleryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent?.getSerializableExtra("data") as HomeData
        val rule = intent?.getSerializableExtra("rule") as Rule

        val galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
        galleryViewModel.ruleLive.observe(this, {
            galleryViewModel.setRuleUtil(RuleUtil(it, AnalyzeRule()))
        })

        galleryViewModel.setRule(rule)

        val galleryAdapter = GalleryAdapter()
        galleryViewModel.imgUrlListLive.observe(this, {
            binding.recyclerView.apply {
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                adapter = galleryAdapter
                galleryAdapter.submitList(it)
            }
        })

        galleryViewModel.hrefLive.observe(this, {
            galleryViewModel.getImgList(it)
        })

        galleryAdapter.setOnClickListener(object : GalleryAdapter.ClickListener {
            override fun setOnClickListener(view: View, position: Int) {
                Intent(this@GalleryActivity, PhotoActivity::class.java).apply {
                    putStringArrayListExtra("urlList", galleryViewModel.imgUrlListLive.value)
                    putExtra("pos", position)
                    startActivity(this)
                }
            }

        })

        galleryViewModel.setHref(data.href)
//
    }

}