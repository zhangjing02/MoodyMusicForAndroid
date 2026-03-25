# RecyclerView 多布局适配器使用指南

本项目基于 **BaseRecyclerViewAdapterHelper (BRVAH)** 库封装了强大的 RecyclerView 适配器，支持单布局和多布局列表。

## 核心组件

### 1. BaseAdapter - 单布局适配器
用于单一 item 类型的列表

### 2. BaseMultiAdapter - 多布局适配器
用于多种 item 类型的复杂列表

### 3. MultiItem - 多布局接口
数据实体实现此接口以支持多布局

---

## 快速开始

### 示例 1：单布局列表

#### 步骤1：创建数据类
```kotlin
data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val coverUrl: String
)
```

#### 步骤2：创建适配器
```kotlin
import com.example.moodymusicforandroid.common.recyclerview.BaseAdapter
import com.example.moodymusicforandroid.common.recyclerview.R
import com.example.moodymusicforandroid.common.recyclerview.Song

class SongAdapter : BaseAdapter<Song, BaseViewHolder> {

    constructor() : super(R.layout.item_song)

    constructor(data: MutableList<Song>) : super(R.layout.item_song, data)

    override fun onBind(holder: BaseViewHolder, position: Int, item: Song) {
        // 绑定数据
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_artist, item.artist)

        // 加载图片
        ImageLoader.with(context)
            .load(item.coverUrl)
            .into(holder.getView(R.id.iv_cover))
    }
}
```

#### 步骤3：使用适配器
```kotlin
class MusicActivity : BaseActivity<ActivityMusicBinding, MusicViewModel>() {

    private lateinit var adapter: SongAdapter
    private val songList = mutableListOf<Song>()

    override fun initView() {
        // 初始化适配器
        adapter = SongAdapter()

        // 设置 RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MusicActivity)
            adapter = this@MusicActivity.adapter
        }

        // 设置 item 点击事件
        adapter.setOnItemClickListener { _, view, position ->
            val song = songList[position]
            playMusic(song)
        }

        // 设置子控件点击事件
        adapter.addChildClickListener(R.id.btn_like) { _, view, position ->
            val song = songList[position]
            likeSong(song)
        }
    }

    fun updateSongs(newSongs: List<Song>) {
        songList.clear()
        songList.addAll(newSongs)
        adapter.setList(songList)
        adapter.notifyDataSetChanged()
    }
}
```

---

### 示例2：多布局列表（复杂布局）

#### 步骤1：创建多布局数据类
```kotlin
import com.example.moodymusicforandroid.common.recyclerview.MultiItem

data class MusicListItem(
    override val itemType: Int,  // 必须：item 类型
    val data: Any? = null
) : MultiItem {

    companion object {
        const val TYPE_TITLE = 1          // 标题
        const val TYPE_SONG = 2           // 歌曲
        const val TYPE_ALBUM = 3          // 专辑
        const val TYPE_DIVIDER = 4        // 分割线
        const val TYPE_LOAD_MORE = 5      // 加载更多
    }

    // 便捷构造函数
    constructor(title: String) : this(TYPE_TITLE, title)

    constructor(song: Song) : this(TYPE_SONG, song)

    constructor(album: Album) : this(TYPE_ALBUM, album)
}
```

#### 步骤2：创建多布局适配器
```kotlin
import com.example.moodymusicforandroid.common.recyclerview.BaseMultiAdapter

class MusicMultiAdapter : BaseMultiAdapter<MusicListItem>() {

    init {
        // 注册布局类型
        addItemType(MusicListItem.TYPE_TITLE, R.layout.item_title)
        addItemType(MusicListItem.TYPE_SONG, R.layout.item_song)
        addItemType(MusicListItem.TYPE_ALBUM, R.layout.item_album)
        addItemType(MusicListItem.TYPE_DIVIDER, R.layout.item_divider)
        addItemType(MusicListItem.TYPE_LOAD_MORE, R.layout.item_load_more)
    }

    override fun onBind(holder: BaseViewHolder, position: Int, item: MusicListItem) {
        when (item.itemType) {
            MusicListItem.TYPE_TITLE -> bindTitleItem(holder, item)
            MusicListItem.TYPE_SONG -> bindSongItem(holder, item)
            MusicListItem.TYPE_ALBUM -> bindAlbumItem(holder, item)
            MusicListItem.TYPE_DIVIDER -> bindDividerItem(holder, item)
            MusicListItem.TYPE_LOAD_MORE -> bindLoadMoreItem(holder, item)
        }
    }

    private fun bindTitleItem(holder: BaseViewHolder, item: MusicListItem) {
        val title = item.data as? String ?: ""
        holder.setText(R.id.tv_title, title)
    }

    private fun bindSongItem(holder: BaseViewHolder, item: MusicListItem) {
        val song = item.data as? Song ?: return
        holder.setText(R.id.tv_title, song.title)
        holder.setText(R.id.tv_artist, song.artist)

        ImageLoader.with(context)
            .load(song.coverUrl)
            .into(holder.getView(R.id.iv_cover))
    }

    private fun bindAlbumItem(holder: BaseViewHolder, item: MusicListItem) {
        val album = item.data as? Album ?: return
        holder.setText(R.id.tv_album_name, album.name)
        holder.setText(R.id.tv_artist, album.artist)

        ImageLoader.with(context)
            .load(album.coverUrl)
            .into(holder.getView(R.id.iv_cover))
    }

    private fun bindDividerItem(holder: BaseViewHolder, item: MusicListItem) {
        // 分割线不需要绑定数据
    }

    private fun bindLoadMoreItem(holder: BaseViewHolder, item: MusicListItem) {
        // 显示加载中
        holder.setVisible(R.id.progress_loading, true)
        holder.setText(R.id.tv_text, "加载中...")
    }
}
```

#### 步骤3：使用多布局适配器
```kotlin
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>() {

    private lateinit var adapter: MusicMultiAdapter
    private val items = mutableListOf<MusicListItem>()

    override fun initView() {
        adapter = MusicMultiAdapter()

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = this@HomeActivity.adapter
        }

        // 设置点击事件
        adapter.setOnItemClickListener { _, view, position ->
            val item = items[position]
            when (item.itemType) {
                MusicListItem.TYPE_SONG -> {
                    val song = item.data as? Song
                    song?.let { playSong(it) }
                }
                MusicListItem.TYPE_ALBUM -> {
                    val album = item.data as? Album
                    album?.let { openAlbum(it) }
                }
            }
        }

        // 加载数据
        loadHomeData()
    }

    private fun loadHomeData() {
        // 添加标题
        items.add(MusicListItem("推荐歌曲"))

        // 添加歌曲
        viewModel.getRecommendedSongs().observe(this) { songs ->
            songs.forEach { song ->
                items.add(MusicListItem(song))
            }
            adapter.setList(items)
            adapter.notifyDataSetChanged()
        }

        // 添加分割线
        items.add(MusicListItem(MusicListItem.TYPE_DIVIDER, null))

        // 添加专辑标题
        items.add(MusicListItem("热门专辑"))

        // 添加专辑
        viewModel.getHotAlbums().observe(this) { albums ->
            albums.forEach { album ->
                items.add(MusicListItem(album))
            }
            adapter.setList(items)
            adapter.notifyDataSetChanged()
        }
    }
}
```

---

### 示例3：使用 BaseMultiItem 简化多布局

#### 步骤1：创建数据类继承 BaseMultiItem
```kotlin
import com.example.moodymusicforandroid.common.recyclerview.BaseMultiItem

// 标题 item
data class TitleItem(
    val title: String
) : BaseMultiItem(MultiItemType.TYPE_TITLE)

// 歌曲 item
data class SongItem(
    val song: Song
) : BaseMultiItem(MultiItemType.TYPE_CONTENT)

// 分割线 item
data class DividerItem(
    val height: Int = 1
) : BaseMultiItem(MultiItemType.TYPE_DIVIDER)
```

#### 步骤2：创建适配器
```kotlin
class SimplifiedMultiAdapter : BaseMultiAdapter<BaseMultiItem>() {

    init {
        addItemType(MultiItemType.TYPE_TITLE, R.layout.item_title)
        addItemType(MultiItemType.TYPE_CONTENT, R.layout.item_song)
        addItemType(MultiItemType.TYPE_DIVIDER, R.layout.item_divider)
    }

    override fun onBind(holder: BaseViewHolder, position: Int, item: BaseMultiItem) {
        when (item) {
            is TitleItem -> {
                holder.setText(R.id.tv_title, item.title)
            }
            is SongItem -> {
                val song = item.song
                holder.setText(R.id.tv_title, song.title)
                holder.setText(R.id.tv_artist, song.artist)
            }
            is DividerItem -> {
                // 可以设置高度等
                val layoutParams = holder.itemView.layoutParams
                layoutParams.height = item.height
            }
        }
    }
}
```

---

## 高级功能

### 1. 添加头部和尾部

```kotlin
// 添加头部 View
val headerView = layoutInflater.inflate(R.layout.header_title, binding.recyclerView, false)
adapter.setHeaderView(headerView)

// 添加尾部 View
val footerView = layoutInflater.inflate(R.layout.footer_load_more, binding.recyclerView, false)
adapter.setFooterView(footerView)

// 添加多个头部
adapter.addHeaderView(getHeaderView(1))
adapter.addHeaderView(getHeaderView(2))

// 清除头部
adapter.removeAllHeaderView()
adapter.removeAllFooters()
```

### 2. 空布局

```kotlin
// 设置空布局
val emptyView = layoutInflater.inflate(R.layout.empty_view, binding.recyclerView, false)
adapter.setEmptyView(emptyView)

// 或者使用布局资源 ID
adapter.setEmptyView(R.layout.empty_view)
```

### 3. 动画

```kotlin
// 开启默认动画（渐显+滑动）
adapter.animationEnable = true

// 使用渐显动画
adapter.animationItem = AlphaInAnimation()

// 使用缩放动画
adapter.animationItem = ScaleInAnimation()

// 自定义动画时长
adapter.animationDuration = 300

// 首次加载不显示动画
adapter.isFirstOnly(false)
```

### 4. 拖拽和滑动删除

```kotlin
// 启用拖拽
adapter.dragEnabled = true

// 启用滑动删除
adapter.swipeEnabled = true

// 同时启用
adapter.dragSwipeEnabled = true

// 设置拖拽方向
adapter.dragMoveFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN

// 设置滑动方向
adapter.swipeMoveFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT

// 监听拖拽事件
adapter.setOnItemDragListener listener : OnItemDragListener

// 监听滑动事件
adapter.setOnItemSwipeListener listener : OnItemSwipeListener
```

### 5. 分组和粘性头部

```kotlin
// 使用分组适配器
class GroupAdapter : BaseSectionQuickAdapter<Group, SectionEntity, BaseViewHolder>() {
    // 实现分组逻辑
}
```

---

## 布局文件示例

### item_song.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="16dp">

    <ImageView
        android:id="@+id/iv_cover"
        android:layout_width="60dp"
        android:layout_height="60dp"
        android:scaleType="centerCrop"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/tv_title"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="12dp"
        android:textSize="16sp"
        android:textColor="@android:color/black"
        android:ellipsize="end"
        android:maxLines="1"
        app:layout_constraintStart_toEndOf="@id/iv_cover"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="@id/iv_cover" />

    <TextView
        android:id="@+id/tv_artist"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="12dp"
        android:layout_marginTop="4dp"
        android:textSize="14sp"
        android:textColor="@android:color/darker_gray"
        android:ellipsize="end"
        android:maxLines="1"
        app:layout_constraintStart_toEndOf="@id/iv_cover"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toBottomOf="@id/tv_title" />

    <ImageButton
        android:id="@+id/btn_like"
        android:layout_width="48dp"
        android:layout_height="48dp"
        android:background="?attr/selectableItemBackgroundBorderless"
        android:src="@android:drawable/star_off"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### item_title.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:paddingStart="16dp"
    android:paddingTop="12dp"
    android:paddingEnd="16dp"
    android:paddingBottom="8dp">

    <TextView
        android:id="@+id/tv_title"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="18sp"
        android:textStyle="bold"
        android:textColor="@android:color/black" />

    <View
        android:layout_width="match_parent"
        android:layout_height="1dp"
        android:layout_marginTop="8dp"
        android:background="@android:color/darker_gray" />

</LinearLayout>
```

### item_divider.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<View
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="8dp"
    android:background="#F5F5F5" />
```

---

## 常用方法

### 数据操作

```kotlin
// 设置新数据
adapter.setList(newList)

// 添加数据
adapter.addData(item)

// 添加多个数据
adapter.addData(item1, item2, item3)

// 在指定位置添加
adapter.addData(position, item)

// 移除指定位置
adapter.removeAt(position)

// 移除指定 item
adapter.remove(item)

// 清空所有数据
adapter.setList(null)

// 获取数据
val data = adapter.data

// 获取指定位置数据
val item = adapter.getItem(position)
```

### 刷新和更新

```kotlin
// 刷新所有
adapter.notifyDataSetChanged()

// 刷新指定位置
adapter.notifyItemChanged(position)

// 刷新指定范围
adapter.notifyItemRangeChanged(positionStart, itemCount)

// 插入
adapter.notifyItemInserted(position)

// 移除
adapter.notifyItemRemoved(position)

// 移动
adapter.notifyItemMoved(fromPosition, toPosition)
```

### 查找

```kotlin
// 根据 position 获取 ViewHolder
val viewHolder = adapter.getViewByPosition(position, item_type)

// 获取 item 的类型
val itemType = adapter.getItemType(position)

// 获取当前显示的 item 数量
val count = adapter.itemCount
```

---

## 性能优化

### 1. 使用 DiffUtil（推荐）

```kotlin
// 使用 DiffUtil 进行差异化刷新
adapter.setDiffNewData(newList, true)

// 或者使用 DiffUtil.Callback
val diffCallback = object : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

val diffResult = DiffUtil.calculateDiff(diffCallback)
adapter.setNewDiffResult(diffResult)
```

### 2. 设置固定大小

```kotlin
// 如果 item 高度固定，设置为 true 提高性能
binding.recyclerView.setHasFixedSize(true)
```

### 3. 复用 ViewHolder

```kotlin
// BRVAH 自动处理 ViewHolder 复用
// 无需手动处理
```

### 4. 图片加载优化

```kotlin
// 使用 Glide 的适当配置
ImageLoader.with(context)
    .load(url)
    .override(200, 200)  // 指定加载尺寸
    .diskCacheStrategy(DiskCacheStrategy.ALL)  // 缓存策略
    .into(imageView)
```

---

## 完整示例：音乐列表

### 数据类
```kotlin
data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val coverUrl: String,
    val duration: Int,  // 秒
    var isPlaying: Boolean = false,
    var isLiked: Boolean = false
)
```

### 适配器
```kotlin
class MusicAdapter : BaseAdapter<Song, BaseViewHolder> {

    var currentPlayingId: String? = null

    constructor() : super(R.layout.item_music)

    override fun onBind(holder: BaseViewHolder, position: Int, item: Song) {
        // 基本信息
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_artist, item.artist)
        holder.setText(R.id.tv_duration, formatDuration(item.duration))

        // 播放状态
        val ivPlaying = holder.getView<ImageView>(R.id.iv_playing)
        if (item.id == currentPlayingId && item.isPlaying) {
            ivPlaying.visibility = View.VISIBLE
            // 添加播放动画
        } else {
            ivPlaying.visibility = View.GONE
        }

        // 喜欢状态
        val btnLike = holder.getView<ImageButton>(R.id.btn_like)
        btnLike.setImageResource(if (item.isLiked) R.drawable.ic_like_on else R.drawable.ic_like_off)

        // 加载封面
        ImageLoader.with(context)
            .load(item.coverUrl)
            .circle()
            .into(holder.getView(R.id.iv_cover))
    }

    private fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%d:%02d", minutes, secs)
    }
}
```

### 使用
```kotlin
class MusicListFragment : BaseFragment<FragmentMusicListBinding, MusicListViewModel>() {

    private lateinit var adapter: MusicAdapter
    private val songs = mutableListOf<Song>()

    override fun initView() {
        adapter = MusicAdapter()

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MusicListFragment.adapter
            setHasFixedSize(true)  // 性能优化
        }

        // item 点击
        adapter.setOnItemClickListener { _, _, position ->
            val song = songs[position]
            playSong(song, position)
        }

        // 喜欢按钮点击
        adapter.addChildClickListener(R.id.btn_like) { _, _, position ->
            val song = songs[position]
            toggleLike(song, position)
        }

        // 更多按钮点击
        adapter.addChildClickListener(R.id.btn_more) { _, _, position ->
            val song = songs[position]
            showMoreOptions(song)
        }

        // 观察数据
        viewModel.songs.observe(viewLifecycleOwner) { songList ->
            updateSongs(songList)
        }
    }

    private fun playSong(song: Song, position: Int) {
        // 更新播放状态
        val oldPlayingId = adapter.currentPlayingId
        adapter.currentPlayingId = song.id

        if (oldPlayingId != null) {
            // 刷新旧的播放项
            val oldPosition = songs.indexOfFirst { it.id == oldPlayingId }
            if (oldPosition >= 0) {
                songs[oldPosition].isPlaying = false
                adapter.notifyItemChanged(oldPosition)
            }
        }

        // 更新新的播放项
        song.isPlaying = true
        adapter.notifyItemChanged(position)

        // 播放音乐
        viewModel.playSong(song)
    }

    private fun updateSongs(newSongs: List<Song>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = songs.size
            override fun getNewListSize() = newSongs.size

            override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
                return songs[oldPos].id == newSongs[newPos].id
            }

            override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
                return songs[oldPos] == newSongs[newPos]
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        songs.clear()
        songs.addAll(newSongs)
        diffResult.dispatchUpdatesTo(adapter)
    }
}
```

---

## 总结

### 单布局使用场景
- ✅ 相同类型的数据列表（如歌曲列表、用户列表）
- ✅ 简单的列表展示

### 多布局使用场景
- ✅ 复杂的混合内容（如标题+内容+分割线）
- ✅ 不同类型的 item（如文本+图片+音频）
- ✅ 需要头部和尾部的列表

### 核心优势
1. ✅ **简单易用** - 几行代码实现复杂列表
2. ✅ **功能强大** - 支持几乎所有常用功能
3. ✅ **高性能** - 优化的 ViewHolder 复用
4. ✅ **扩展性强** - 轻松自定义和扩展
5. ✅ **维护活跃** - 持续更新和社区支持

### 参考文档
- [BRVAH 官方文档](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)
- [RecyclerView 最佳实践](https://developer.android.com/guide/topics/ui/layout/recyclerview)
