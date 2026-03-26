package com.example.moodymusicforandroid.ui.home

import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.base.BaseFragment
import com.example.moodymusicforandroid.databinding.FragmentDiscoverBinding
import com.example.moodymusicforandroid.ui.home.viewmodel.DiscoverViewModel

class DiscoverFragment : BaseFragment<FragmentDiscoverBinding, DiscoverViewModel>() {

    override fun getViewModelClass(): Class<DiscoverViewModel> = DiscoverViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_discover

    override fun initView() {
        super.initView()
        // 此处可添加 Toolbar 按钮点击事件等
    }
}
