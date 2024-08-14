package com.baidu.infinity.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import com.baidu.infinity.ui.fragment.home.draw.BaseShape

/**
 * 圆形
 */
class LineShape: BaseShape() {
    override fun draw(canvas: Canvas) {
        //绘制椭圆
        canvas.drawLine(startX,startY,endX,endY,mPaint)
    }

    override fun containsPoint(x: Float, y: Float): Boolean {
        return true
    }
}