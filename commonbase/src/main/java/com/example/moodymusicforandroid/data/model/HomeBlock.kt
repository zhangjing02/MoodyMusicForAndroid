package com.example.moodymusicforandroid.data.model

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

/**
 * 首页切片流响应数据结构 (SDUI - Server-Driven UI)
 */
data class HomeFeedResponse(
    @SerializedName("title") val title: String? = null,
    @SerializedName("issueNumber") val issueNumber: String? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("items") val items: List<HomeBlockItem> = emptyList()
)

/**
 * 首页切片多态数据项
 */
data class HomeBlockItem(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("data") val data: JsonElement? = null,
    @Transient val parsedData: Any? = null
)

/**
 * 切片类型常量定义
 */
object HomeBlockType {
    const val HERO_BANNER = "hero_banner"
    const val CATEGORY_TABS = "category_tabs"
    const val SECTION_TITLE = "section_title"
    const val ESSAY_CARD = "essay_card"
    const val ARTIST_GRID = "artist_grid"
    const val TRACK_LIST = "track_list"
    const val ARCHIVE_CARD = "archive_card"
    const val IMAGE_FEATURE = "image_feature"
    const val QUICK_ACTIONS = "quick_actions"
}

// ==================== 各切片具体数据结构 ====================

/**
 * 1. 深度专题 Hero 卡片数据
 */
data class HeroBannerData(
    @SerializedName("title") val title: String = "回响：寻找消失的黑胶灵魂",
    @SerializedName("tag") val tag: String = "深度专题 / DEEP DIVE",
    @SerializedName("summary") val summary: String = "在数字化的浪潮中，我们如何通过物理的震动重新找回听觉的本真？这是一场跨越半个世纪的模拟信号寻访。",
    @SerializedName("imageUrl") val imageUrl: String = "https://m-api.changgepd.top/storage/covers/hero/hero_acoustic_guitar.jpg",
    @SerializedName("articleId") val articleId: String = "vinyl_soul",
    @SerializedName("albumId") val albumId: String = "forest_echo",
    @SerializedName("albumTitle") val albumTitle: String = "林间碎影",
    @SerializedName("primaryActionText") val primaryActionText: String = "阅读专题",
    @SerializedName("secondaryActionText") val secondaryActionText: String = "聆听专辑"
)

/**
 * 2. 分类标签栏数据
 */
data class CategoryTabsData(
    @SerializedName("tabs") val tabs: List<CategoryTabItem> = emptyList(),
    @SerializedName("selectedId") val selectedId: String = "all"
)

data class CategoryTabItem(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("isSelected") val isSelected: Boolean = false
)

/**
 * 3. 栏目标题与期号数据
 */
data class SectionTitleData(
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String? = null,
    @SerializedName("actionText") val actionText: String? = null,
    @SerializedName("actionRoute") val actionRoute: String? = null
)

/**
 * 4. 选集随笔列表数据
 */
data class EssayCardData(
    @SerializedName("title") val title: String = "选集随笔",
    @SerializedName("essays") val essays: List<EssayItemData> = emptyList()
)

data class EssayItemData(
    @SerializedName("id") val id: String,
    @SerializedName("date") val date: String,
    @SerializedName("title") val title: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("readTime") val readTime: String? = null,
    @SerializedName("coverUrl") val coverUrl: String? = null
)

/**
 * 5. 艺术家网格/列表数据
 */
data class ArtistGridData(
    @SerializedName("title") val title: String? = "精选艺术家",
    @SerializedName("actionText") val actionText: String? = "浏览全部",
    @SerializedName("layout") val layout: String = "grid", // "grid", "row", "list"
    @SerializedName("artists") val artists: List<ArtistBlockItem> = emptyList()
)

data class ArtistBlockItem(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("genre") val genre: String,
    @SerializedName("albumCount") val albumCount: Int = 0,
    @SerializedName("avatarUrl") val avatarUrl: String? = null
)

/**
 * 6. 单曲试听列表数据
 */
data class TrackListData(
    @SerializedName("title") val title: String? = "今日单曲试听",
    @SerializedName("tracks") val tracks: List<TrackBlockItem> = emptyList()
)

data class TrackBlockItem(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("album") val album: String? = null,
    @SerializedName("duration") val duration: String = "03:45",
    @SerializedName("coverUrl") val coverUrl: String? = null,
    @SerializedName("audioUrl") val audioUrl: String? = null,
    @SerializedName("isPlaying") val isPlaying: Boolean = false
)

/**
 * 7. 时代留声机 / 档案卡片数据
 */
data class ArchiveCardData(
    @SerializedName("id") val id: String = "archive_1",
    @SerializedName("title") val title: String = "冀西南林家铺子",
    @SerializedName("subtitle") val subtitle: String = "万能青年旅店 • 2020",
    @SerializedName("description") val description: String? = "时代留声机：当代华语独立摇滚里程碑之作",
    @SerializedName("imageUrl") val imageUrl: String = "https://m-api.changgepd.top/storage/covers/albums/album_hebei_kirin.jpg",
    @SerializedName("badge") val badge: String? = "典藏黑胶",
    @SerializedName("targetRoute") val targetRoute: String? = null
)

/**
 * 8. 杂志视觉大图数据
 */
data class ImageFeatureData(
    @SerializedName("imageUrl") val imageUrl: String = "https://m-api.changgepd.top/storage/covers/hero/hero_acoustic_guitar.jpg",
    @SerializedName("caption") val caption: String? = "静谧之声 / SOUND OF SILENCE",
    @SerializedName("author") val author: String? = "Photo by Songbook Studio",
    @SerializedName("aspectRatio") val aspectRatio: Float = 1.78f,
    @SerializedName("targetRoute") val targetRoute: String? = null
)

/**
 * 9. 快捷功能入口数据
 */
data class QuickActionsData(
    @SerializedName("title") val title: String? = null,
    @SerializedName("actions") val actions: List<QuickActionItem> = emptyList()
)

data class QuickActionItem(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("subtitle") val subtitle: String? = null,
    @SerializedName("route") val route: String? = null
)

// ==================== 安全解析扩展方法 ====================

inline fun <reified T> HomeBlockItem.getParsedData(): T? {
    if (parsedData is T) return parsedData
    if (data == null) return null
    return try {
        Gson().fromJson(data, T::class.java)
    } catch (_: Exception) {
        null
    }
}

fun HomeBlockItem.toHeroBanner(): HeroBannerData = getParsedData<HeroBannerData>() ?: HeroBannerData()
fun HomeBlockItem.toCategoryTabs(): CategoryTabsData = getParsedData<CategoryTabsData>() ?: CategoryTabsData()
fun HomeBlockItem.toSectionTitle(): SectionTitleData = getParsedData<SectionTitleData>() ?: SectionTitleData(title = "")
fun HomeBlockItem.toEssayCard(): EssayCardData = getParsedData<EssayCardData>() ?: EssayCardData()
fun HomeBlockItem.toArtistGrid(): ArtistGridData = getParsedData<ArtistGridData>() ?: ArtistGridData()
fun HomeBlockItem.toTrackList(): TrackListData = getParsedData<TrackListData>() ?: TrackListData()
fun HomeBlockItem.toArchiveCard(): ArchiveCardData = getParsedData<ArchiveCardData>() ?: ArchiveCardData()
fun HomeBlockItem.toImageFeature(): ImageFeatureData = getParsedData<ImageFeatureData>() ?: ImageFeatureData()
fun HomeBlockItem.toQuickActions(): QuickActionsData = getParsedData<QuickActionsData>() ?: QuickActionsData()
