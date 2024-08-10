package com.baidu.infinity.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import com.baidu.infinity.model.IconModel
import com.baidu.infinity.ui.util.IconState
import com.baidu.infinity.ui.util.OperationType
import com.baidu.infinity.ui.util.dp2px
import com.baidu.infinity.ui.util.toast
import com.baidu.infinity.viewmodel.HomeViewModel

/**
 * 横向或者纵向排列的图标菜单
 * 外部需要传递一个模型数组
 * 外部需要设置布局的方向
 */
class IconMenuView(
    context: Context,
    attrs: AttributeSet?
): LinearLayout(context,attrs) {
    private var iconList:List<IconModel> = emptyList()
    private val defaultWidth = dp2px(40)
    private val defaultHeight = dp2px(40)
    private var mWidth = defaultWidth
    private var mHeight = defaultHeight
    private var mCurrentSelectedView:IconTextView? = null
    var iconClickListener:(OperationType)->Unit = {}

    //给外部提供一个设置数据模型的方法
    fun setIcons(icons: List<IconModel>) {
        iconList = icons

        //设置weight总和
        weightSum = icons.size.toFloat()
        //设置显示在中心
        gravity = Gravity.CENTER
        //添加CircleIconView
        icons.forEach { model ->
            val circleIconView = CircleIconView(context)
            circleIconView.setIconModel(model)
            circleIconView.clickCallback = { iconTextView ->
                dealWithCallback(iconTextView)
            }
            //设置布局参数
            val lp = LayoutParams(mWidth, mHeight)
            lp.weight = 1f
            if (orientation == VERTICAL) {
                lp.topMargin = dp2px(5)
            }else{
                lp.leftMargin = dp2px(5)
            }
            //添加到布局中
            addView(circleIconView,lp)
        }
    }

    //处理点击事件
    private fun dealWithCallback(iconTextView: IconTextView){
        //判断是否有选中的
        if (mCurrentSelectedView == null){
            //选中当前这个
            iconTextView.updateIconState(IconState.SELECTED)
            mCurrentSelectedView = iconTextView
            //告诉外部自己是什么类型的工具
            iconClickListener(iconTextView.mIconModel!!.type)
        }else{
            //之前选中过
            //判断是否是同一个
            if (mCurrentSelectedView != iconTextView){
                //取消之前的选中状态
                mCurrentSelectedView!!.updateIconState(IconState.NORMAL)
                //选中当前这个
                iconTextView.updateIconState(IconState.SELECTED)
                //保存这个对象
                mCurrentSelectedView = iconTextView
                //告诉外部自己是什么类型的工具
                iconClickListener(iconTextView.mIconModel!!.type)
            }
        }
    }
}










