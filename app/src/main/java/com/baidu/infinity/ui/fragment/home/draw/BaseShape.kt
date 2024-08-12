package com.baidu.infinity.ui.fragment.home.draw

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.baidu.infinity.ui.util.dp2pxF
import com.baidu.infinity.viewmodel.HomeViewModel

/**
 * 绘制图形的抽象类
 * 定义起点
 * 终点
 * 矩形区域
 * 绘制方法
 * 判断是否在区域内
 */
abstract class BaseShape {
    private var startX:Float = 0f
    private var startY:Float = 0f
    private var endX:Float = 0f
    private var endY:Float = 0f

    //中心点
    var centerX:Float = 0f
    var centerY:Float = 0f
    //矩形区域
    val rectF: RectF = RectF()
    //画笔
    val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = HomeViewModel.instance().mStrokeWidth
        color = HomeViewModel.instance().mColor
        style = HomeViewModel.instance().mStrokeStyle
    }

    //设置图形起始坐标
    fun setStartPoint(x:Float,y:Float){
        startX = x
        startY = y
        //保存当前选择的颜色
        mPaint.color = HomeViewModel.instance().mColor
        mPaint.strokeWidth = HomeViewModel.instance().mStrokeWidth
        mPaint.style = HomeViewModel.instance().mStrokeStyle
    }
    //设置图形的终点坐标
    fun setEndPoint(x:Float,y:Float){
        endX = x
        endY = y
        //计算中心点
        centerX = (startX + endX)/2
        centerY = (startY + endY)/2
        //矩形区域就确定了 适配左到右 右到左 上到下  下到上 拉动图形
        rectF.left = Math.min(startX, endX)
        rectF.top = Math.min(startY, endY)
        rectF.right = Math.max(startX, endX)
        rectF.bottom = Math.max(startY, endY)
    }

    //具体绘制的方法
    abstract fun draw(canvas: Canvas)
    //判断触摸点是否在当前这个图形内部
    abstract fun containsPoint(x:Float, y:Float):Boolean
}