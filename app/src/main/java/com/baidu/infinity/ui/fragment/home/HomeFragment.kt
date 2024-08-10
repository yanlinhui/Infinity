package com.baidu.infinity.ui.fragment.home

import android.graphics.Color
import android.widget.PopupWindow
import com.baidu.infinity.databinding.FragmentHomeBinding
import com.baidu.infinity.ui.base.BaseFragment
import com.baidu.infinity.ui.util.getOperationToolIconModels
import com.skydoves.colorpickerview.ColorPickerDialog

class HomeFragment: BaseFragment<FragmentHomeBinding>() {
    override fun initBinding(): FragmentHomeBinding {

        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun initView() {
        //配置数据源
        mBinding.iconMenuView.setIcons(getOperationToolIconModels())
        //监听工具点击事件
        mBinding.iconMenuView.iconClickListener = { type ->

        }
    }
}

