package com.baidu.infinity.ui.fragment.home

import com.baidu.infinity.databinding.FragmentHomeBinding
import com.baidu.infinity.ui.base.BaseFragment
import com.baidu.infinity.ui.util.getOperationToolIconModels
import com.baidu.infinity.ui.util.toast

class HomeFragment: BaseFragment<FragmentHomeBinding>() {
    override fun initBinding(): FragmentHomeBinding {

        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun initView() {
        //配置数据源
        mBinding.iconMenuView.setIcons(getOperationToolIconModels())
        //监听工具点击事件
        mBinding.iconMenuView.iconClickListener = { type ->
            toast("$type")
        }
    }

}

