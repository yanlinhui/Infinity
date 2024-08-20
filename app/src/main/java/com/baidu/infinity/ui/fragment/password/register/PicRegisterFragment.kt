package com.baidu.infinity.ui.fragment.password.register


import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.baidu.infinity.R
import com.baidu.infinity.databinding.FragmentPicRegistBinding
import com.baidu.infinity.ui.base.BaseFragment
import com.baidu.infinity.ui.util.LoginRegisterResult
import com.baidu.infinity.viewmodel.UserViewModel

class PicRegisterFragment: BaseFragment<FragmentPicRegistBinding>() {
    private val viewModel: UserViewModel by activityViewModels()
    private val args:PicRegisterFragmentArgs by navArgs()
    private var mPassword:String? = null

    override fun initBinding(): FragmentPicRegistBinding {
        return FragmentPicRegistBinding.inflate(layoutInflater)
    }

    override fun initView() {
        //监听注册结果
        viewModel.loginRegisterResult.observe(viewLifecycleOwner){
            if(it is LoginRegisterResult.Success){
                //切换到登录界面
                viewModel.resetLoginState()
                val direction = PicRegisterFragmentDirections.actionPicRegisterFragmentToPicLoginFragment2(true)
                findNavController().navigate(direction)
            }
        }
        mBinding.tvAlert.text = "请设置密码图案"
        //接收UnlockView 回 调的密码
        mBinding.unlockView.addPicPathFinishedListener { string ->
            if (mPassword == null){ //第一次设置密码
                mPassword = string
                mBinding.tvAlert.text = "请再次确认密码图案"
                true //立刻隐藏
            }else{ //这是第二次确认密码
                if (mPassword == string) { //两次密码一致
                    //注册用户
                    viewModel.register(args.name,args.password,string)
                    true //立刻隐藏
                }else{
                    //两次密码不一致
                    //1.报错 unlockView显示红  提示文本提示错误
                    mBinding.tvAlert.text = "两次密码不一致 请重新设置密码图案"
                    mBinding.unlockView.showError()
                    //2.清空密码 123  235
                    mPassword = null
                    false //不要立刻隐藏
                }
            }
        }
    }
}