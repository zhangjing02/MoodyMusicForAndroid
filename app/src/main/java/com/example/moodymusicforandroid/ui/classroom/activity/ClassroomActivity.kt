package com.example.moodymusicforandroid.ui.classroom.activity

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.base.BaseActivity
import com.example.moodymusicforandroid.data.model.RosterItem
import com.example.moodymusicforandroid.databinding.ActivityClassroomBinding
import com.example.moodymusicforandroid.ui.classroom.adapter.SeatAdapter
import com.example.moodymusicforandroid.ui.classroom.viewmodel.ClassroomViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout

class ClassroomActivity : BaseActivity<ActivityClassroomBinding, ClassroomViewModel>() {

    private lateinit var adapter: SeatAdapter
    private val seatColumnCount = 8

    private var claimVerifyDialog: AlertDialog? = null
    private var claimInputLayouts: List<TextInputLayout> = emptyList()
    private var claimInputFields: List<EditText> = emptyList()

    private var claimFinalizeDialog: AlertDialog? = null

    override fun getViewModelClass() = ClassroomViewModel::class.java

    override fun getLayoutId() = R.layout.activity_classroom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        observeViewModel()
        viewModel.fetchRoster()
    }

    override fun onDestroy() {
        claimVerifyDialog?.dismiss()
        claimFinalizeDialog?.dismiss()
        super.onDestroy()
    }

    private fun setupUI() {
        adapter = SeatAdapter(columnCount = seatColumnCount) { seat ->
            handleSeatClick(seat)
        }

        binding.rvSeats.layoutManager = GridLayoutManager(
            this,
            seatColumnCount,
            RecyclerView.VERTICAL,
            false
        )
        binding.rvSeats.adapter = adapter

        binding.rvSeats.layoutAnimation = android.view.animation.AnimationUtils.loadLayoutAnimation(
            this,
            R.anim.anim_layout_fall_down
        )

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun observeViewModel() {
        viewModel.classroomData.observe(this) { data ->
            adapter.submitList(data.roster)

            binding.tvBlackboardTitle.text = "同学录 · 青春到此一游"
            binding.tvBlackboardHint.text = "点击你的姓名认领座位"

            val claimedCount = data.roster.count { it.isClaimed == 1 }
            binding.tvClaimedCount.text = claimedCount.toString()
            binding.tvTotalCount.text = data.roster.size.toString()

            binding.rvSeats.scheduleLayoutAnimation()

            binding.hsvClassroom.post {
                val scrollWidth = binding.rvSeats.width
                val containerWidth = binding.hsvClassroom.width
                if (scrollWidth > containerWidth) {
                    binding.hsvClassroom.scrollTo((scrollWidth - containerWidth) / 2, 0)
                }
            }
        }

        viewModel.verifyResult.observe(this) { response ->
            val claimToken = response.claimToken.trim()
            if (claimToken.isBlank()) {
                claimVerifyDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = true
                showToast("验证通过但未拿到 claim token，请重试")
                return@observe
            }
            claimVerifyDialog?.dismiss()
            showClaimFinalizeDialog(claimToken)
        }

        viewModel.claimResult.observe(this) { user ->
            claimFinalizeDialog?.dismiss()
            Toast.makeText(this, "认领成功，${user.username}，欢迎回到教室", Toast.LENGTH_SHORT).show()
            finish()
        }

        viewModel.loginResult.observe(this) {
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun handleError(errorMsg: String) {
        val handledInVerifyDialog = handleClaimVerifyError(errorMsg)
        val handledInFinalizeDialog = if (!handledInVerifyDialog) handleClaimFinalizeError(errorMsg) else false

        if (!handledInVerifyDialog && !handledInFinalizeDialog) {
            showToast(errorMsg)
        }
    }

    private fun handleSeatClick(seat: RosterItem) {
        if (seat.isClaimed == 1) {
            showLoginDialog(seat.realName)
        } else {
            showClaimVerifyDialog(seat)
        }
    }

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

    private fun showClaimVerifyDialog(seat: RosterItem) {
        val questions = viewModel.classroomData.value?.securityQuestions?.take(3) ?: emptyList()
        if (questions.size < 3) {
            Toast.makeText(this, "请先在后端配置 3 个安全问题", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input_three_fields, null)
        val etField1 = dialogView.findViewById<EditText>(R.id.etField1)
        val etField2 = dialogView.findViewById<EditText>(R.id.etField2)
        val etField3 = dialogView.findViewById<EditText>(R.id.etField3)

        val tilField1 = dialogView.findViewById<TextInputLayout>(R.id.tilField1)
        val tilField2 = dialogView.findViewById<TextInputLayout>(R.id.tilField2)
        val tilField3 = dialogView.findViewById<TextInputLayout>(R.id.tilField3)

        etField1.hint = questions[0].question
        etField2.hint = questions[1].question
        etField3.hint = questions[2].question

        claimInputLayouts = listOf(tilField1, tilField2, tilField3)
        claimInputFields = listOf(etField1, etField2, etField3)
        clearClaimFieldErrors()

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("认领座位: ${seat.realName}")
            .setView(dialogView)
            .setPositiveButton("下一步", null)
            .setNegativeButton("取消", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                clearClaimFieldErrors()

                val answers = listOf(
                    etField1.text?.toString()?.trim().orEmpty(),
                    etField2.text?.toString()?.trim().orEmpty(),
                    etField3.text?.toString()?.trim().orEmpty()
                )

                val firstEmptyIndex = answers.indexOfFirst { it.isEmpty() }
                if (firstEmptyIndex >= 0) {
                    markClaimFieldError(firstEmptyIndex, "请填写该题答案")
                    return@setOnClickListener
                }

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                viewModel.verifyClaim(seat.id, answers)
            }
        }

        dialog.setOnDismissListener {
            clearClaimVerifyDialogState()
        }

        claimVerifyDialog = dialog
        dialog.show()
    }

    private fun handleClaimVerifyError(message: String): Boolean {
        val dialog = claimVerifyDialog ?: return false
        if (!dialog.isShowing || claimInputLayouts.isEmpty()) {
            return false
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = true

        val wrongAnswerIndex = Regex("""第\s*(\d+)\s*道问题答案不正确""")
            .find(message)
            ?.groupValues
            ?.getOrNull(1)
            ?.toIntOrNull()
            ?.minus(1)

        if (wrongAnswerIndex != null && wrongAnswerIndex in claimInputLayouts.indices) {
            markClaimFieldError(wrongAnswerIndex, message)
            return true
        }

        if (message.contains("答案不正确")) {
            claimInputLayouts.forEach { it.error = "答案不正确，请重新填写" }
            claimInputFields.firstOrNull()?.requestFocus()
            return true
        }

        return false
    }

    private fun clearClaimFieldErrors() {
        claimInputLayouts.forEach { it.error = null }
    }

    private fun markClaimFieldError(index: Int, errorMessage: String) {
        val targetLayout = claimInputLayouts.getOrNull(index) ?: return
        val targetField = claimInputFields.getOrNull(index)

        targetLayout.error = errorMessage
        targetField?.requestFocus()
        targetField?.setSelection(targetField.text?.length ?: 0)
    }

    private fun clearClaimVerifyDialogState() {
        claimVerifyDialog = null
        claimInputLayouts = emptyList()
        claimInputFields = emptyList()
    }

    private fun showClaimFinalizeDialog(claimToken: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input_one_field, null)
        val etPassword = dialogView.findViewById<EditText>(R.id.etInput)
        etPassword.hint = "设置你的新密码"
        etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("验证通过")
            .setMessage("请设置一个新密码，邮箱可稍后绑定。")
            .setView(dialogView)
            .setPositiveButton("完成认领", null)
            .setNegativeButton("取消", null)
            .setCancelable(false)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val password = etPassword.text?.toString()?.trim().orEmpty()
                if (password.isBlank()) {
                    showToast("密码不能为空")
                    return@setOnClickListener
                }
                if (password.length < 6) {
                    showToast("密码至少 6 位")
                    return@setOnClickListener
                }

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                viewModel.finalizeClaim(claimToken, password)
            }
        }

        dialog.setOnDismissListener {
            clearFinalizeDialogState()
        }

        claimFinalizeDialog = dialog
        dialog.show()
    }

    private fun handleClaimFinalizeError(message: String): Boolean {
        val dialog = claimFinalizeDialog ?: return false
        if (!dialog.isShowing) {
            return false
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = true

        if (message.contains("email", ignoreCase = true) || message.contains("邮箱")) {
            showToast("后台免邮箱注册正在发布中，请稍后再试")
            return true
        }

        return false
    }

    private fun clearFinalizeDialogState() {
        claimFinalizeDialog = null
    }
}