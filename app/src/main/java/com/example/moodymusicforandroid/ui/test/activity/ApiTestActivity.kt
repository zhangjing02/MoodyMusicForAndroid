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
import com.example.moodymusicforandroid.data.api.MoodyApiProvider
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
            testGetWelcomeImages()
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
     * 测试5：欢迎图片
     */
    private fun testGetWelcomeImages() {
        lifecycleScope.launch {
            try {
                showLoading(true)
                updateResult("正在获取欢迎图片...")

                val response = MoodyApiProvider.apiService.getWelcomeImages()

                Log.d(TAG, "=== 获取欢迎图片成功 ===")
                Log.d(TAG, "响应码: ${response.code}")
                Log.d(TAG, "消息: ${response.message}")
                Log.d(TAG, "数据: ${GsonBuilder().setPrettyPrinting().create().toJson(response.data)}")

                val imageCount = response.data?.size ?: 0
                updateResult("✅ 获取欢迎图片成功\n\n图片数量: $imageCount\n\n详细内容请查看 Logcat 日志")

            } catch (e: Exception) {
                Log.e(TAG, "获取欢迎图片失败", e)
                updateResult("❌ 获取欢迎图片失败\n\n错误: ${e.message}")
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
