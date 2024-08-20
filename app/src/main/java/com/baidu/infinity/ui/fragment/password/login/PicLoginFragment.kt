package com.baidu.infinity.ui.fragment.password.login

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
    private val args: PicLoginFragmentArgs by navArgs()

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
                    //200ms之后再跳转
                    delayTask(200){
                        //跳转分两种
                        if (args.hasHome){//从home过来的
                            findNavController().navigateUp()
                        }else {//第一次进入
                            findNavController().navigate(R.id.action_picLoginFragment_to_homeFragment)
                        }
                    }
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
                is LoginRegisterResult.None -> {}
            }
        }

        //更具是否第一次进入这个界面调整back是否显示
        if (args.hasHome){
            //从home界面跳转过来的
            showBack()
            mBinding.backBtn.setOnClickListener {
                //直接将当前栈上的第一个元素弹出 == 把这个界面弹出 显示Home界面
                findNavController().navigateUp()
            }
        }else{
            //从welcome界面进来的
            hideBack()
        }

        //添加点击注册事件
        mBinding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_picLoginFragment_to_pinRegisterFragment)
        }
    }

    private fun showBack(){
        mBinding.backBtn.visibility = View.VISIBLE
        mBinding.tvBack.visibility = View.VISIBLE
    }
    private fun hideBack(){
        mBinding.backBtn.visibility = View.INVISIBLE
        mBinding.tvBack.visibility = View.INVISIBLE
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