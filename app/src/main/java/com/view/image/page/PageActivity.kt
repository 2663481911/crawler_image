package com.view.image.page

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.view.image.analyzeRule.AnalyzeRule
import com.view.image.analyzeRule.Rule
import com.view.image.analyzeRule.RuleUtil
import com.view.image.databinding.ActivityPageBinding
import com.view.image.home.HomeData
import com.view.image.model.*
import com.view.image.photo.PhotoActivity

class PageActivity : AppCompatActivity() {
    lateinit var binding: ActivityPageBinding

    companion object {
        fun actionStart(context: Context, data: HomeData, rule: Rule) {
            Intent(context, PageActivity::class.java).apply {
                putExtra("rule", rule)
                putExtra("data", data)
                context.startActivity(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent?.getSerializableExtra("data") as HomeData
        val rule = intent?.getSerializableExtra("rule") as Rule

        title = data.imgTitle

        val galleryViewModel = ViewModelProvider(this).get(PageViewModel::class.java)

        galleryViewModel.ruleLive.observe(this, {
            galleryViewModel.setRuleUtil(RuleUtil(it, AnalyzeRule()))
        })

        galleryViewModel.setRule(rule)
        val galleryAdapter = PageAdapter()

        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = galleryAdapter
        }

        galleryViewModel.imgUrlListLive.observe(this, {
            galleryAdapter.submitList(it)
        })

        galleryViewModel.hrefLive.observe(this, {
            galleryAdapter.setReferer(it)
            galleryViewModel.getImgList(it)
        })

        galleryAdapter.setOnClickListener(object : PageAdapter.ClickListener {
            override fun setOnClickListener(view: View, position: Int) {
                galleryViewModel.imgUrlListLive.value?.let {
                    PhotoActivity.actionStart(this@PageActivity,
                        it, position, data.imgTitle)
                }
            }

        })

        galleryViewModel.setHref(data.href)
//
    }

}