package com.baidu.infinity.ui.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.animation.AnticipateInterpolator
import android.view.animation.BounceInterpolator
import android.widget.FrameLayout
import com.baidu.infinity.databinding.LayoutAlertViewBinding
import com.baidu.infinity.ui.util.delayTask
import com.baidu.infinity.ui.util.dp2pxF

//约束布局 相对布局 帧布局 线性布局 都满足不了我们的要求就自己定义一个容器
//定义容器就是定义一套摆放的规则
class PXDAlertView(context: Context, attrs:AttributeSet?) : FrameLayout(context, attrs){
    private lateinit var binding: LayoutAlertViewBinding
    private val mDuration = 500L
    private val mDistance = dp2pxF(130)
    private val mDownAnimator:ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(
            this,
            "translationY",
            0f,
            mDistance
        ).apply {
            duration = mDuration
            interpolator = BounceInterpolator()
        }
    }
    private val mUpAnimator:ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(
            this,
            "translationY",
            mDistance,
            0f
        ).apply {
            duration = mDuration
            interpolator = AnticipateInterpolator()
        }
    }
    init {
        //将自己定义的视图添加到这个容器上
        val inflater = LayoutInflater.from(context)
        binding = LayoutAlertViewBinding.inflate(inflater)
        //确定布局参数
        val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        //将视图添加到容器上
        addView(binding.root,lp)
    }

    fun showMessage(message: String){
        binding.tvContent.text = message
        binding.tvContent.setTextColor(Color.BLACK)
        startAnimation()
    }
    fun showErrorMessage(message: String){
        binding.tvContent.text = message
        binding.tvContent.setTextColor(Color.RED)
        startAnimation()
    }

    private fun startAnimation(){
        mDownAnimator.start()
        mUpAnimator.startDelay = 1500
        mUpAnimator.start()
    }
}