package com.example.moodymusicforandroid.ui.home.activity

import android.content.Intent
import android.util.Log
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.base.BaseActivity
import com.example.moodymusicforandroid.databinding.ActivityMainBinding
import com.example.moodymusicforandroid.ui.home.viewmodel.MainViewModel
import com.example.moodymusicforandroid.ui.test.activity.ApiTestActivity

/**
 * 主Activity
 * 使用泛型基类，简化代码
 */
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private val TAG = "MainActivity"

    override fun getViewModelClass() = MainViewModel::class.java

    override fun getLayoutId() = R.layout.activity_main

    override fun setupBindingVariables() {
        super.setupBindingVariables()
        // TODO: 手动设置viewModel到binding
        // binding.viewModel = viewModel
    }

    override fun initView() {
        super.initView()
        setupClickListeners()
    }

    override fun initData() {
        super.initData()
        // 初始化数据
        viewModel.loadWelcomeMessage()
    }

    private fun setupClickListeners() {
        // 跳转到 API 测试页面
        binding.btnTestNetwork.setOnClickListener {
            val intent = Intent(this, ApiTestActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity destroyed")
    }
}
