package com.example.moodymusicforandroid.ui.home

import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.base.BaseFragment
import com.example.moodymusicforandroid.databinding.FragmentHomeBinding
import com.example.moodymusicforandroid.ui.home.viewmodel.HomeViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override fun getViewModelClass(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun initView() {
        super.initView()
        // TODO: Add click listeners and UI interactions
    }

    override fun initData() {
        super.initData()
        // TODO: Load data from ViewModel
    }
}
