package com.baidu.infinity.ui.fragment.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.widget.PopupWindow
import com.baidu.infinity.R
import com.baidu.infinity.databinding.FragmentHomeBinding
import com.baidu.infinity.model.IconModel
import com.baidu.infinity.ui.base.BaseFragment
import com.baidu.infinity.ui.util.OperationType
import com.baidu.infinity.ui.util.getDrawToolIconModels
import com.baidu.infinity.ui.util.getHomeMenuIconModels
import com.baidu.infinity.ui.util.getMenuIconModel
import com.baidu.infinity.ui.util.getOperationToolIconModels
import com.skydoves.colorpickerview.ColorPickerDialog

class HomeFragment: BaseFragment<FragmentHomeBinding>() {
    private val closeBottom: Int by lazy {
        mBinding.drawLayout.top + mBinding.iconMenuView.top
    }
    private val openBottom: Int by lazy {
        mBinding.drawLayout.bottom
    }
    private val closeLeft: Int by lazy {
        mBinding.mainMenuView.right
    }
    private val openLeft: Int by lazy {
        mBinding.mainMenuView.left
    }
    private val hideLeft: Int by lazy {
        mBinding.root.width
    }
    private val showLeft: Int by lazy {
        mBinding.actionMenuView.left
    }

    private var isDrawMenuOpen = true //记录绘制工具menu的打开关闭状态
    private var isArrowRightOpen = true //记录绘制工具menu的打开关闭状态

    //绘制工具展开和收回动画
    private val drawMenuCloseAnim: AnimatorSet by lazy {
        val r = ObjectAnimator.ofFloat(mBinding.menuIconView,"rotation",0f,360f).apply{
            duration = 400
        }
        val m = ObjectAnimator.ofInt(mBinding.drawLayout,"bottom",openBottom,closeBottom).apply {
            duration = 400
        }
        AnimatorSet().apply {
            playTogether(r,m)
        }
    }
    private val drawMenuOpenAnim: AnimatorSet by lazy {
        val r = ObjectAnimator.ofFloat(mBinding.menuIconView,"rotation",360f,0f).apply{
            duration = 400
        }
        val m = ObjectAnimator.ofInt(mBinding.drawLayout,"bottom",closeBottom,openBottom).apply {
            duration = 400
        }
        AnimatorSet().apply {
            playTogether(r,m)
        }
    }
    //主菜单展开收回动画
    private val mainMenuCloseAnim: ObjectAnimator by lazy {
        ObjectAnimator.ofInt(mBinding.mainMenuView,"left",openLeft,closeLeft).apply {
            duration = 400
        }
    }
    private val mainMenuOpenAnim: ObjectAnimator by lazy {
        ObjectAnimator.ofInt(mBinding.mainMenuView,"left",closeLeft,openLeft).apply {
            duration = 400
        }
    }
    //工具操作菜单隐藏和显示动画
    private val actionMenuHideAnim: AnimatorSet by lazy {
        val alpha = ObjectAnimator.ofFloat(mBinding.actionMenuView,"alpha",1f,0f).apply {
            duration = 400
        }
        val move = ObjectAnimator.ofInt(mBinding.actionMenuView,"left",showLeft,hideLeft).apply {
            duration = 400
        }
        AnimatorSet().apply {
            playTogether(move)
        }
    }
    private val actionMenuShowAnim: AnimatorSet by lazy {
        val alpha = ObjectAnimator.ofFloat(mBinding.actionMenuView,"alpha",0f,1f).apply {
            duration = 400
        }
        val move = ObjectAnimator.ofInt(mBinding.actionMenuView,"left",hideLeft,showLeft).apply {
            duration = 400
        }
        AnimatorSet().apply {
            playTogether(move)
        }
    }
    //箭头左右旋转动画
    private val arrowRightAnim: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(mBinding.arrowImageView,"rotation",180f,0f).apply{
            duration = 400
        }
    }
    private val arrowLeftAnim: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(mBinding.arrowImageView,"rotation",0f,180f).apply{
            duration = 400
        }
    }

    override fun initBinding(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun initView() {
        //配置绘制工具栏
        mBinding.menuIconView.setIconModel(getMenuIconModel())
        mBinding.iconMenuView.setIcons(getDrawToolIconModels())
        mBinding.actionMenuView.setIcons(getOperationToolIconModels())
        mBinding.mainMenuView.setIcons(getHomeMenuIconModels())
        //监听工具点击事件
        mBinding.iconMenuView.iconClickListener = { type ->

        }
        //绘制工具栏的menu按钮被点击
        mBinding.menuIconView.clickCallback = {
            //如果有正在做动画 就不响应
            //if (drawMenuCloseAnim.isRunning || drawMenuOpenAnim.isRunning) return@setOnClickListener
            if (isDrawMenuOpen){
                //关闭动画
                drawMenuCloseAnim.start()
                actionMenuHideAnim.start()
            }else{
                drawMenuOpenAnim.start()
                actionMenuShowAnim.start()
            }
            isDrawMenuOpen = !isDrawMenuOpen
        }
        //主菜单arrow箭头点击
        mBinding.arrowImageView.setOnClickListener {
            if (isArrowRightOpen){ //关闭动画
                arrowLeftAnim.start()
                mainMenuCloseAnim.start()
            }else{ //展开动画
                arrowRightAnim.start()
                mainMenuOpenAnim.start()
            }
            isArrowRightOpen = !isArrowRightOpen
        }
    }
}

