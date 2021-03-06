package com.view.image.debug

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.view.image.analyzeRule.Rule
import com.view.image.databinding.ActivityDebugBinding

class DebugActivity : AppCompatActivity() {
    lateinit var binding: ActivityDebugBinding

    companion object {
        fun actionStart(context: Context, rule: Rule) {
            Intent(context, DebugActivity::class.java).apply {
                putExtra("rule", rule)
                context.startActivity(this)
            }
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDebugBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rule = intent.getSerializableExtra("rule") as Rule
        val viewModel = ViewModelProvider(this).get(DebugReqViewModel::class.java)
        viewModel.setRuleUtil(rule)

        viewModel.tabMap.observe(this, {
            binding.tab.text = it.toString()
            viewModel.getUrl()
        })

        viewModel.homeListData.observe(this, {
            binding.homeList.text = it
        })

        viewModel.homeReqSrc.observe(this, {
            binding.homeReqHref.text = it
        })

        viewModel.homeHref.observe(this, {
            when (it) {
                is List<*> -> {
                    binding.homeHref.text = "size:${it.size}   $it"
                    viewModel.getImagePage()
                }
                else -> {
                    binding.homeHref.text = "size:   $it"
                }
            }

//            }
        })

        viewModel.homeTitle.observe(this, {
            when (it) {
                is List<*> -> {
                    binding.homeTitle.text = "size:${it.size}   $it"
                }
                else -> {
                    binding.homeTitle.text = "size:   $it"
                }
            }
        })

        viewModel.homeSrc.observe(this, {
            when (it) {
                is List<*> -> {
                    binding.homeSrc.text = "size:${it.size}   $it"
                }
                else -> {
                    binding.homeSrc.text = "size:   $it"
                }
            }
        })

        viewModel.imageListData.observe(this, {
            binding.imageList.text = it
        })

        viewModel.nextPage.observe(this, {
            binding.imageNextPage.text = it
        })
    }
}