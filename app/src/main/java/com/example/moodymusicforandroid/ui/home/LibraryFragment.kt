package com.example.moodymusicforandroid.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jpush.android.api.JPushInterface
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.base.BaseFragment
import com.example.moodymusicforandroid.common.preferences.PreferencesManager
import com.example.moodymusicforandroid.databinding.FragmentLibraryBinding
import com.example.moodymusicforandroid.common.utils.AppFlags
import com.example.moodymusicforandroid.receiver.JPushReceiver
import com.example.moodymusicforandroid.ui.home.adapter.LibraryAdapter
import com.example.moodymusicforandroid.ui.home.model.LibraryItem
import com.example.moodymusicforandroid.ui.home.viewmodel.LibraryViewModel
import com.example.moodymusicforandroid.ui.music.activity.AlbumSocialAdapter
import com.example.moodymusicforandroid.ui.music.viewmodel.AlbumSocialViewModel

class LibraryFragment : BaseFragment<FragmentLibraryBinding, LibraryViewModel>() {

    private lateinit var libraryAdapter: LibraryAdapter
    private val socialViewModel: AlbumSocialViewModel by activityViewModels()
    private var currentAlbumId: String = "night_peace" // 默认演示专辑ID

    // 广播接收器：处理来自 JPushReceiver 的实时刷新信号
    private val refreshReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val receivedAlbumId = intent?.getStringExtra(JPushReceiver.EXTRA_ALBUM_ID)
            if (receivedAlbumId == currentAlbumId) {
                socialViewModel.fetchSocialContent(currentAlbumId)
            }
        }
    }

    override fun getViewModelClass(): Class<LibraryViewModel> = LibraryViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_library

    override fun initView() {
        super.initView()

        // 1. Setup Main Library RecyclerView (Master Branch logic)
        libraryAdapter = LibraryAdapter { item, view -> onItemClick(item, view) }
        binding.recyclerView.adapter = libraryAdapter

        // 2. Setup Social Discussion Section (Social Feature logic)
        binding.rvReplies.layoutManager = LinearLayoutManager(requireContext())
        
        // 发送按钮逻辑
        binding.btnSend.setOnClickListener {
            val content = binding.etComment.text.toString()
            if (content.isBlank()) return@setOnClickListener
            
            val social = socialViewModel.socialContent.value
            if (social == null) {
                socialViewModel.postMainPost(currentAlbumId, content)
            } else {
                socialViewModel.postReply(social.id, currentAlbumId, content)
            }
            binding.etComment.setText("")
        }

        // 观察社交内容变化
        socialViewModel.socialContent.observe(viewLifecycleOwner) { content ->
            if (content != null) {
                binding.llSocialSection.visibility = View.VISIBLE
                binding.cardMainPost.visibility = View.VISIBLE
                binding.tvMainPostContent.text = content.content
                binding.tvMainPostAuthor.text = "来自: ${content.author.username}"
                binding.tvMainPostTime.text = content.createdAt
                
                binding.rvReplies.adapter = AlbumSocialAdapter(content.replies)
            } else {
                // 如果没有内容，暂时隐藏社交模块或保持现状
                binding.cardMainPost.visibility = View.GONE
                binding.rvReplies.adapter = null
            }
        }
    }

    override fun initData() {
        super.initData()

        // Load Library data (Master Branch mock data)
        val libraryItems = createMockData()
        libraryAdapter.setItems(libraryItems)
        
        // Load Social data
        socialViewModel.fetchSocialContent(currentAlbumId)
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
                // Navigate to album detail or update social content for this album
                currentAlbumId = "night_peace"
                socialViewModel.fetchSocialContent(currentAlbumId)
            }
            is LibraryItem.AlbumSmallItem -> {
                // Navigate to album detail
            }
            is LibraryItem.ArtistItem -> {
                // Navigate to artist detail
            }
            else -> {}
        }
    }

    override fun onResume() {
        super.onResume()
        // 1. 设置全局标记，供 JPushReceiver 判断前台状态
        AppFlags.isAlbumDetailVisible = true
        AppFlags.visibleAlbumId = currentAlbumId

        // 2. 消费脏标记
        if (AppFlags.hasNewComments && AppFlags.pendingRefreshAlbumId == currentAlbumId) {
            socialViewModel.fetchSocialContent(currentAlbumId)
            AppFlags.hasNewComments = false
            AppFlags.pendingRefreshAlbumId = ""
        }

        // 3. 绑定 JPush Tag
        val classId = PreferencesManager.getClassId() ?: "default"
        val tag = "album_${currentAlbumId}_class_${classId}"
        JPushInterface.setTags(requireContext(), 1, setOf(tag))

        // 4. 注册实时刷新广播
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            refreshReceiver, IntentFilter(JPushReceiver.BROADCAST_ACTION_REFRESH_COMMENTS)
        )
    }

    override fun onPause() {
        super.onPause()
        AppFlags.isAlbumDetailVisible = false
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(refreshReceiver)
        JPushInterface.cleanTags(requireContext(), 2)
    }
}
