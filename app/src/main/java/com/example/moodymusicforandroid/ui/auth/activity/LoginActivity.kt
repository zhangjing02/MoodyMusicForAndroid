package com.example.moodymusicforandroid.ui.auth.activity

import android.content.Intent
import android.view.View
import android.view.inputmethod.EditorInfo
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.base.BaseActivity
import com.example.moodymusicforandroid.common.eventbus.BaseEvent
import com.example.moodymusicforandroid.common.eventbus.EventType
import com.example.moodymusicforandroid.databinding.ActivityLoginBinding
import com.example.moodymusicforandroid.ui.auth.viewmodel.AuthViewModel
import com.example.moodymusicforandroid.ui.home.activity.MainActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LoginActivity : BaseActivity<ActivityLoginBinding, AuthViewModel>() {

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
            submitAuth()
        }

        binding.etPassword.setOnEditorActionListener { _, actionId, _ ->
            if (!isRegisterMode && actionId == EditorInfo.IME_ACTION_DONE) {
                submitAuth()
                true
            } else {
                false
            }
        }

        binding.etConfirmPassword.setOnEditorActionListener { _, actionId, _ ->
            if (isRegisterMode && actionId == EditorInfo.IME_ACTION_DONE) {
                submitAuth()
                true
            } else {
                false
            }
        }
    }

    override fun initData() {
        super.initData()

        viewModel.loginUser.observe(this) { user ->
            if (user != null) {
                navigateToMain()
            }
        }

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

    private fun submitAuth() {
        val username = binding.etUsername.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString()?.trim().orEmpty()

        if (username.isBlank() || password.isBlank()) {
            showToast("Username and password are required")
            return
        }

        if (isRegisterMode) {
            val confirmPassword = binding.etConfirmPassword.text?.toString()?.trim().orEmpty()
            if (confirmPassword.isBlank()) {
                showToast("Please confirm your password")
                return
            }
            if (password != confirmPassword) {
                showToast("Passwords do not match")
                return
            }
            viewModel.register(username, password)
        } else {
            viewModel.login(username, password)
        }
    }

    private fun updateModeUI() {
        if (isRegisterMode) {
            binding.tvSubtitle.text = "Create an account to claim your seat"
            binding.btnSubmit.text = "Register"
            binding.tvToggleMode.text = "Already have an account? Sign in"
            binding.tvModeBadge.text = "REGISTER MODE"
            binding.tilConfirmPassword.visibility = View.VISIBLE
        } else {
            binding.tvSubtitle.text = "Sign in to claim your seat"
            binding.btnSubmit.text = "Sign In"
            binding.tvToggleMode.text = "No account? Register"
            binding.tvModeBadge.text = "LOGIN MODE"
            binding.tilConfirmPassword.visibility = View.GONE
            binding.etConfirmPassword.text?.clear()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        binding.etUsername.text?.clear()
        binding.etPassword.text?.clear()
        binding.etConfirmPassword.text?.clear()
    }
}
