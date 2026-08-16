package com.example.moodymusicforandroid.ui.test.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.moodymusicforandroid.MoodyMusicApplication
import com.example.moodymusicforandroid.common.preferences.PreferencesManager
import com.example.moodymusicforandroid.common.utils.ThemeManager
import com.example.moodymusicforandroid.data.api.MoodyApiProvider
import com.example.moodymusicforandroid.data.model.LoginRequest
import com.example.moodymusicforandroid.data.model.RegisterRequest
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch

class ApiTestActivity : AppCompatActivity() {
    private val TAG = "ApiTest"

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(MoodyMusicApplication.currentThemeResId)
        super.onCreate(savedInstanceState)
        ThemeManager.initTheme(this)

        setContent {
            var resultText by remember { mutableStateOf("准备就绪！\n\n点击下方按钮测试各个接口") }
            var isLoading by remember { mutableStateOf(false) }

            MaterialTheme {
                Scaffold(
                    topBar = {
                        @OptIn(ExperimentalMaterial3Api::class)
                        TopAppBar(title = { Text("API 测试") })
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = resultText,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .verticalScroll(rememberScrollState())
                                )
                                if (isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                }
                            }
                        }

                        val buttons = listOf(
                            "测试: 艺人列表" to { testGetArtists { resultText = it; isLoading = false } },
                            "测试: 歌曲(周杰伦)" to { testGetSongs { resultText = it; isLoading = false } },
                            "测试: 搜索" to { testSearch { resultText = it; isLoading = false } },
                            "测试: 系统统计" to { testGetStats { resultText = it; isLoading = false } },
                            "测试: 登录" to { testLogin { resultText = it; isLoading = false } },
                            "测试: 注册" to { testRegister { resultText = it; isLoading = false } },
                            "测试: 刷新 Token" to { testRefreshToken { resultText = it; isLoading = false } },
                            "测试: 个人信息" to { testProfile { resultText = it; isLoading = false } }
                        )

                        buttons.forEach { (text, action) ->
                            Button(
                                onClick = {
                                    isLoading = true
                                    resultText = "正在请求..."
                                    action()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun testGetArtists(onResult: (String) -> Unit) {
        lifecycleScope.launch {
            try {
                val response = MoodyApiProvider.apiService.getArtists()
                onResult("✅ 获取艺人列表成功\n\n艺人数量: ${response.data?.artists?.size ?: 0}")
            } catch (e: Exception) {
                onResult("❌ 获取艺人列表失败\n\n错误: ${e.message}")
            }
        }
    }

    private fun testGetSongs(onResult: (String) -> Unit) {
        lifecycleScope.launch {
            try {
                val response = MoodyApiProvider.apiService.getSongs(artist = "周杰伦")
                onResult("✅ 获取歌曲数据成功\n\n艺人数量: ${response.data?.size ?: 0}")
            } catch (e: Exception) {
                onResult("❌ 获取歌曲数据失败\n\n错误: ${e.message}")
            }
        }
    }

    private fun testSearch(onResult: (String) -> Unit) {
        lifecycleScope.launch {
            try {
                val response = MoodyApiProvider.apiService.search("周杰伦")
                val result = response.data
                onResult("✅ 搜索成功\n\n找到:\n- 艺人: ${result?.artists?.size ?: 0}\n- 专辑: ${result?.albums?.size ?: 0}\n- 歌曲: ${result?.songs?.size ?: 0}")
            } catch (e: Exception) {
                onResult("❌ 搜索失败\n\n错误: ${e.message}")
            }
        }
    }

    private fun testGetStats(onResult: (String) -> Unit) {
        lifecycleScope.launch {
            try {
                val response = MoodyApiProvider.apiService.getSystemStats()
                val stats = response.data
                onResult("✅ 系统统计成功\n\n统计数据:\n- 艺人: ${stats?.artists ?: 0}\n- 专辑: ${stats?.albums ?: 0}\n- 歌曲: ${stats?.tracks ?: 0}")
            } catch (e: Exception) {
                onResult("❌ 获取系统统计失败\n\n错误: ${e.message}")
            }
        }
    }

    private fun testLogin(onResult: (String) -> Unit) {
        lifecycleScope.launch {
            try {
                val response = MoodyApiProvider.apiService.login(LoginRequest("test_user_001", "123456"))
                response.data?.let { loginData ->
                    PreferencesManager.saveUserToken(loginData.token ?: "")
                    loginData.refreshToken?.let { PreferencesManager.saveUserRefreshToken(it) }
                    loginData.user?.let { user -> PreferencesManager.saveUserInfo(user.userId.toString(), user.username) }
                }
                onResult("✅ 登录成功\n\n用户: ${response.data?.user?.username}\nToken 已保存")
            } catch (e: Exception) {
                onResult("❌ 登录失败\n\n错误: ${e.message}")
            }
        }
    }

    private fun testRegister(onResult: (String) -> Unit) {
        lifecycleScope.launch {
            try {
                val testName = "TestUser_${System.currentTimeMillis() % 1000}"
                val response = MoodyApiProvider.apiService.register(RegisterRequest(testName, "test@example.com", "123456"))
                response.data?.let { user ->
                    PreferencesManager.saveUserToken(user.token ?: "")
                    user.refreshToken?.let { PreferencesManager.saveUserRefreshToken(it) }
                }
                onResult("✅ 注册成功\n\n用户: ${response.data?.username}\nToken 已保存")
            } catch (e: Exception) {
                onResult("❌ 注册失败\n\n错误: ${e.message}")
            }
        }
    }

    private fun testRefreshToken(onResult: (String) -> Unit) {
        lifecycleScope.launch {
            try {
                val refreshToken = PreferencesManager.getUserRefreshToken()
                if (refreshToken.isNullOrEmpty()) {
                    onResult("❌ 失败: 没有找到已保存的 Refresh Token，请先登录")
                    return@launch
                }
                val response = MoodyApiProvider.apiService.refreshToken(
                    com.example.moodymusicforandroid.data.model.RefreshTokenRequest(refreshToken)
                )
                response.data?.let { user ->
                    PreferencesManager.saveUserToken(user.token ?: "")
                    user.refreshToken?.let { PreferencesManager.saveUserRefreshToken(it) }
                }
                onResult("✅ Token 刷新成功\n\n新 Token 已保存")
            } catch (e: Exception) {
                onResult("❌ 刷新失败\n\n错误: ${e.message}")
            }
        }
    }

    private fun testProfile(onResult: (String) -> Unit) {
        lifecycleScope.launch {
            try {
                val response = MoodyApiProvider.apiService.getProfile()
                onResult("✅ 获取个人信息成功\n\n用户: ${response.data?.username}\n昵称: ${response.data?.nickname}")
            } catch (e: Exception) {
                onResult("❌ 获取个人信息失败 (未登录或 Token 失效)\n\n错误: ${e.message}")
            }
        }
    }
}
