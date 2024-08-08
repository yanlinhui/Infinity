package com.baidu.infinity.ui.fragment.home

import com.baidu.infinity.databinding.FragmentHomeBinding
import com.baidu.infinity.ui.base.BaseFragment

class HomeFragment: BaseFragment<FragmentHomeBinding>() {
    override fun initBinding(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun initView() {
        // TODO: 2022/11/22
    }
}