package com.example.moodymusicforandroid.ui.auth.activity

import android.content.Intent
import android.os.Bundle
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.base.BaseActivity
import com.example.moodymusicforandroid.common.eventbus.BaseEvent
import com.example.moodymusicforandroid.common.eventbus.EventBusManager
import com.example.moodymusicforandroid.common.eventbus.EventType
import com.example.moodymusicforandroid.databinding.ActivityLoginBinding
import com.example.moodymusicforandroid.ui.home.activity.MainActivity
import com.example.moodymusicforandroid.ui.auth.viewmodel.AuthViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 登录/注册页面
 * 同一个页面支持登录和注册模式切换
 */
class LoginActivity : BaseActivity<ActivityLoginBinding, AuthViewModel>() {

    companion object {
        val TAG = "LoginActivity"
    }

    private var isRegisterMode = false

    override fun getViewModelClass() = AuthViewModel::class.java
    override fun getLayoutId() = R.layout.activity_login

    override fun useEventBus(): Boolean = true

    override fun setupBindingVariables() {
        binding.viewModel = viewModel
    }

    override fun initView() {
        super.initView()
        updateModeUI()

        binding.tvToggleMode.setOnClickListener {
            isRegisterMode = !isRegisterMode
            updateModeUI()
        }

        binding.btnSubmit.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isBlank() || password.isBlank()) {
                showToast("用户名和密码不能为空")
                return@setOnClickListener
            }

            if (isRegisterMode) {
                viewModel.register(username, password)
            } else {
                viewModel.login(username, password)
            }
        }
    }

    override fun initData() {
        super.initData()
        // 观察登录成功
        viewModel.loginUser.observe(this) { user ->
            if (user != null) {
                navigateToMain()
            }
        }
        // 观察注册成功（注册后自动登录，同样跳转主页）
        viewModel.registerUser.observe(this) { user ->
            if (user != null) {
                navigateToMain()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onEventReceived(event: BaseEvent) {
        when (event.eventType) {
            EventType.AUTH_TOKEN_EXPIRED -> navigateToLogin()
            EventType.USER_LOGIN -> navigateToMain()
            else -> Unit
        }
    }

    /**
     * UI 文案随模式切换
     * 登录模式：显示"没有账号？去注册"
     * 注册模式：显示"已有账号？去登录"
     */
    private fun updateModeUI() {
        if (isRegisterMode) {
            binding.tvSubtitle.text = "注册新账号"
            binding.btnSubmit.text = "注册"
            binding.tvToggleMode.text = "已有账号？去登录"
        } else {
            binding.tvSubtitle.text = "登录"
            binding.btnSubmit.text = "登录"
            binding.tvToggleMode.text = "没有账号？去注册"
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        // 已在 LoginActivity，无需跳转，仅清空输入
        binding.etUsername.text?.clear()
        binding.etPassword.text?.clear()
    }
}
