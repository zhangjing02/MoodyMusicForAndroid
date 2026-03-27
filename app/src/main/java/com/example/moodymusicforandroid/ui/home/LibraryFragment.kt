package com.example.moodymusicforandroid.ui.home

import android.view.View
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.base.BaseFragment
import com.example.moodymusicforandroid.databinding.FragmentLibraryBinding
import com.example.moodymusicforandroid.ui.home.adapter.LibraryAdapter
import com.example.moodymusicforandroid.ui.home.model.LibraryItem
import com.example.moodymusicforandroid.ui.home.viewmodel.LibraryViewModel

class LibraryFragment : BaseFragment<FragmentLibraryBinding, LibraryViewModel>() {

    private lateinit var adapter: LibraryAdapter

    override fun getViewModelClass(): Class<LibraryViewModel> = LibraryViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_library

    override fun initView() {
        super.initView()

        // Setup RecyclerView
        adapter = LibraryAdapter { item, view -> onItemClick(item, view) }
        binding.recyclerView.adapter = adapter
    }

    override fun initData() {
        super.initData()

        // Load data - for now using mock data
        val libraryItems = createMockData()
        adapter.setItems(libraryItems)
    }

    private fun createMockData(): List<LibraryItem> {
        return listOf(
            // Albums Section
            LibraryItem.AlbumsHeaderItem(
                title = "收藏专辑",
                seeAllText = "查看全部"
            ),
            LibraryItem.AlbumFeaturedItem(
                title = "夜的宁静",
                artist = "白日梦乐团",
                imageUrl = null
            ),
            LibraryItem.AlbumSmallItem(
                title = "森林回响",
                artist = "自然主义者",
                imageUrl = null
            ),
            LibraryItem.AlbumSmallItem(
                title = "潮汐呼吸",
                artist = "海浪诗人",
                imageUrl = null
            ),

            // Artists Section
            LibraryItem.ArtistsHeaderItem(
                title = "关注的艺人"
            ),
            LibraryItem.ArtistItem(
                name = "大提琴鸣响",
                description = "342k 听众 • 治愈系",
                imageUrl = null,
                isFollowing = true
            ),
            LibraryItem.ArtistItem(
                name = "林间风",
                description = "128k 听众 • 民谣",
                imageUrl = null,
                isFollowing = true
            ),
            LibraryItem.ArtistItem(
                name = "空灵笛音",
                description = "85k 听众 • 冥想",
                imageUrl = null,
                isFollowing = true
            )
        )
    }

    private fun onItemClick(item: LibraryItem, view: View) {
        // Handle item click
        when (item) {
            is LibraryItem.AlbumFeaturedItem -> {
                // Navigate to album detail
            }
            is LibraryItem.AlbumSmallItem -> {
                // Navigate to album detail
            }
            is LibraryItem.ArtistItem -> {
                // Navigate to artist detail or handle follow/unfollow
            }
            else -> {
                // Default handling
            }
        }
    }
}
