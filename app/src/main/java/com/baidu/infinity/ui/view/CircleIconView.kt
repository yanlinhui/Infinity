package com.baidu.infinity.ui.view

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import com.baidu.infinity.R
import com.baidu.infinity.model.IconModel
import io.github.florent37.shapeofview.shapes.CircleView

class CircleIconView(
    context: Context,
    attrs: AttributeSet?
): CircleView(context, attrs) {
    private var mIconTextView: IconTextView
    init {
        //在内部添加一个IconTextView
        mIconTextView = IconTextView(context)
        //确定布局参数
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        lp.gravity = Gravity.CENTER
        //加到容器上
        addView(mIconTextView,lp)

        //解析自定义的属性
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CircleIconView)
        val textSize = ta.getDimension(R.styleable.CircleIconView_icon_size_sp,25f)
        ta.recycle()
        //设置字体大小
        setIconSize(textSize.toInt())
    }

    fun setIconModel(model: IconModel){
        mIconTextView.setIconModel(model)
    }

    //设置图标大小
    fun setIconSize(size: Int){
        //设置字体大小
        mIconTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
    }
}