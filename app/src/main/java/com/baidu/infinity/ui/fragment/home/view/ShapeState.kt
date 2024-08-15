package com.baidu.infinity.ui.fragment.home.view

/**
 * 记录所有图形绘制过程中的状态
 */
enum class ShapeState {
    NORMAL,//正常状态
    DRAWING, //绘制状态
    SELECT,//移动时选中状态
}