package com.baidu.infinity.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.baidu.infinity.ui.fragment.home.draw.BaseShape

/**
 * 圆形
 */
class CircleShape: BaseShape() {
    override fun setEndPoint(x: Float, y: Float) {
        super.setEndPoint(x, y)
        mPath.reset()
        mPath.addOval(rectF, Path.Direction.CW)
    }

    override fun draw(canvas: Canvas) {
        //绘制椭圆
        canvas.drawPath(mPath,mPaint)
        //先让父类统一绘制选中时的边框
        super.draw(canvas)
    }

    override fun fillColor() {
        super.fillColor()
        //修改为实心模式
        mPaint.style = Paint.Style.FILL
    }
}