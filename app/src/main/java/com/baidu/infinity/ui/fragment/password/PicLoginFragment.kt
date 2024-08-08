package com.baidu.infinity.ui.fragment.password

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.baidu.infinity.databinding.FragmentPicLoginBinding
import com.baidu.infinity.ui.base.BaseFragment
import com.baidu.infinity.ui.util.KeyboardAnimation

class PicLoginFragment: BaseFragment<FragmentPicLoginBinding>() {
    override fun initBinding(): FragmentPicLoginBinding {
        return FragmentPicLoginBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun initView() {
        initkeyboardEvent()
        initKeyboardListener()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun initkeyboardEvent(){
        mBinding.root.setOnClickListener {
            val insets = ViewCompat.getRootWindowInsets(mBinding.root)
            insets?.also{
                if (insets.isVisible(WindowInsetsCompat.Type.ime())){
                    //隐藏键盘
                    mBinding.root.windowInsetsController?.hide(WindowInsetsCompat.Type.ime())
                }
            }
        }
    }
    private fun initKeyboardListener(){
        ViewCompat.setOnApplyWindowInsetsListener(mBinding.inputlayout, object :
            OnApplyWindowInsetsListener {
            override fun onApplyWindowInsets(
                v: View,
                insets: WindowInsetsCompat
            ): WindowInsetsCompat {
                //键盘动画
                KeyboardAnimation.setupKeyboardAnimation(mBinding.inputlayout, 120)
                return insets
            }
        })
    }
}