package com.baidu.infinity.ui.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import com.baidu.infinity.model.IconModel
import com.baidu.infinity.ui.util.IconState
import com.baidu.infinity.ui.util.toast
import com.baidu.infinity.viewmodel.HomeViewModel
import java.lang.ref.WeakReference

/**
 * 广播的使用步骤
 * 1. 在AndroidManifest.xml中注册广播接收器 确定要接收什么类型的广播
 * 2. 在代码中创建广播接收器 BroadcastReceiver
 * 3. 在代码中注册广播接收器
 * 4. 在代码中发送广播
 *
 * 动态注册
 *     代码中注册
 * 静态注册
 *    在AndroidManifest.xml中注册
 */

/**
 * 能够显示文字图标
 * 切换背景颜色
 */
class IconTextView(
    context: Context,
    attrs: AttributeSet? = null
): AppCompatTextView(context, attrs) {
    var mIconModel: IconModel? = null
    var clickCallback:(IconTextView)->Unit = {}

    init {
        typeface = Typeface.createFromAsset(context.assets, "iconfont.ttf")
        //配置图标在中心位置
        gravity = Gravity.CENTER
        //配置文字颜色
        setTextColor(Color.WHITE)
        //配置点击事件
        setOnClickListener {
            clickCallback(this)
        }
    }

    //接收外部传递过来的这个图标的模型
    fun setIconModel(model: IconModel) {
        //保存model
        mIconModel = model
        //配置显示的图标
        text = resources.getString(model.iconString)
        //配置默认显示的背景颜色
        setBackgroundColor(resources.getColor(model.normalColor,null))
    }

    //提供给外部一个方法 更新当前这个icon的状态
    fun updateIconState(state: IconState) {
        if(state == IconState.SELECTED) {
            setBackgroundColor(resources.getColor(mIconModel?.selectColor?:0,null))
        } else {
            setBackgroundColor(resources.getColor(mIconModel?.normalColor?:0,null))
        }
        mIconModel?.state = state
    }
}