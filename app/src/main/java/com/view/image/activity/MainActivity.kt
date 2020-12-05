package com.view.image.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.view.image.databinding.ActivityMainBinding
import com.view.image.setting.Setting
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Setting.moveSettingFile(this, Setting.RULE_FILE_NAME)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // fragment里的onActivityResult起作用
        if (supportFragmentManager.fragments.size > 0) {
            val fragments: List<Fragment> = supportFragmentManager.fragments
            for (mFragment in fragments) {
                mFragment.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    // 双击退出应用
    private var firstTime: Long = 0
    override fun onBackPressed() {
        val secondTime = System.currentTimeMillis() //以毫秒为单位
        if (secondTime - firstTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show()
            firstTime = secondTime
        } else {
            finish()
            exitProcess(0)
        }
    }

}