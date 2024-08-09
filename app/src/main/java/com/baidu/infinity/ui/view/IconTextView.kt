package com.baidu.infinity.ui.view

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import com.baidu.infinity.model.IconModel
import com.baidu.infinity.ui.util.IconState

/**
 * 能够显示文字图标
 * 切换背景颜色
 */
class IconTextView(
    context: Context,
    attrs: AttributeSet? = null
): AppCompatTextView(context, attrs) {
    private var iconModel: IconModel? = null

    init {
        typeface = Typeface.createFromAsset(context.assets, "iconfont.ttf")
        //配置图标在中心位置
        gravity = Gravity.CENTER
        //配置文字颜色
        setTextColor(Color.WHITE)
        //配置点击事件
        setOnClickListener {
            //切换状态
            iconModel?.let {
                if(it.state == IconState.NORMAL) {
                    it.state = IconState.SELECTED
                    setBackgroundColor(resources.getColor(it.selectColor,null))
                } else {
                    it.state = IconState.NORMAL
                    setBackgroundColor(resources.getColor(it.normalColor,null))
                }
            }
        }
    }

    //接收外部传递过来的这个图标的模型
    fun setIconModel(model: IconModel) {
        //保存model
        iconModel = model
        //配置显示的图标
        text = resources.getString(model.iconString)
        //配置默认显示的背景颜色
        setBackgroundColor(resources.getColor(model.normalColor,null))
    }
}