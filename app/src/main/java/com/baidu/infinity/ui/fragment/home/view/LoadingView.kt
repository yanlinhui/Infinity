package com.baidu.infinity.ui.fragment.home.view

import android.animation.Animator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager.LayoutParams
import android.widget.PopupWindow
import androidx.annotation.DrawableRes
import com.baidu.infinity.R
import com.baidu.infinity.databinding.LayerItemLayoutBinding
import com.baidu.infinity.databinding.LoadingLayoutBinding
import com.baidu.infinity.databinding.PickimagePopupViewLayoutBinding
import com.baidu.infinity.ui.fragment.home.layer.LayerState
import com.bumptech.glide.Glide
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup

/**
 * 将PopupWindow封装在内部
 * 方便外部调用
 * 降低耦合性
 */
class LoadingView(val context: Context) {
    private var mBinding: LoadingLayoutBinding? = null

    private val popupWindow:PopupWindow by lazy {
        val inflater = LayoutInflater.from(context)
        mBinding = LoadingLayoutBinding.inflate(inflater)
        PopupWindow(context).apply {
            contentView = mBinding!!.root
            width = LayoutParams.WRAP_CONTENT
            height = LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    //show
    fun show(parent: View){
        popupWindow.showAtLocation(parent,Gravity.CENTER,0,0)
    }

    //hide
    fun hide(){
        mBinding!!.loadingView.visibility = View.INVISIBLE
        mBinding!!.okView.visibility = View.VISIBLE
        mBinding!!.okView.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator) {
            }
            override fun onAnimationEnd(animation: Animator) {
                popupWindow.dismiss()
                mBinding!!.loadingView.visibility = View.VISIBLE
                mBinding!!.okView.visibility = View.INVISIBLE
            }
            override fun onAnimationCancel(animation: Animator) {
            }
            override fun onAnimationRepeat(animation: Animator) {
            }
        })
    }
}