package com.example.moodymusicforandroid.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.ui.home.components.ArtistDirectoryItem
import com.example.moodymusicforandroid.ui.home.viewmodel.DiscoverViewModel
import com.example.moodymusicforandroid.ui.theme.SongbookColors
import kotlinx.coroutines.launch

import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState

/**
 * 发现 / 档案名录屏幕 (Discover / A-Z Archive)
 *
 * 特色：
 * 1. 极简纸质搜索框与流派筛选胶囊
 * 2. 杂志风大标题「档案名录」与 A-Z Archive 英文副标
 * 3. 艺术家列表与首字母快速定位导航
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    modifier: Modifier = Modifier,
    viewModel: DiscoverViewModel = viewModel(),
    onMenuClick: () -> Unit = {},
    onArtistClick: (String, String) -> Unit = { _, _ -> }
) {
    val artistsFromVm by viewModel.artists.observeAsState(emptyList())
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var searchQuery by remember { mutableStateOf("") }
    val genres = listOf("全部", "爵士乐", "民谣", "古典", "电子", "摇滚")
    var selectedGenre by remember { mutableStateOf("全部") }

    LaunchedEffect(Unit) {
        viewModel.fetchArtists()
    }

    // 默认内置的优质艺术家档案数据（保障离线/无网络时的完整视觉呈现）
    val defaultArtists = listOf(
        DirectoryArtist(
            id = "abigail_chen",
            name = "阿比盖尔·陈",
            initial = "A",
            genre = "爵士乐",
            albumCount = 12,
            avatarUrl = "/storage/artists/artist_abigail_chen.jpg",
            fallbackRes = R.drawable.artist_abigail_chen
        ),
        DirectoryArtist(
            id = "alan_walker",
            name = "艾伦·沃克",
            initial = "A",
            genre = "电子",
            albumCount = 8,
            avatarUrl = "/storage/artists/artist_alan_walker.jpg",
            fallbackRes = R.drawable.artist_alan_walker
        ),
        DirectoryArtist(
            id = "beatrice",
            name = "碧翠丝",
            initial = "B",
            genre = "古典",
            albumCount = 5,
            avatarUrl = "/storage/artists/artist_beatrice.jpg",
            fallbackRes = R.drawable.artist_beatrice
        ),
        DirectoryArtist(
            id = "charlie_wood",
            name = "查理·伍德",
            initial = "C",
            genre = "民谣",
            albumCount = 15,
            avatarUrl = "/storage/artists/artist_charlie.jpg",
            fallbackRes = R.drawable.artist_charlie
        ),
        DirectoryArtist(
            id = "hero_acoustic",
            name = "林间碎影",
            initial = "L",
            genre = "民谣",
            albumCount = 9,
            avatarUrl = "/storage/artists/artist_1.jpg",
            fallbackRes = R.drawable.hero_acoustic_guitar
        ),
        DirectoryArtist(
            id = "sunset_rollercoaster",
            name = "落日飞车",
            initial = "L",
            genre = "爵士乐",
            albumCount = 6,
            avatarUrl = "/storage/covers/albums/album__3_6.jpg",
            fallbackRes = R.drawable.album_modern_jazz
        ),
        DirectoryArtist(
            id = "omnipresent_youth",
            name = "万能青年旅店",
            initial = "W",
            genre = "摇滚",
            albumCount = 2,
            avatarUrl = "/storage/covers/albums/album_hebei_kirin.jpg",
            fallbackRes = R.drawable.album_hebei_kirin
        ),
        DirectoryArtist(
            id = "ryuichi_sakamoto",
            name = "坂本龍一",
            initial = "S",
            genre = "古典",
            albumCount = 24,
            avatarUrl = "/storage/artists/artist_5.jpg",
            fallbackRes = R.drawable.album_classical_piano
        )
    )

    // 合并后端数据与本地数据
    val allArtists = remember(artistsFromVm) {
        if (artistsFromVm.isNotEmpty()) {
            artistsFromVm.map { vmArtist ->
                DirectoryArtist(
                    id = vmArtist.id,
                    name = vmArtist.name,
                    initial = vmArtist.group ?: vmArtist.name.firstOrNull()?.uppercase() ?: "#",
                    genre = vmArtist.category ?: "民谣",
                    albumCount = vmArtist.albumCount,
                    avatarUrl = vmArtist.avatar,
                    fallbackRes = R.drawable.artist_abigail_chen
                )
            }
        } else {
            defaultArtists
        }
    }

    // 过滤与分组
    val filteredArtists = allArtists.filter { artist ->
        val matchesSearch = searchQuery.isBlank() || 
            artist.name.contains(searchQuery, ignoreCase = true) || 
            artist.genre.contains(searchQuery, ignoreCase = true)
        val matchesGenre = selectedGenre == "全部" || artist.genre == selectedGenre
        matchesSearch && matchesGenre
    }

    val groupedArtists = filteredArtists.groupBy { it.initial }.toSortedMap()
    val alphabetList = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")

    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.fetchArtists() },
            state = pullToRefreshState,
            modifier = Modifier.fillMaxSize(),
            indicator = {
                Indicator(
                    state = pullToRefreshState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = statusBarTop + 6.dp),
                    isRefreshing = isRefreshing,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    color = SongbookColors.BurntOrange
                )
            }
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(
                    top = statusBarTop + 6.dp,
                    bottom = 140.dp
                )
            ) {
            // 1. TopBar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onMenuClick, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = SongbookColors.BurntOrange
                        )
                    }

                    Text(
                        text = "歌手",
                        style = MaterialTheme.typography.headlineMedium,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 2. 极简低饱和搜索框
            item {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    placeholder = {
                        Text(
                            text = "搜索歌手、流派或乐器",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = SongbookColors.Outline
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. 流派筛选胶囊
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(genres) { genre ->
                        val isSelected = selectedGenre == genre
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) SongbookColors.BurntOrange else MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier.clickable { selectedGenre = genre }
                        ) {
                            Text(
                                text = genre,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }

            // 4. 档案名录大标题
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "档案名录",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "A-Z ARCHIVE",
                        style = MaterialTheme.typography.labelSmall,
                        color = SongbookColors.Outline,
                        letterSpacing = 2.sp
                    )
                }
                HorizontalDivider(
                    color = SongbookColors.OutlineVariant.copy(alpha = 0.25f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 5. 按字母分组的艺术家列表
            groupedArtists.forEach { (initial, artistsInGroup) ->
                item {
                    Text(
                        text = initial,
                        style = MaterialTheme.typography.titleLarge,
                        color = SongbookColors.BurntOrange,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                    )
                }

                items(artistsInGroup) { artist ->
                    ArtistDirectoryItem(
                        name = artist.name,
                        genre = artist.genre,
                        albumCount = artist.albumCount,
                        avatarUrl = artist.avatarUrl,
                        fallbackRes = artist.fallbackRes,
                        onClick = { onArtistClick(artist.id, artist.name) }
                    )
                }
            }

            if (groupedArtists.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "未找到相关艺术家档案",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

        // 6. 右侧快捷字母导航索引栏
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp, top = 80.dp, bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            alphabetList.forEach { letter ->
                val hasArtists = groupedArtists.containsKey(letter)
                Text(
                    text = letter,
                    fontSize = 9.sp,
                    fontWeight = if (hasArtists) FontWeight.Bold else FontWeight.Normal,
                    color = if (hasArtists) SongbookColors.BurntOrange else SongbookColors.Outline.copy(alpha = 0.35f),
                    modifier = Modifier
                        .clickable(enabled = hasArtists) {
                            coroutineScope.launch {
                                // 滚动到该字母位置
                                listState.animateScrollToItem(0)
                            }
                        }
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
    }
}

/**
 * 档案名录模型
 */
data class DirectoryArtist(
    val id: String,
    val name: String,
    val initial: String,
    val genre: String,
    val albumCount: Int,
    val avatarUrl: String?,
    val fallbackRes: Int
)
