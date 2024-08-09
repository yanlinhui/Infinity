package com.baidu.infinity.ui.fragment.home

import com.baidu.infinity.R
import com.baidu.infinity.databinding.FragmentHomeBinding
import com.baidu.infinity.model.IconModel
import com.baidu.infinity.ui.base.BaseFragment
import com.baidu.infinity.ui.util.OperationType

class HomeFragment: BaseFragment<FragmentHomeBinding>() {
    override fun initBinding(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val model = IconModel(
            OperationType.DRAW_MOVE,
            R.string.menu
        )
        mBinding.circleIconView.setIconModel(model)
        //mBinding.circleIconView.setIconSize(30)
    }
}