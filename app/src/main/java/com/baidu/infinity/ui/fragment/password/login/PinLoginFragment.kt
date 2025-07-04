package com.baidu.infinity.ui.fragment.password.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.baidu.infinity.R
import com.baidu.infinity.databinding.FragmentPinLoginBinding
import com.baidu.infinity.ui.base.BaseFragment
import com.baidu.infinity.ui.fragment.home.file.FileManager
import com.baidu.infinity.ui.util.LoginRegisterResult
import com.baidu.infinity.ui.util.PasswordType
import com.baidu.infinity.ui.util.toast
import com.baidu.infinity.viewmodel.UserViewModel

class PinLoginFragment: BaseFragment<FragmentPinLoginBinding>() {
    private val viewModel:UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transInflater = TransitionInflater.from(requireContext())
        sharedElementEnterTransition = transInflater.inflateTransition(android.R.transition.explode)
        sharedElementReturnTransition = transInflater.inflateTransition(android.R.transition.explode)
    }

    override fun initBinding(): FragmentPinLoginBinding {
        return FragmentPinLoginBinding.inflate(layoutInflater)
    }

    override fun initView() {
        viewModel.loginRegisterResult.observe(viewLifecycleOwner){ result ->
            when (result){
                is LoginRegisterResult.Success ->{
                    //配置文件系统
                    FileManager.instance.login(viewModel.currentUser!!.name)
                    findNavController().navigate(R.id.action_pinLoginFragment_to_homeFragment)
                }
                is LoginRegisterResult.Failure ->{
                    toast("${result.type}")
                }
                else ->{}
            }
        }
        mBinding.unlockView.addPicPathFinishedListener {
            if (it == viewModel.currentUser?.pinPassword){
                //密码正确
                viewModel.login(viewModel.currentUser!!.name,it,PasswordType.PIN)
                true
            }else{
                //密码错误
                mBinding.unlockView.showError()
                false
            }
        }
    }
}