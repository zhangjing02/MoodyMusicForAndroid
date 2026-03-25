# ImageLoader 使用指南

`ImageLoader` 是基于 Glide 封装的图片加载工具类，位于 `com.example.moodymusicforandroid.common.image` 包中。

## 功能特性

- ✅ 加载网络图片 URL
- ✅ 加载本地资源
- ✅ 加载本地文件
- ✅ 加载 Uri
- ✅ 加载 Bitmap
- ✅ 加载 ByteArray
- ✅ 圆形图片
- ✅ 圆角矩形图片
- ✅ 自定义变换
- ✅ 占位图、错误图、回退图
- ✅ 缓存控制
- ✅ 预加载
- ✅ 清除缓存

## 基本用法

### 1. 加载网络图片

```kotlin
// 简单方式
ImageLoader.loadUrl(context, "https://example.com/image.jpg", imageView)

// 构建器方式
ImageLoader.with(context)
    .load("https://example.com/image.jpg")
    .into(imageView)
```

### 2. 加载本地资源

```kotlin
ImageLoader.loadResource(context, R.drawable.my_image, imageView)

// 或使用构建器
ImageLoader.with(context)
    .load(R.drawable.my_image)
    .into(imageView)
```

### 3. 加载本地文件

```kotlin
val file = File("/sdcard/Pictures/image.jpg")
ImageLoader.loadFile(context, file, imageView)

// 或使用构建器
ImageLoader.with(context)
    .load(file)
    .into(imageView)
```

### 4. 加载 Uri

```kotlin
val uri = Uri.parse("content://media/external/images/1")
ImageLoader.loadUri(context, uri, imageView)

// 或使用构建器
ImageLoader.with(context)
    .load(uri)
    .into(imageView)
```

## 高级用法

### 1. 圆形图片

```kotlin
// 简单方式
ImageLoader.loadUrlCircle(context, url, imageView)

// 构建器方式
ImageLoader.with(context)
    .load(url)
    .circle()
    .into(imageView)
```

### 2. 圆角矩形图片

```kotlin
// 简单方式（默认圆角 10dp）
ImageLoader.loadUrlRounded(context, url, imageView)

// 简单方式（自定义圆角）
ImageLoader.loadUrlRounded(context, url, imageView, radiusDp = 20)

// 构建器方式
ImageLoader.with(context)
    .load(url)
    .rounded(20)  // 圆角半径 20dp
    .into(imageView)
```

### 3. 设置占位图、错误图、回退图

```kotlin
ImageLoader.with(context)
    .load(url)
    .placeholder(R.drawable.loading)  // 加载中显示
    .error(R.drawable.error)          // 加载失败显示
    .fallback(R.drawable.empty)       // URL 为 null 时显示
    .into(imageView)
```

### 4. 控制缓存策略

```kotlin
ImageLoader.with(context)
    .load(url)
    .diskCacheStrategy(DiskCacheStrategy.ALL)  // 缓存所有版本
    .skipMemoryCache(false)                    // 不跳过内存缓存
    .into(imageView)
```

**缓存策略选项**：
- `DiskCacheStrategy.ALL` - 缓存所有版本（原始数据 + 解码后）
- `DiskCacheStrategy.NONE` - 不缓存到磁盘
- `DiskCacheStrategy.DATA` - 只缓存原始数据
- `DiskCacheStrategy.RESOURCE` - 只缓存解码后的数据
- `DiskCacheStrategy.AUTOMATIC` - 自动选择（默认）

### 5. 指定图片尺寸

```kotlin
ImageLoader.with(context)
    .load(url)
    .override(300, 300)  // 加载 300x300 的图片
    .into(imageView)
```

### 6. 缩略图

```kotlin
ImageLoader.with(context)
    .load(url)
    .thumbnail(0.1f)  // 先显示原图的 10%，然后加载完整图
    .into(imageView)
```

### 7. 禁用动画

```kotlin
ImageLoader.with(context)
    .load(url)
    .dontAnimate()  // 不显示淡入动画
    .into(imageView)
```

### 8. 自定义变换

```kotlin
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter

ImageLoader.with(context)
    .load(url)
    .transform(CenterCrop(), FitCenter())
    .into(imageView)
```

### 9. 预加载图片到缓存

```kotlin
ImageLoader.with(context)
    .load(url)
    .preload()  // 提前加载到缓存，稍后显示时会更快
```

## 缓存管理

### 清除内存缓存

```kotlin
ImageLoader.with(context).clearMemoryCache()
```

### 清除磁盘缓存

```kotlin
ImageLoader.with(context).clearDiskCache {
    // 清除完成回调
    showToast("缓存已清除")
}
```

### 清除所有缓存

```kotlin
ImageLoader.clearAllCache(context) {
    // 清除完成回调
    showToast("所有缓存已清除")
}
```

## 使用场景示例

### 1. 列表图片加载（带占位图）

```kotlin
ImageLoader.with(context)
    .load(song.coverUrl)
    .placeholder(R.drawable.ic_music_placeholder)
    .error(R.drawable.ic_music_error)
    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
    .into(holder.coverImageView)
```

### 2. 用户头像（圆形）

```kotlin
ImageLoader.with(context)
    .load(user.avatarUrl)
    .placeholder(R.drawable.ic_user_placeholder)
    .circle()
    .into(avatarImageView)
```

### 3. 相册封面（圆角）

```kotlin
ImageLoader.with(context)
    .load(album.coverUrl)
    .placeholder(R.drawable.ic_album_placeholder)
    .rounded(16)  // 16dp 圆角
    .into(coverImageView)
```

### 4. 大图加载（指定尺寸）

```kotlin
ImageLoader.with(context)
    .load(largeImageUrl)
    .override(1024, 1024)
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .into(largeImageView)
```

### 5. GIF 图片加载

```kotlin
ImageLoader.with(context)
    .load(gifUrl)
    .dontAnimate()  // 保持 GIF 的动画效果
    .into(gifImageView)
```

## 注意事项

1. **Context 选择**：推荐使用 `ApplicationContext` 而不是 `Activity` Context，避免内存泄漏
2. **ImageView 复用**：在 RecyclerView 中，ImageView 会被复用，不用担心内存问题
3. **取消加载**：Activity/Fragment 销毁时，Glide 会自动取消加载，无需手动处理
4. **缓存策略**：
   - 列表图片建议使用 `DiskCacheStrategy.RESOURCE`
   - 需要频繁更新的图片使用 `DiskCacheStrategy.NONE`
5. **图片尺寸**：根据 ImageView 实际大小设置 `override`，节省内存

## 性能优化建议

1. **预加载**：在列表显示前，预加载即将显示的图片
2. **缩略图**：使用 `thumbnail()` 提升用户体验
3. **缓存策略**：根据图片更新频率选择合适的缓存策略
4. **图片尺寸**：不要加载比 ImageView 大得多的图片
5. **内存缓存**：默认开启，除非有特殊需求，否则不要关闭

## 完整示例

```kotlin
class MusicAdapter : RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]

        ImageLoader.with(holder.itemView.context)
            .load(song.coverUrl)
            .placeholder(R.drawable.ic_music_placeholder)
            .error(R.drawable.ic_music_error)
            .rounded(12)
            .override(400, 400)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(holder.coverImageView)
    }

    // 预加载前几张图片
    fun preloadImages(context: Context) {
        songs.take(5).forEach { song ->
            ImageLoader.with(context)
                .load(song.coverUrl)
                .preload()
        }
    }
}
```

## 与 Glide 直接使用的对比

**之前使用 Glide**：
```kotlin
Glide.with(context)
    .load(url)
    .placeholder(R.drawable.placeholder)
    .circleCrop()
    .into(imageView)
```

**现在使用 ImageLoader**：
```kotlin
ImageLoader.with(context)
    .load(url)
    .placeholder(R.drawable.placeholder)
    .circle()
    .into(imageView)
```

**优势**：
- ✅ 统一的 API 风格
- ✅ 简化的方法调用（`circle()` vs `circleCrop()`）
- ✅ 更多的便捷方法（`loadUrl`, `loadUrlCircle`, `loadUrlRounded`）
- ✅ 自定义圆角半径（`rounded(dp)`）
- ✅ 集中管理，易于维护和扩展
