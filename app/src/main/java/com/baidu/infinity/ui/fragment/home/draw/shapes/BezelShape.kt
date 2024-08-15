package com.baidu.infinity.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.baidu.infinity.ui.fragment.home.draw.BaseShape

/**
 * 圆形
 */
class BezelShape: BaseShape() {
    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
        mPaint.style = Paint.Style.STROKE
    }

    override fun setEndPoint(x: Float, y: Float) {
        super.setEndPoint(x, y)
        //清空路径 重新绘制
        mPath.reset()

        //设置拉伸点x1 x2 x3
        val space = Math.abs(startX-endX)*0.382f
        //计算最左边的起始点x
        val sx = Math.min(startX,endX)
        val ex = Math.max(endX,startX)
        //计算一个波峰的高度
        val height = Math.abs(startY-endY)
        //设置贝塞尔曲线的起始点
        mPath.moveTo(sx,(startY + endY)/2)
        mPath.cubicTo(
            sx + space,
            Math.min(startY,endY) - height ,
            ex - space,
            Math.max(startY,endY) + height,
            ex,
            (startY + endY)/2
        )
    }
    override fun draw(canvas: Canvas) {
        //绘制椭圆
        canvas.drawPath(mPath,mPaint)
        //先让父类统一绘制选中时的边框
        super.draw(canvas)
    }

}