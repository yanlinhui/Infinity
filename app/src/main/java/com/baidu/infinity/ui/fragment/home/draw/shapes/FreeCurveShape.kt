package com.baidu.infinity.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.baidu.infinity.ui.fragment.home.draw.BaseShape

/**
 * 圆形
 */
class FreeCurveShape: BaseShape() {
    private val mPath = Path()

    //重写父类方法
    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
        //设置路径的起点
        mPath.moveTo(x,y)
        //修改画笔类型
        mPaint.style = Paint.Style.STROKE
    }

    override fun setEndPoint(x: Float, y: Float) {
        super.setEndPoint(x, y)
        //从上一个点到当前点联接一条线
        mPath.lineTo(x,y)
    }
    override fun draw(canvas: Canvas) {
        //绘制椭圆
        canvas.drawPath(mPath,mPaint)
    }

    override fun containsPoint(x: Float, y: Float): Boolean {
        return true
    }
}