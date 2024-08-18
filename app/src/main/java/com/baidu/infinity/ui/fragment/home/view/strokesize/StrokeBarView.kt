package com.baidu.infinity.ui.fragment.home.view.strokesize

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.baidu.infinity.databinding.StrokeBarVewLayoutBinding
import com.baidu.infinity.ui.util.dp2pxF
import com.baidu.infinity.ui.util.toast
import com.baidu.infinity.viewmodel.HomeViewModel

class StrokeBarView(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs){
    private var mBinding: StrokeBarVewLayoutBinding? = null
    private val mDistance: Float by lazy {
        (mBinding!!.ivDotFill.top - mBinding!!.ivDotEmpty.top).toFloat()
    }
    private val mDownAnimation: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(mBinding?.indicatorView, "translationY",mDistance).apply {
            duration = 200
        }
    }
    private val mUpAnimation: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(mBinding?.indicatorView, "translationY",0f).apply {
            duration = 200
        }
    }
    private var mIsEmptyStyle = true

    init {
        val inflater = LayoutInflater.from(context)
        mBinding = StrokeBarVewLayoutBinding.inflate(inflater)
        val lp = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        addView(mBinding!!.root,lp)

        //添加事件
        mBinding!!.barView.addProgressChangeListener = { progress ->
            mBinding!!.tvSize.text = "$progress"
            HomeViewModel.instance().mStrokeWidth = dp2pxF(progress)
        }
        mBinding!!.barView.addTouchStateListener = { isTouch ->
            if (isTouch){
                mBinding!!.tvSize.visibility = View.VISIBLE
            }else{
                mBinding!!.tvSize.visibility = View.INVISIBLE
            }
        }

        mBinding!!.ivDotEmpty.setOnClickListener {
            if (!mUpAnimation.isRunning && !mIsEmptyStyle) {
                mUpAnimation.start()
                mIsEmptyStyle = true
                HomeViewModel.instance().mStrokeStyle = Paint.Style.STROKE
            }
        }
        mBinding!!.ivDotFill.setOnClickListener {
            if (!mDownAnimation.isRunning && mIsEmptyStyle) {
                mDownAnimation.start()
                mIsEmptyStyle = false
                HomeViewModel.instance().mStrokeStyle = Paint.Style.FILL
            }
        }
    }
}