package com.view.image.manage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.view.image.databinding.ActivityManageRuleBinding

class ManageRuleActivity : AppCompatActivity() {
    lateinit var binding: ActivityManageRuleBinding

    companion object {
        fun actionStart(activity: Activity, code: Int) {
            Intent(activity, ManageRuleActivity::class.java).also {
                activity.startActivityForResult(it, code)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageRuleBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (mFragment in supportFragmentManager.fragments) {
            mFragment.onActivityResult(requestCode, resultCode, data)

        }
    }

    override fun onBackPressed() {
        ViewModelProvider(this).get(ManageRuleViewModel::class.java).saveRuleList()
        finish()
        super.onBackPressed()
    }

}