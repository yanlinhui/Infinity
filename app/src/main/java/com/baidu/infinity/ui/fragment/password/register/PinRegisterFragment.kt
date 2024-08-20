package com.baidu.infinity.ui.fragment.password.register

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.baidu.infinity.databinding.FragmentPinRegistBinding
import com.baidu.infinity.ui.base.BaseFragment
import com.baidu.infinity.viewmodel.UserViewModel

class PinRegisterFragment: BaseFragment<FragmentPinRegistBinding>() {
    private val viewModel: UserViewModel by activityViewModels()

    override fun initBinding(): FragmentPinRegistBinding {
        return FragmentPinRegistBinding.inflate(layoutInflater)
    }

    override fun initView() {
        //监听输入框事件
        mBinding.usernameView.addTextChangeListener {
            changeRegisterState()
        }
        mBinding.passweordView.addTextChangeListener {
            changeRegisterState()
        }
        mBinding.surePassweordView.addTextChangeListener {
            changeRegisterState()
        }

        //按钮点击事件
        mBinding.switchButton.setOnClickListener {
            //判断两个密码是否一致
            if (mBinding.passweordView.text() != mBinding.surePassweordView.text()){
                mBinding.passweordView.showError()
                mBinding.surePassweordView.showError()
            }else{
                //跳转到图案解锁界面
                val directions = PinRegisterFragmentDirections
                    .actionPinRegisterFragmentToPicRegisterFragment2(
                        mBinding.usernameView.text(),
                        mBinding.passweordView.text()
                    )
                findNavController().navigate(directions)
            }
        }
    }

    private fun changeRegisterState() {
        mBinding.switchButton.isEnabled =
            mBinding.usernameView.text().isNotEmpty()
                    && mBinding.passweordView.text().isNotEmpty()
                    && mBinding.surePassweordView.text().isNotEmpty()
    }
}

















