package com.baidu.infinity.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.baidu.infinity.ui.fragment.home.draw.BaseShape
import com.baidu.infinity.ui.fragment.home.view.ShapeState

/**
 * 圆形
 */
class RectangleShape: BaseShape() {
    override fun draw(canvas: Canvas) {
        //绘制矩形
        canvas.drawRect(rectF,mPaint)
        //先让父类统一绘制选中时的边框
        super.draw(canvas)
    }

    override fun fillColor() {
        super.fillColor()
        //修改为实心模式
        mPaint.style = Paint.Style.FILL
    }

    override fun containsPointInPath(x: Float, y: Float): Boolean {
        return rectF.contains(x,y)
    }
}