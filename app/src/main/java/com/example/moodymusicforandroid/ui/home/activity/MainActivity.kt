package com.example.moodymusicforandroid.ui.home.activity

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.RadioGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import android.content.Intent
import com.example.moodymusicforandroid.MoodyMusicApplication
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.base.BaseActivity
import com.example.moodymusicforandroid.common.eventbus.BaseEvent
import com.example.moodymusicforandroid.common.eventbus.EventBusManager
import com.example.moodymusicforandroid.common.eventbus.EventType
import com.example.moodymusicforandroid.common.preferences.PreferencesManager
import com.example.moodymusicforandroid.common.utils.FontManager
import com.example.moodymusicforandroid.common.utils.ThemeManager
import com.example.moodymusicforandroid.databinding.ActivityMainBinding
import com.example.moodymusicforandroid.databinding.NavigationDrawerBinding
import com.example.moodymusicforandroid.ui.classroom.activity.ClassroomActivity
import com.example.moodymusicforandroid.ui.home.DiscoverFragment
import com.example.moodymusicforandroid.ui.home.HomeFragment
import com.example.moodymusicforandroid.ui.home.LibraryFragment
import com.example.moodymusicforandroid.ui.home.viewmodel.MainViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 主Activity
 * 使用泛型基类，简化代码
 * 包含 ViewPager2 和底部导航
 */
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private val TAG = "MainActivity"

    private lateinit var viewPagerAdapter: MainViewPagerAdapter
    private lateinit var drawerBinding: NavigationDrawerBinding

    // 在 onCreate 之前设置主题
    override fun onCreate(savedInstanceState: Bundle?) {
        // 在调用 super.onCreate() 之前应用组合主题（字体 + 颜色）
        setTheme(MoodyMusicApplication.currentThemeResId)
        super.onCreate(savedInstanceState)
    }

    override fun getViewModelClass() = MainViewModel::class.java

    override fun getLayoutId() = R.layout.activity_main

    override fun initView() {
        super.initView()
        setupImmersiveStatusBar() // 设置沉浸式状态栏
        setupViewPager()
        setupBottomNavigation()
        setupDrawer()
        setupThemeAndFont()
        updateUserInfoUI() // 初始化用户信息显示
    }

    /**
     * 设置沉浸式状态栏
     */
    private fun setupImmersiveStatusBar() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
        window.setFlags(
            WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
            WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
        )

        // 设置状态栏为半透明背景色，匹配app主题
        window.statusBarColor = android.graphics.Color.parseColor("#CCF8FAF8") // 80% 透明度

        // 设置状态栏图标为深色（适合浅色背景）
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
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

    private fun setupDrawer() {
        // 获取抽屉布局的绑定
        drawerBinding = binding.navigationDrawer as NavigationDrawerBinding

        // 菜单图标点击打开抽屉
        binding.ivMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        // 关闭按钮点击关闭抽屉
        drawerBinding.ivCloseDrawer.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        // 教室认证点击
        drawerBinding.btnClassroomAuth.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, ClassroomActivity::class.java))
        }

        // 注销点击
        drawerBinding.btnLogout.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            PreferencesManager.clearUserInfo()
            EventBusManager.post(EventType.USER_LOGOUT, "用户退出登录")
            showToast("已退出当前认证")
        }

        // 点击抽屉外部区域关闭抽屉
        binding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    private fun setupThemeAndFont() {
        // 初始化主题
        ThemeManager.initTheme(this)

        // 主题切换监听 - 直接响应点击，不检查 isChecked
        drawerBinding.rbThemeDefault.setOnClickListener {
            ThemeManager.setTheme(this, ThemeManager.ThemeMode.DEFAULT)
            (application as MoodyMusicApplication).updateTheme()
            showToast("已切换至默认绿主题")
            recreate() // 重新创建Activity以应用新颜色主题
        }

        drawerBinding.rbThemeOcean.setOnClickListener {
            ThemeManager.setTheme(this, ThemeManager.ThemeMode.OCEAN)
            (application as MoodyMusicApplication).updateTheme()
            showToast("已切换至海洋蓝主题")
            recreate()
        }

        drawerBinding.rbThemeSunset.setOnClickListener {
            ThemeManager.setTheme(this, ThemeManager.ThemeMode.SUNSET)
            (application as MoodyMusicApplication).updateTheme()
            showToast("已切换至日落橙主题")
            recreate()
        }

        drawerBinding.rbThemeNight.setOnClickListener {
            ThemeManager.setTheme(this, ThemeManager.ThemeMode.NIGHT)
            (application as MoodyMusicApplication).updateTheme()
            showToast("已切换至暗夜紫主题")
            recreate()
        }

        // 字体切换监听 - 直接响应点击，不改变主题
        drawerBinding.rbFontHandwriting.setOnClickListener {
            Log.d(TAG, "Font clicked: HANDWRITING")
            FontManager.setFontStyle(this, FontManager.FontStyle.HANDWRITING)
            showToast("字体已切换为：手写飘逸")
            recreate() // 重新创建Activity，应用新字体
        }

        drawerBinding.rbFontModern.setOnClickListener {
            Log.d(TAG, "Font clicked: MODERN")
            FontManager.setFontStyle(this, FontManager.FontStyle.MODERN)
            showToast("字体已切换为：现代轻盈")
            recreate()
        }

        drawerBinding.rbFontElegant.setOnClickListener {
            Log.d(TAG, "Font clicked: ELEGANT")
            FontManager.setFontStyle(this, FontManager.FontStyle.ELEGANT)
            showToast("字体已切换为：清秀文艺")
            recreate()
        }

        // 设置当前选中的主题和字体
        val currentTheme = ThemeManager.getTheme(this)
        when (currentTheme) {
            ThemeManager.ThemeMode.DEFAULT -> drawerBinding.rbThemeDefault.isChecked = true
            ThemeManager.ThemeMode.OCEAN -> drawerBinding.rbThemeOcean.isChecked = true
            ThemeManager.ThemeMode.SUNSET -> drawerBinding.rbThemeSunset.isChecked = true
            ThemeManager.ThemeMode.NIGHT -> drawerBinding.rbThemeNight.isChecked = true
        }

        val currentFont = FontManager.getFontStyle(this)
        when (currentFont) {
            FontManager.FontStyle.HANDWRITING -> drawerBinding.rbFontHandwriting.isChecked = true
            FontManager.FontStyle.MODERN -> drawerBinding.rbFontModern.isChecked = true
            FontManager.FontStyle.ELEGANT -> drawerBinding.rbFontElegant.isChecked = true
        }
    }

    override fun useEventBus() = true

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onEventReceived(event: BaseEvent) {
        super.onEventReceived(event)
        when (event.eventType) {
            EventType.USER_LOGIN, EventType.USER_LOGOUT -> {
                updateUserInfoUI()
            }
        }
    }

    /**
     * 更新侧边栏用户信息
     */
    private fun updateUserInfoUI() {
        if (PreferencesManager.isLoggedIn()) {
            val userName = PreferencesManager.getUserName() ?: "同学"
            drawerBinding.tvDrawerSubtitle.text = "欢迎回来，$userName"
            drawerBinding.btnClassroomAuth.text = "已认证"
            // 已认证状态下禁用认证按钮，显示注销按钮
            drawerBinding.btnClassroomAuth.isEnabled = false
            drawerBinding.btnLogout.visibility = View.VISIBLE
        } else {
            drawerBinding.tvDrawerSubtitle.text = "听，风吹过的声音"
            drawerBinding.btnClassroomAuth.text = getString(R.string.classroom_auth)
            drawerBinding.btnClassroomAuth.isEnabled = true
            drawerBinding.btnLogout.visibility = View.GONE
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
        // 观察头像点击跳转认证页
        binding.cvAvatar.setOnClickListener {
            startActivity(Intent(this, ClassroomActivity::class.java))
        }
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
