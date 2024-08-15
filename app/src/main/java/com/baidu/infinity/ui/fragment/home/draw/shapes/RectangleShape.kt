package com.baidu.infinity.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.graphics.Paint
import com.baidu.infinity.ui.fragment.home.draw.BaseShape

/**
 * 圆形
 */
class RectangleShape: BaseShape() {
    override fun draw(canvas: Canvas) {
        //绘制椭圆
        canvas.drawRect(rectF,mPaint)
        //先让父类统一绘制选中时的边框
        super.draw(canvas)
    }

    override fun fillColor() {
        super.fillColor()
        //修改为实心模式
        mPaint.style = Paint.Style.FILL
    }

    override fun containsPoint(x: Float, y: Float): Boolean {
        return rectF.contains(x,y)
    }
}