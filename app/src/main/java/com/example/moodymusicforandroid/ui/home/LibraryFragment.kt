package com.example.moodymusicforandroid.ui.home

import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.base.BaseFragment
import com.example.moodymusicforandroid.databinding.FragmentLibraryBinding
import com.example.moodymusicforandroid.ui.home.viewmodel.LibraryViewModel

class LibraryFragment : BaseFragment<FragmentLibraryBinding, LibraryViewModel>() {

    override fun getViewModelClass(): Class<LibraryViewModel> = LibraryViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_library

    override fun initView() {
        super.initView()
        // 此处可添加 Toolbar 按钮点击事件等
    }
}
