package com.baidu.infinity.ui.fragment.password

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.baidu.infinity.R
import com.baidu.infinity.databinding.FragmentPicLoginBinding
import com.baidu.infinity.ui.base.BaseFragment
import com.baidu.infinity.ui.util.KeyboardAnimation
import com.baidu.infinity.ui.util.LoginRegisterResult
import com.baidu.infinity.ui.util.PasswordType
import com.baidu.infinity.ui.util.WrongType
import com.baidu.infinity.ui.util.delayTask
import com.baidu.infinity.viewmodel.UserViewModel

class PicLoginFragment: BaseFragment<FragmentPicLoginBinding>() {
    private val viewModel:UserViewModel by activityViewModels()

    override fun initBinding(): FragmentPicLoginBinding {
        return FragmentPicLoginBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun initView() {
        initkeyboardEvent()
        initKeyboardListener()
        initEvents()
    }
    private fun initEvents(){
        mBinding.usernameView.addTextChangeListener {
            mBinding.loginButton.isEnabled = mBinding.usernameView.text().isNotEmpty()
                    && mBinding.passweordView.text().isNotEmpty()
        }
        mBinding.passweordView.addTextChangeListener {
            mBinding.loginButton.isEnabled = mBinding.usernameView.text().isNotEmpty()
                    && mBinding.passweordView.text().isNotEmpty()
        }
        mBinding.loginButton.setOnClickListener {
            viewModel.login(mBinding.usernameView.text(), mBinding.passweordView.text(),PasswordType.PIC)
        }
        //监听登录结果
        viewModel.loginRegisterResult.observe(viewLifecycleOwner){
            when(it){
                is LoginRegisterResult.Success -> {
                    //成功登录 进入下一个界面
                    mBinding.alertView.showMessage("登录成功")
                    findNavController().navigate(R.id.action_picLoginFragment_to_homeFragment)
                }
                is LoginRegisterResult.Failure -> {
                    //登录失败
                    when(it.type){
                        WrongType.USER_NOT_FOUND -> {
                            mBinding.usernameView.showError()
                            mBinding.alertView.showErrorMessage("用户不存在")
                        }
                        WrongType.WRONG_PASSWORD -> {
                            mBinding.passweordView.showError()
                            mBinding.alertView.showErrorMessage("密码错误")
                        }
                        WrongType.USER_LOGINED ->{
                            mBinding.alertView.showMessage("用户已登录 立刻跳转")
                            delayTask(1500) {
                                findNavController().navigate(R.id.action_picLoginFragment_to_homeFragment)
                            }
                        }
                        else -> {}
                    }
                }//welcome -> pic -> home
                else ->{
                    //findNavController().navigate(R.id.action_picLoginFragment_to_homeFragment)
                }
            }
        }
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