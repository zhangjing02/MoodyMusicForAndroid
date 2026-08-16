package com.example.moodymusicforandroid.ui.auth.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.moodymusicforandroid.MoodyMusicApplication
import com.example.moodymusicforandroid.common.eventbus.BaseEvent
import com.example.moodymusicforandroid.common.eventbus.EventBusManager
import com.example.moodymusicforandroid.common.eventbus.EventType
import com.example.moodymusicforandroid.common.utils.ThemeManager
import com.example.moodymusicforandroid.ui.auth.viewmodel.AuthViewModel
import com.example.moodymusicforandroid.ui.home.activity.MainActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LoginActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(MoodyMusicApplication.currentThemeResId)
        super.onCreate(savedInstanceState)
        
        EventBusManager.register(this)
        ThemeManager.initTheme(this)
        
        if (intent.getBooleanExtra("KICKED_OUT", false)) {
            Toast.makeText(this, "您的账号已在其他设备登录，请重新登录", Toast.LENGTH_SHORT).show()
        }

        setContent {
            var isRegisterMode by remember { mutableStateOf(false) }
            var username by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var confirmPassword by remember { mutableStateOf("") }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isRegisterMode) "REGISTER MODE" else "LOGIN MODE",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isRegisterMode) "Create an account to claim your seat" else "Sign in to claim your seat",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                        
                        if (isRegisterMode) {
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirm Password") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                if (username.isBlank() || password.isBlank()) {
                                    Toast.makeText(this@LoginActivity, "Username and password are required", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (isRegisterMode) {
                                    if (confirmPassword.isBlank() || password != confirmPassword) {
                                        Toast.makeText(this@LoginActivity, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    viewModel.register(username, password)
                                } else {
                                    viewModel.login(username, password)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isRegisterMode) "Register" else "Sign In")
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
                            Text(if (isRegisterMode) "Already have an account? Sign in" else "No account? Register")
                        }
                    }
                }
            }
        }
        
        viewModel.loginUser.observe(this) { user ->
            if (user != null) navigateToMain()
        }
        viewModel.registerUser.observe(this) { user ->
            if (user != null) navigateToMain()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventReceived(event: BaseEvent) {
        when (event.eventType) {
            EventType.AUTH_TOKEN_EXPIRED -> {
                if (event.eventData == "KICKED_OUT") {
                    Toast.makeText(this, "您的账号已在其他设备登录，请重新登录", Toast.LENGTH_SHORT).show()
                }
            }
            EventType.USER_LOGIN -> navigateToMain()
            else -> Unit
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusManager.unregister(this)
    }
}
