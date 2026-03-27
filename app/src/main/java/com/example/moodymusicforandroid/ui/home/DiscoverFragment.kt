package com.example.moodymusicforandroid.ui.home

import android.view.View
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.base.BaseFragment
import com.example.moodymusicforandroid.databinding.FragmentDiscoverBinding
import com.example.moodymusicforandroid.ui.home.adapter.DiscoverAdapter
import com.example.moodymusicforandroid.ui.home.model.DiscoverItem
import com.example.moodymusicforandroid.ui.home.viewmodel.DiscoverViewModel

class DiscoverFragment : BaseFragment<FragmentDiscoverBinding, DiscoverViewModel>() {

    private lateinit var adapter: DiscoverAdapter

    override fun getViewModelClass(): Class<DiscoverViewModel> = DiscoverViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_discover

    override fun initView() {
        super.initView()

        // Setup RecyclerView
        adapter = DiscoverAdapter { item, view -> onItemClick(item, view) }
        binding.recyclerView.adapter = adapter
    }

    override fun initData() {
        super.initData()

        // Load data - for now using mock data
        val discoverItems = createMockData()
        adapter.setItems(discoverItems)
    }

    private fun createMockData(): List<DiscoverItem> {
        return listOf(
            // Search Bar
            DiscoverItem.SearchItem(
                hint = "搜索艺术家、流派或乐器"
            ),

            // Categories
            DiscoverItem.CategoriesItem(
                categories = listOf(
                    DiscoverItem.CategoriesItem.Category("全部", true),
                    DiscoverItem.CategoriesItem.Category("华语", false),
                    DiscoverItem.CategoriesItem.Category("港台", false),
                    DiscoverItem.CategoriesItem.Category("摇滚", false),
                    DiscoverItem.CategoriesItem.Category("民谣", false),
                    DiscoverItem.CategoriesItem.Category("R&B", false),
                    DiscoverItem.CategoriesItem.Category("音乐综艺", false)
                )
            ),

            // Artists Section A
            DiscoverItem.ArtistHeaderItem(
                title = "A"
            ),
            DiscoverItem.ArtistItem(
                name = "Acoustic Echoes",
                description = "42 张专辑",
                imageUrl = null
            ),
            DiscoverItem.ArtistItem(
                name = "Ambient Rain",
                description = "128 张专辑",
                imageUrl = null
            ),

            // Artists Section B
            DiscoverItem.ArtistHeaderItem(
                title = "B"
            ),
            DiscoverItem.ArtistItem(
                name = "Bamboo Flute",
                description = "15 张专辑",
                imageUrl = null
            )
        )
    }

    private fun onItemClick(item: DiscoverItem, view: View) {
        // Handle item click
        when (item) {
            is DiscoverItem.SearchItem -> {
                // Handle search
            }
            is DiscoverItem.CategoriesItem -> {
                // Handle category selection
            }
            is DiscoverItem.ArtistItem -> {
                // Navigate to artist detail
            }
            else -> {
                // Default handling
            }
        }
    }
}
