package com.baidu.infinity.ui.fragment.home

import com.baidu.infinity.R
import com.baidu.infinity.databinding.FragmentHomeBinding
import com.baidu.infinity.model.IconModel
import com.baidu.infinity.ui.base.BaseFragment
import com.baidu.infinity.ui.util.OperationType
import com.baidu.infinity.ui.util.getDrawToolIconModels
import com.baidu.infinity.ui.util.getHomeMenuIconModels
import com.baidu.infinity.ui.util.getOperationToolIconModels

class HomeFragment: BaseFragment<FragmentHomeBinding>() {
    override fun initBinding(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun initView() {
        //配置数据源
        //mBinding.iconMenuView.setIcons(getOperationToolIconModels())
    }
}