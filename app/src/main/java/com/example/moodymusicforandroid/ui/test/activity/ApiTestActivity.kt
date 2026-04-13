package com.example.moodymusicforandroid.ui.test.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.common.preferences.PreferencesManager
import com.example.moodymusicforandroid.data.api.MoodyApiProvider
import com.example.moodymusicforandroid.data.model.LoginRequest
import com.example.moodymusicforandroid.data.model.RegisterRequest
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch

/**
 * API 接口测试 Activity
 */
class ApiTestActivity : AppCompatActivity() {

    private val TAG = "ApiTest"

    private lateinit var tvResult: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_test)

        initViews()
        setupClickListeners()

        // 显示 API 地址
        tvResult.text = "准备就绪！\n\n点击下方按钮测试各个接口"
    }

    private fun initViews() {
        tvResult = findViewById(R.id.tvResult)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupClickListeners() {
        findViewById<Button>(R.id.btnTestSkeleton).setOnClickListener {
            testGetArtists()
        }

        findViewById<Button>(R.id.btnTestSongs).setOnClickListener {
            testGetSongs()
        }

        findViewById<Button>(R.id.btnTestSearch).setOnClickListener {
            testSearch()
        }

        findViewById<Button>(R.id.btnTestStats).setOnClickListener {
            testGetStats()
        }

        findViewById<Button>(R.id.btnTestWelcome).setOnClickListener {
            updateResult("Welcome Images 接口已移除")
        }

        findViewById<Button>(R.id.btnTestLogin).setOnClickListener {
            testLogin()
        }

        findViewById<Button>(R.id.btnTestRegister).setOnClickListener {
            testRegister()
        }

        findViewById<Button>(R.id.btnTestRefresh).setOnClickListener {
            testRefreshToken()
        }

        findViewById<Button>(R.id.btnTestProfile).setOnClickListener {
            testProfile()
        }
    }

    /**
     * 测试1：获取艺人列表
     */
    private fun testGetArtists() {
        lifecycleScope.launch {
            try {
                showLoading(true)
                updateResult("正在获取艺人列表...")

                val response = MoodyApiProvider.apiService.getArtists()

                // 打印到日志
                Log.d(TAG, "=== 获取艺人列表成功 ===")
                Log.d(TAG, "响应码: ${response.code}")
                Log.d(TAG, "消息: ${response.message}")
                Log.d(TAG, "数据: ${GsonBuilder().setPrettyPrinting().create().toJson(response.data)}")

                updateResult("✅ 获取艺人列表成功\n\n艺人数量: ${response.data?.artists?.size ?: 0}\n\n详细内容请查看 Logcat 日志")

            } catch (e: Exception) {
                Log.e(TAG, "获取艺人列表失败", e)
                updateResult("❌ 获取艺人列表失败\n\n错误: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    /**
     * 测试2：获取歌曲数据
     */
    private fun testGetSongs() {
        lifecycleScope.launch {
            try {
                showLoading(true)
                updateResult("正在获取歌曲数据（周杰伦）...")

                val response = MoodyApiProvider.apiService.getSongs(artist = "周杰伦")

                Log.d(TAG, "=== 获取歌曲数据成功 ===")
                Log.d(TAG, "响应码: ${response.code}")
                Log.d(TAG, "消息: ${response.message}")
                Log.d(TAG, "数据: ${GsonBuilder().setPrettyPrinting().create().toJson(response.data)}")

                val artistCount = response.data?.size ?: 0
                updateResult("✅ 获取歌曲数据成功\n\n艺人数量: $artistCount\n\n详细内容请查看 Logcat 日志")

            } catch (e: Exception) {
                Log.e(TAG, "获取歌曲数据失败", e)
                updateResult("❌ 获取歌曲数据失败\n\n错误: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    /**
     * 测试3：搜索功能
     */
    private fun testSearch() {
        lifecycleScope.launch {
            try {
                showLoading(true)
                updateResult("正在搜索'周杰伦'...")

                val response = MoodyApiProvider.apiService.search("周杰伦")

                Log.d(TAG, "=== 搜索成功 ===")
                Log.d(TAG, "响应码: ${response.code}")
                Log.d(TAG, "消息: ${response.message}")
                Log.d(TAG, "数据: ${GsonBuilder().setPrettyPrinting().create().toJson(response.data)}")

                val result = response.data
                val artistCount = result?.artists?.size ?: 0
                val albumCount = result?.albums?.size ?: 0
                val songCount = result?.songs?.size ?: 0

                updateResult("✅ 搜索成功\n\n找到:\n- 艺人: $artistCount\n- 专辑: $albumCount\n- 歌曲: $songCount\n\n详细内容请查看 Logcat 日志")

            } catch (e: Exception) {
                Log.e(TAG, "搜索失败", e)
                updateResult("❌ 搜索失败\n\n错误: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    /**
     * 测试4：系统统计
     */
    private fun testGetStats() {
        lifecycleScope.launch {
            try {
                showLoading(true)
                updateResult("正在获取系统统计...")

                val response = MoodyApiProvider.apiService.getSystemStats()

                Log.d(TAG, "=== 系统统计成功 ===")
                Log.d(TAG, "响应码: ${response.code}")
                Log.d(TAG, "消息: ${response.message}")
                Log.d(TAG, "数据: ${GsonBuilder().setPrettyPrinting().create().toJson(response.data)}")

                val stats = response.data
                updateResult("✅ 系统统计成功\n\n统计数据:\n- 艺人: ${stats?.artists ?: 0}\n- 专辑: ${stats?.albums ?: 0}\n- 歌曲: ${stats?.tracks ?: 0}")

            } catch (e: Exception) {
                Log.e(TAG, "获取系统统计失败", e)
                updateResult("❌ 获取系统统计失败\n\n错误: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    /**
     * 测试6：登录
     */
    private fun testLogin() {
        lifecycleScope.launch {
            try {
                showLoading(true)
                updateResult("正在尝试登录...")

                val response = MoodyApiProvider.apiService.login(LoginRequest("test_user_001", "123456"))

                Log.d(TAG, "=== 登录成功 ===")
                Log.d(TAG, "响应码: ${response.code}")
                Log.d(TAG, "数据: ${GsonBuilder().setPrettyPrinting().create().toJson(response.data)}")

                response.data?.let { user ->
                    PreferencesManager.saveUserToken(user.token ?: "")
                    user.refreshToken?.let { PreferencesManager.saveUserRefreshToken(it) }
                    PreferencesManager.saveUserInfo(user.userId.toString(), user.username)
                }

                updateResult("✅ 登录成功\n\n用户: ${response.data?.username}\nToken 已保存")

            } catch (e: Exception) {
                Log.e(TAG, "登录失败", e)
                updateResult("❌ 登录失败\n\n错误: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    /**
     * 测试7：注册
     */
    private fun testRegister() {
        lifecycleScope.launch {
            try {
                showLoading(true)
                val testName = "TestUser_${System.currentTimeMillis() % 1000}"
                updateResult("正在尝试注册: $testName ...")

                val response = MoodyApiProvider.apiService.register(
                    RegisterRequest(testName, "test@example.com", "123456")
                )

                Log.d(TAG, "=== 注册成功 ===")
                Log.d(TAG, "数据: ${GsonBuilder().setPrettyPrinting().create().toJson(response.data)}")

                response.data?.let { user ->
                    PreferencesManager.saveUserToken(user.token ?: "")
                    user.refreshToken?.let { PreferencesManager.saveUserRefreshToken(it) }
                }

                updateResult("✅ 注册成功\n\n用户: ${response.data?.username}\nToken 已保存")

            } catch (e: Exception) {
                Log.e(TAG, "注册失败", e)
                updateResult("❌ 注册失败\n\n错误: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    /**
     * 测试8：刷新 Token
     */
    private fun testRefreshToken() {
        lifecycleScope.launch {
            try {
                showLoading(true)
                updateResult("正在测试自动刷新逻辑...")

                // 故意破坏 Token 来触发 401（如果接口支持的话）
                // 或者是直接调用服务接口
                val refreshToken = PreferencesManager.getUserRefreshToken()
                if (refreshToken.isNullOrEmpty()) {
                    updateResult("❌ 失败: 没有找到已保存的 Refresh Token，请先登录")
                    return@launch
                }

                val response = MoodyApiProvider.apiService.refreshToken(
                    com.example.moodymusicforandroid.data.model.RefreshTokenRequest(refreshToken)
                )

                Log.d(TAG, "=== 刷新成功 ===")
                Log.d(TAG, "新数据: ${GsonBuilder().setPrettyPrinting().create().toJson(response.data)}")

                response.data?.let { user ->
                    PreferencesManager.saveUserToken(user.token ?: "")
                    user.refreshToken?.let { PreferencesManager.saveUserRefreshToken(it) }
                }

                updateResult("✅ Token 刷新成功\n\n新 Token 已保存")

            } catch (e: Exception) {
                Log.e(TAG, "刷新失败", e)
                updateResult("❌ 刷新失败\n\n错误: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    /**
     * 测试9：获取个人信息 (验证已登录请求)
     */
    private fun testProfile() {
        lifecycleScope.launch {
            try {
                showLoading(true)
                updateResult("正在获取个人信息...")

                val response = MoodyApiProvider.apiService.getProfile()

                Log.d(TAG, "=== 获取个人信息成功 ===")
                Log.d(TAG, "数据: ${GsonBuilder().setPrettyPrinting().create().toJson(response.data)}")

                updateResult("✅ 获取个人信息成功\n\n用户: ${response.data?.username}\n昵称: ${response.data?.nickname}")

            } catch (e: Exception) {
                Log.e(TAG, "获取个人信息失败", e)
                updateResult("❌ 获取个人信息失败 (未登录或 Token 失效)\n\n错误: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun updateResult(text: String) {
        tvResult.text = text
        Log.d(TAG, "===== 界面更新 =====")
        Log.d(TAG, text)
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
    }
}
