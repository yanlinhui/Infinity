package com.baidu.infinity.model

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.baidu.infinity.R
import com.baidu.infinity.ui.util.IconState
import com.baidu.infinity.ui.util.OperationType

/**
 * 封装一个图标
 * 1. 功能
 * 2. 图标的名称
 * 3. 正常和选中的背景颜色
 */
data class IconModel(
    val type: OperationType, //图标的功能类型
    @StringRes val iconString: Int, //文字图标名称的资源id
    var state: IconState = IconState.NORMAL, //图标的状态
    @ColorRes val normalColor: Int = R.color.middle_black, //正常状态下的颜色
    @ColorRes val selectColor: Int = R.color.light_blue, //选中状态下的颜色
)