package com.baidu.infinity.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import com.baidu.infinity.model.IconModel
import com.baidu.infinity.ui.util.dp2px

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
    private val defaultWidth = dp2px(50)
    private val defaultHeight = dp2px(50)
    private var mWidth = defaultWidth
    private var mHeight = defaultHeight

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
            //设置布局参数
            val lp = LayoutParams(mWidth, mHeight)
            lp.weight = 1f
            //添加到布局中
            addView(circleIconView,lp)
        }
    }
}










