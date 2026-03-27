package com.example.moodymusicforandroid.ui.home

import android.view.View
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.base.BaseFragment
import com.example.moodymusicforandroid.databinding.FragmentHomeBinding
import com.example.moodymusicforandroid.ui.home.adapter.HomeAdapter
import com.example.moodymusicforandroid.ui.home.model.HomeItem
import com.example.moodymusicforandroid.ui.home.viewmodel.HomeViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    private lateinit var adapter: HomeAdapter

    override fun getViewModelClass(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun initView() {
        super.initView()

        // Setup RecyclerView
        adapter = HomeAdapter { item, view -> onItemClick(item, view) }
        binding.recyclerView.adapter = adapter
    }

    override fun initData() {
        super.initData()

        // Load data - for now using mock data
        val homeItems = createMockData()
        adapter.setItems(homeItems)
    }

    private fun createMockData(): List<HomeItem> {
        return listOf(
            // Hero Section
            HomeItem.HeroItem(
                title = "听，风吹过的声音",
                subtitle = "在这个嘈杂的世界，为你保留一方静谧。让音乐成为身心的良药。"
            ),

            // Featured Section
            HomeItem.FeaturedHeaderItem(
                title = "余音 · 回响",
                seeAllText = "查看全部"
            ),
            HomeItem.FeaturedMainItem(
                tag = "今日推荐",
                title = "山间雾起时",
                description = "白噪音与长笛的对话，带你重返大自然的呼吸频率。",
                imageUrl = null
            ),
            HomeItem.FeaturedSmallItem(
                title = "深海潜行",
                description = "45分钟 · 沉浸式冥想",
                imageUrl = null,
                iconResId = R.drawable.ic_play_circle
            ),
            HomeItem.FeaturedSmallItem(
                title = "雨夜回廊",
                description = "听见屋檐落下的节奏",
                imageUrl = null,
                iconResId = R.drawable.ic_search,
                tag = "Natural"
            ),

            // Articles Section
            HomeItem.ArticlesHeaderItem(
                title = "读物 · 志"
            ),
            HomeItem.ArticleItem(
                tag = "Vol. 124 · 2024",
                title = "民谣里的土地与乡愁",
                description = "那些从土地里生长出来的旋律，为何总能触动我们内心最柔软的部分？",
                imageUrl = null
            ),
            HomeItem.ArticleItem(
                tag = "Vol. 123 · 2024",
                title = "声音生态学：重建与自然的关系",
                description = "在这个数字化的时代，如何通过聆听来重新连接我们所处的环境？",
                imageUrl = null
            ),
            HomeItem.ArticleItem(
                tag = "Vol. 122 · 2024",
                title = "慢下来的艺术：黑胶唱片的复兴",
                description = "从实体触感到模拟音质，黑胶唱片如何在这个快节奏的时代教我们珍惜当下。",
                imageUrl = null
            )
        )
    }

    private fun onItemClick(item: HomeItem, view: View) {
        // Handle item click
        when (item) {
            is HomeItem.HeroItem -> {
                // Handle hero click
            }
            is HomeItem.FeaturedMainItem -> {
                // Navigate to detail
            }
            is HomeItem.ArticleItem -> {
                // Navigate to article
            }
            else -> {
                // Default handling
            }
        }
    }
}
