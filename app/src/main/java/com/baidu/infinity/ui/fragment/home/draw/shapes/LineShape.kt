package com.baidu.infinity.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import com.baidu.infinity.ui.fragment.home.draw.BaseShape

/**
 * 圆形
 */
class LineShape: BaseShape() {
    override fun setEndPoint(x: Float, y: Float) {
        super.setEndPoint(x, y)
        mPath.reset()
        mPath.moveTo(startX,startY)
        mPath.lineTo(endX,endY)
    }
    override fun draw(canvas: Canvas) {
        //绘制椭圆
        //mPaint.strokeWidth = 100f
        canvas.drawPath(mPath,mPaint)
    }

    override fun containsPoint(x: Float, y: Float): Boolean {
        val tolerance = mPaint.strokeWidth
        val d1 = distance(startX, startY, x, y)
        val d2 = distance(endX, endY, x, y)
        val lineLen = distance(startX, startY, endX, endY)
        return Math.abs(d1 + d2 - lineLen) <= tolerance
    }

    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return Math.sqrt(((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)).toDouble()).toFloat()
    }
}













