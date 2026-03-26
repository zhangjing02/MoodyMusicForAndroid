package com.example.moodymusicforandroid.ui.home.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.base.BaseActivity
import com.example.moodymusicforandroid.databinding.ActivityMainBinding
import com.example.moodymusicforandroid.ui.home.DiscoverFragment
import com.example.moodymusicforandroid.ui.home.HomeFragment
import com.example.moodymusicforandroid.ui.home.LibraryFragment
import com.example.moodymusicforandroid.ui.home.viewmodel.MainViewModel

/**
 * 主Activity
 * 使用泛型基类，简化代码
 * 包含 ViewPager2 和底部导航
 */
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private val TAG = "MainActivity"

    private lateinit var viewPagerAdapter: MainViewPagerAdapter

    override fun getViewModelClass() = MainViewModel::class.java

    override fun getLayoutId() = R.layout.activity_main

    override fun initView() {
        super.initView()
        setupViewPager()
        setupBottomNavigation()
    }

    private fun setupViewPager() {
        viewPagerAdapter = MainViewPagerAdapter(this)
        binding.viewPager.apply {
            adapter = viewPagerAdapter
            isUserInputEnabled = false // 禁用手动滑动，只通过底部导航切换
            offscreenPageLimit = 2 // 预加载所有页面
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    binding.viewPager.currentItem = 0
                    updateTitle("音信")
                    true
                }
                R.id.nav_discover -> {
                    binding.viewPager.currentItem = 1
                    updateTitle("发现")
                    true
                }
                R.id.nav_library -> {
                    binding.viewPager.currentItem = 2
                    updateTitle("收藏")
                    true
                }
                else -> false
            }
        }
    }

    private fun updateTitle(title: String) {
        binding.tvTitle.text = title
    }

    /**
     * 显示迷你播放器
     */
    fun showMiniPlayer() {
        binding.miniPlayer.visibility = View.VISIBLE
    }

    /**
     * 隐藏迷你播放器
     */
    fun hideMiniPlayer() {
        binding.miniPlayer.visibility = View.GONE
    }

    override fun initData() {
        super.initData()
        // 初始化数据
        viewModel.loadWelcomeMessage()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity destroyed")
    }

    /**
     * ViewPager2 Adapter
     */
    private inner class MainViewPagerAdapter(activity: BaseActivity<*, *>) : FragmentStateAdapter(activity) {

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HomeFragment()
                1 -> DiscoverFragment()
                2 -> LibraryFragment()
                else -> HomeFragment()
            }
        }
    }
}
