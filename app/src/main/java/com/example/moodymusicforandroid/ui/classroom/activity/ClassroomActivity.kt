package com.example.moodymusicforandroid.ui.classroom.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.base.BaseActivity
import com.example.moodymusicforandroid.data.model.RosterItem
import com.example.moodymusicforandroid.databinding.ActivityClassroomBinding
import com.example.moodymusicforandroid.ui.classroom.adapter.SeatAdapter
import com.example.moodymusicforandroid.ui.classroom.viewmodel.ClassroomViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ClassroomActivity : BaseActivity<ActivityClassroomBinding, ClassroomViewModel>() {

    private lateinit var adapter: SeatAdapter

    override fun getViewModelClass() = ClassroomViewModel::class.java

    override fun getLayoutId() = R.layout.activity_classroom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        observeViewModel()
        viewModel.fetchRoster()
    }

    private fun setupUI() {
        adapter = SeatAdapter { seat ->
            handleSeatClick(seat)
        }

        // Use Grid with 8 columns to match the "wide classroom" look
        binding.rvSeats.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 8)
        binding.rvSeats.adapter = adapter
        
        // Remove PagerSnapHelper to allow smooth free scanning as user requested

        binding.rvSeats.layoutAnimation = android.view.animation.AnimationUtils.loadLayoutAnimation(
            this,
            R.anim.anim_layout_fall_down
        )

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.classroomData.observe(this) { data ->
            adapter.submitList(data.roster)
            
            // Update Blackboard Context
            binding.tvBlackboardTitle.text = "同学录  •  青春到此一游"
            binding.tvBlackboardHint.text = "点击你的姓名认领座位"
            
            // Dynamic Count Calculation
            val claimedCount = data.roster.count { it.isClaimed == 1 }
            binding.tvClaimedCount.text = claimedCount.toString()
            binding.tvTotalCount.text = data.roster.size.toString()
            
            binding.rvSeats.scheduleLayoutAnimation()
            
            // Initial Horizontal Center: Move the lens to the middle of the room
            binding.hsvClassroom.post {
                val scrollWidth = binding.rvSeats.width
                val screenWidth = resources.displayMetrics.widthPixels
                if (scrollWidth > screenWidth) {
                    binding.hsvClassroom.scrollTo((scrollWidth - screenWidth) / 2, 0)
                }
            }
        }

        viewModel.verifyResult.observe(this) { response ->
            // 获取到 Token 后，进行最后一步：设置密码
            showClaimFinalizeDialog(response.claimToken)
        }

        viewModel.claimResult.observe(this) { user ->
            Toast.makeText(this, "认证成功！${user.username}，欢迎回到教室", Toast.LENGTH_SHORT)
                .show()
            finish()
        }

        viewModel.loginResult.observe(this) { user ->
            Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show()
            finish()
        }

        viewModel.errorMessage.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(this, "Oops: $msg", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleSeatClick(seat: RosterItem) {
        if (seat.isClaimed == 1) {
            showLoginDialog(seat.realName)
        } else {
            showClaimVerifyDialog(seat)
        }
    }

    /**
     * 已认领座位：使用实名登录
     */
    private fun showLoginDialog(name: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input_one_field, null)
        val etInput = dialogView.findViewById<EditText>(R.id.etInput)
        etInput.hint = "请输入密码"

        MaterialAlertDialogBuilder(this)
            .setTitle("$name，欢迎回来")
            .setView(dialogView)
            .setPositiveButton("登录") { _, _ ->
                val password = etInput.text.toString()
                if (password.isNotEmpty()) {
                    viewModel.login(name, password)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    /**
     * 未认领座位：输入安全问题的答案
     */
    private fun showClaimVerifyDialog(seat: RosterItem) {
        // v15.0 中安全问题在 RosterResponse 中返回，ViewModel 需要存储或 Activity 直接取
        val questions = viewModel.classroomData.value?.securityQuestions ?: emptyList()
        if (questions.isEmpty()) {
            Toast.makeText(this, "该座位暂未设置验证问题", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input_one_field, null)
        val etInput = dialogView.findViewById<EditText>(R.id.etInput)
        // 简单处理：展示第一个问题
        etInput.hint = questions[0].question

        MaterialAlertDialogBuilder(this)
            .setTitle("认领座位: ${seat.realName}")
            .setView(dialogView)
            .setPositiveButton("下一步") { _, _ ->
                val answer = etInput.text.toString()
                if (answer.isNotEmpty()) {
                    viewModel.verifyClaim(seat.id, listOf(answer))
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    /**
     * 最后一步：设置新密码
     */
    private fun showClaimFinalizeDialog(claimToken: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input_one_field, null)
        val etPassword = dialogView.findViewById<EditText>(R.id.etInput)
        etPassword.hint = "设置你的新密码"

        MaterialAlertDialogBuilder(this)
            .setTitle("验证通过")
            .setMessage("请设置一个新密码。此后你可以使用实名+密码直接进入教室。")
            .setView(dialogView)
            .setPositiveButton("确定") { _, _ ->
                val password = etPassword.text.toString()
                if (password.isNotEmpty()) {
                    viewModel.finalizeClaim(claimToken, password)
                }
            }
            .setCancelable(false)
            .show()
    }
}

