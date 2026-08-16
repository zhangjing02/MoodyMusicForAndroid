package com.example.moodymusicforandroid.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moodymusicforandroid.ui.home.viewmodel.LibraryViewModel
import com.example.moodymusicforandroid.ui.music.viewmodel.AlbumSocialViewModel
import androidx.activity.ComponentActivity
import androidx.compose.runtime.livedata.observeAsState
import com.example.moodymusicforandroid.data.model.AlbumSocialContent
import com.example.moodymusicforandroid.ui.home.components.CommunitySocialSection
import com.example.moodymusicforandroid.ui.home.components.FavoriteAlbumsSection
import com.example.moodymusicforandroid.ui.home.components.FollowedArtistsSection

/**
 * 音乐库页面主屏幕组件
 *
 * 展示用户的音乐收藏内容和社区社交动态。
 * 内部已将不同模块抽取为独立的组件，以便于维护。
 */
@Composable
fun LibraryScreen(
    /** 用于处理音乐库业务逻辑的 ViewModel */
    viewModel: LibraryViewModel = viewModel(),
    /** 用于处理社交动态的 ViewModel，作用域为 Activity */
    socialViewModel: AlbumSocialViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
) {
    val socialContent by socialViewModel.socialContent.observeAsState()
    val errorMessage by socialViewModel.errorMessage.observeAsState()
    var commentText by remember { mutableStateOf("") }
    
    // Simulate fetching on mount
    LaunchedEffect(Unit) {
        socialViewModel.fetchSocialContent("night_peace")
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            FavoriteAlbumsSection()
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            FollowedArtistsSection()
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            CommunitySocialSection(
                content = socialContent,
                errorMessage = errorMessage?.toString(),
                commentText = commentText,
                onCommentTextChange = { commentText = it },
                onRetryClick = { socialViewModel.fetchSocialContent("night_peace") },
                onSendClick = {
                    if (commentText.isNotBlank()) {
                        socialContent?.id?.let {
                            socialViewModel.postReply(it, "night_peace", commentText)
                        }
                        commentText = ""
                    }
                }
            )
        }
    }
}
