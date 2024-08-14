package com.baidu.infinity.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.baidu.infinity.ui.fragment.home.draw.BaseShape

/**
 * 圆形
 */
class TriangleShape: BaseShape() {
    private val mPath = Path()

    //重写父类方法
    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
    }

    override fun setEndPoint(x: Float, y: Float) {
        super.setEndPoint(x, y)
        //清空路径 重新绘制
        mPath.reset()
        //设置三角形的起点
        mPath.moveTo(startX,endY)
        mPath.lineTo(x,y)
        mPath.lineTo((startX+x)/2f,startY)
    }
    override fun draw(canvas: Canvas) {
        //绘制椭圆
        canvas.drawPath(mPath,mPaint)
    }

    override fun containsPoint(x: Float, y: Float): Boolean {
        return true
    }
}