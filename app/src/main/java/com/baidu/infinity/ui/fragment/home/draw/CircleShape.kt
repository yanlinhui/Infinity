package com.baidu.infinity.ui.fragment.home.draw

import android.graphics.Canvas
import com.baidu.infinity.viewmodel.HomeViewModel

/**
 * 圆形
 */
class CircleShape: BaseShape() {
    override fun draw(canvas: Canvas) {
        //绘制椭圆
        canvas.drawOval(rectF,mPaint)
    }

    override fun containsPoint(x: Float, y: Float): Boolean {
        return true
    }
}