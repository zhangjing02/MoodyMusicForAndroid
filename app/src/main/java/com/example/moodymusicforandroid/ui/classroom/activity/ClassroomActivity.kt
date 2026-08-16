package com.example.moodymusicforandroid.ui.classroom.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import cn.jpush.android.api.JPushInterface
import com.example.moodymusicforandroid.MoodyMusicApplication
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.common.utils.ThemeManager
import com.example.moodymusicforandroid.data.model.RosterItem
import com.example.moodymusicforandroid.data.model.SecurityQuestion
import com.example.moodymusicforandroid.receiver.JPushReceiver
import com.example.moodymusicforandroid.ui.classroom.viewmodel.ClassroomViewModel

class ClassroomActivity : AppCompatActivity() {

    private val viewModel: ClassroomViewModel by viewModels()
    private var currentClassId: Int? = null

    private val refreshReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.fetchRoster()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(MoodyMusicApplication.currentThemeResId)
        super.onCreate(savedInstanceState)
        ThemeManager.initTheme(this)

        LocalBroadcastManager.getInstance(this).registerReceiver(
            refreshReceiver,
            IntentFilter(JPushReceiver.BROADCAST_ACTION_REFRESH_ROSTER)
        )

        viewModel.fetchRoster()

        setContent {
            val classroomData by viewModel.classroomData.observeAsState()
            val verifyResult by viewModel.verifyResult.observeAsState()
            val claimResult by viewModel.claimResult.observeAsState()
            val loginResult by viewModel.loginResult.observeAsState()
            val errorMsg by viewModel.errorMessage.observeAsState()

            var showLoginDialog by remember { mutableStateOf<RosterItem?>(null) }
            var showVerifyDialog by remember { mutableStateOf<RosterItem?>(null) }
            var showFinalizeDialog by remember { mutableStateOf<String?>(null) }

            // Handle Navigation & Side Effects
            LaunchedEffect(classroomData?.classId) {
                classroomData?.classId?.let { newId ->
                    if (currentClassId != newId) {
                        currentClassId?.let { oldId ->
                            JPushInterface.deleteTags(this@ClassroomActivity, 101, setOf("classroom_$oldId"))
                        }
                        currentClassId = newId
                        JPushInterface.setTags(this@ClassroomActivity, 102, setOf("classroom_$newId"))
                    }
                }
            }

            LaunchedEffect(verifyResult) {
                verifyResult?.let {
                    val claimToken = it.claimToken.trim()
                    if (claimToken.isBlank()) {
                        Toast.makeText(this@ClassroomActivity, "验证通过但未拿到 claim token，请重试", Toast.LENGTH_SHORT).show()
                    } else {
                        showVerifyDialog = null
                        showFinalizeDialog = claimToken
                    }
                }
            }

            LaunchedEffect(claimResult) {
                claimResult?.let {
                    showFinalizeDialog = null
                    Toast.makeText(this@ClassroomActivity, "认领成功，${it.username}，欢迎回到教室", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            LaunchedEffect(loginResult) {
                loginResult?.let {
                    Toast.makeText(this@ClassroomActivity, "登录成功", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            LaunchedEffect(errorMsg) {
                if (!errorMsg.isNullOrEmpty()) {
                    Toast.makeText(this@ClassroomActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }

            MaterialTheme {
                Scaffold(
                    topBar = {
                        @OptIn(ExperimentalMaterial3Api::class)
                        TopAppBar(
                            title = { Text("同学录 · 青春到此一游") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(painterResource(R.drawable.ic_close), contentDescription = "Back")
                                }
                            }
                        )
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        val claimedCount = classroomData?.roster?.count { it.isClaimed == 1 } ?: 0
                        val totalCount = classroomData?.roster?.size ?: 0

                        // Blackboard Section
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF2C3E33))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "青春纪念册", // Equivalent to blackboard title
                                    color = Color.White,
                                    fontSize = 22.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "快去认领你的专属座位吧...",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(text = "$claimedCount", color = Color(0xFFFFD700), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text(text = " 已认领", color = Color(0xFFFFD700), fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(text = "$totalCount", color = Color(0xFFFFD700), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text(text = " 总座位", color = Color(0xFFFFD700), fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))
                                }
                            }
                        }

                        // Wooden Platform
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "这里是老师的讲台，同学们不要乱丢纸团哦",
                                color = Color.Gray.copy(alpha = 0.52f),
                                fontSize = 9.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFD4A373)), // Wooden color
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "讲 台",
                                    color = Color(0xFF5C4033),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Seats Grid
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .horizontalScroll(rememberScrollState())
                        ) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(8),
                                modifier = Modifier
                                    .width(800.dp) // Match original width for 8 seats to fit nicely
                                    .padding(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                items(classroomData?.roster ?: emptyList()) { seat ->
                                    val isClaimed = seat.isClaimed == 1
                                    com.example.moodymusicforandroid.ui.classroom.components.ClassroomSeat(
                                        seat = seat,
                                        onClick = {
                                            if (isClaimed) showLoginDialog = seat
                                            else showVerifyDialog = seat
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Dialogs
                    showLoginDialog?.let { seat ->
                        var password by remember { mutableStateOf("") }
                        AlertDialog(
                            onDismissRequest = { showLoginDialog = null },
                            title = { Text("${seat.realName}，欢迎回来") },
                            text = {
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text("请输入密码") },
                                    visualTransformation = PasswordVisualTransformation()
                                )
                            },
                            confirmButton = {
                                Button(onClick = {
                                    if (password.isNotEmpty()) {
                                        val username = "${seat.yearCode}.${seat.seatCode}${seat.realName}"
                                        viewModel.login(username, password)
                                    }
                                }) { Text("登录") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showLoginDialog = null }) { Text("取消") }
                            }
                        )
                    }

                    showVerifyDialog?.let { seat ->
                        val questions = classroomData?.securityQuestions?.take(3) ?: emptyList()
                        if (questions.size < 3) {
                            LaunchedEffect(Unit) {
                                Toast.makeText(this@ClassroomActivity, "请先在后端配置 3 个安全问题", Toast.LENGTH_SHORT).show()
                                showVerifyDialog = null
                            }
                        } else {
                            var q1 by remember { mutableStateOf("") }
                            var q2 by remember { mutableStateOf("") }
                            var q3 by remember { mutableStateOf("") }
                            
                            AlertDialog(
                                onDismissRequest = { showVerifyDialog = null },
                                title = { Text("认领座位: ${seat.realName}") },
                                text = {
                                    Column {
                                        OutlinedTextField(value = q1, onValueChange = { q1 = it }, label = { Text(questions[0].question) })
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(value = q2, onValueChange = { q2 = it }, label = { Text(questions[1].question) })
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(value = q3, onValueChange = { q3 = it }, label = { Text(questions[2].question) })
                                    }
                                },
                                confirmButton = {
                                    Button(onClick = {
                                        if (q1.isBlank() || q2.isBlank() || q3.isBlank()) {
                                            Toast.makeText(this@ClassroomActivity, "请填写所有答案", Toast.LENGTH_SHORT).show()
                                        } else {
                                            viewModel.verifyClaim(seat.id, listOf(q1, q2, q3))
                                        }
                                    }) { Text("下一步") }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showVerifyDialog = null }) { Text("取消") }
                                }
                            )
                        }
                    }

                    showFinalizeDialog?.let { token ->
                        var password by remember { mutableStateOf("") }
                        AlertDialog(
                            onDismissRequest = { showFinalizeDialog = null },
                            title = { Text("验证通过") },
                            text = {
                                Column {
                                    Text("请设置一个新密码，邮箱可稍后绑定。")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = password,
                                        onValueChange = { password = it },
                                        label = { Text("新密码") },
                                        visualTransformation = PasswordVisualTransformation()
                                    )
                                }
                            },
                            confirmButton = {
                                Button(onClick = {
                                    if (password.length >= 6) {
                                        viewModel.finalizeClaim(token, password)
                                    } else {
                                        Toast.makeText(this@ClassroomActivity, "密码至少 6 位", Toast.LENGTH_SHORT).show()
                                    }
                                }) { Text("完成认领") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showFinalizeDialog = null }) { Text("取消") }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(refreshReceiver)
        currentClassId?.let { id ->
            JPushInterface.deleteTags(this, 100, setOf("classroom_$id"))
        }
        super.onDestroy()
    }
}