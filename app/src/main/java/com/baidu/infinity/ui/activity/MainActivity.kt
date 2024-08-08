package com.baidu.infinity.ui.activity

import com.baidu.infinity.databinding.ActivityMainBinding
import com.baidu.infinity.ui.base.BaseActivity
import com.baidu.infinity.ui.util.toast


class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun initBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {

    }

}