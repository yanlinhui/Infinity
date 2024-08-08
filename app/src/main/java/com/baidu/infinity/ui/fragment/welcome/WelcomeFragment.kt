package com.baidu.infinity.ui.fragment.welcome

import android.animation.Animator
import android.graphics.Color
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.baidu.infinity.R
import com.baidu.infinity.databinding.FragmentWelcomeLayoutBinding
import com.baidu.infinity.ui.base.BaseFragment
import com.baidu.infinity.ui.util.PasswordType
import com.baidu.infinity.ui.util.toast
import com.baidu.infinity.viewmodel.UserViewModel
import java.util.Date

class WelcomeFragment: BaseFragment<FragmentWelcomeLayoutBinding>() {
    private val viewModel:UserViewModel by activityViewModels()
    private var isAnimatorEnd = false

    override fun initBinding(): FragmentWelcomeLayoutBinding {
        return FragmentWelcomeLayoutBinding.inflate(layoutInflater)
    }

    override fun initView() {
        //监听查找登录用户的操作是否结束
        viewModel.isFindFinihsed.observe(viewLifecycleOwner){
            if (isAnimatorEnd){
                navigate()
            }
        }
        //监听lottie动画事件
        mBinding.lottieView.addAnimatorListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator) {

            }
            override fun onAnimationEnd(animation: Animator) {
                //动画结束切换到下一个界面
                //检查超找用户是否结束
                if (viewModel.isFindFinihsed.value == true){
                    navigate()
                }else{
                    isAnimatorEnd = true
                }

            }
            override fun onAnimationCancel(animation: Animator) {

            }
            override fun onAnimationRepeat(animation: Animator) {

            }
        })
    }

    private fun insertUser(){
        //viewModel.register("merry","123","123")
        //viewModel.login("merry","123",PasswordType.PIN)
        viewModel.changePasswordType(PasswordType.PIC)
    }

    private fun navigate(){
        //为了测试 插入一个用户
        //insertUser()

        //获取登录用户
        val user = viewModel.currentUser

        //有登录用户
        if (user != null){
            //判断是否在有效期内 当前时间 - 登录时间
            val duration = Date().time - user.loginDate.time
            if (duration > user.validate){
                //过期 需要登录
                //看默认的登陆类型
                if (user.passwordType == 0){
                    toast("0")
                    val extras = FragmentNavigatorExtras(mBinding.animationView to "explode")
                    findNavController().navigate(
                        R.id.action_welcomeFragment_to_pinLoginFragment,
                        null,
                        null,
                        extras
                    )
                }else{
                    toast("1")
                    findNavController().navigate(R.id.action_welcomeFragment_to_picLoginFragment)
                }
            }else{
                //这个是游客 直接进入主页
                toast("2")
                findNavController().navigate(R.id.action_welcomeFragment_to_homeFragment)
            }
        }else{
            toast("3")
            //这个是游客 直接进入主页
            findNavController().navigate(R.id.action_welcomeFragment_to_homeFragment)
        }
    }
}