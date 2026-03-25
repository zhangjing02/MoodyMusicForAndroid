package com.example.moodymusicforandroid.ui.music.activity

import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.base.BaseActivity
import com.example.moodymusicforandroid.databinding.ActivityMusicListBinding
import com.example.moodymusicforandroid.ui.music.viewmodel.MusicListViewModel

/**
 * 音乐列表Activity
 * 示例：展示如何使用基类
 */
class MusicListActivity : BaseActivity<ActivityMusicListBinding, MusicListViewModel>() {

    override fun getViewModelClass() = MusicListViewModel::class.java

    override fun getLayoutId() = R.layout.activity_music_list

    override fun setupBindingVariables() {
        super.setupBindingVariables()
        binding.viewModel = viewModel
    }

    override fun initView() {
        super.initView()
        setupClickListeners()
        setupRecyclerView()
    }

    override fun initData() {
        super.initData()
        // 加载推荐音乐
        viewModel.getRecommendMusic()
    }

    private fun setupClickListeners() {
        // 设置点击监听
    }

    private fun setupRecyclerView() {
        // 设置RecyclerView
    }

    override fun handleError(errorMsg: String) {
        super.handleError(errorMsg)
        // 自定义错误处理
    }
}
