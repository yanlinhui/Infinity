package com.baidu.infinity.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Region
import com.baidu.infinity.ui.fragment.home.draw.ArrowPath
import com.baidu.infinity.ui.fragment.home.draw.BaseShape
import com.baidu.infinity.ui.util.dp2pxF
import com.baidu.infinity.viewmodel.HomeViewModel

/**
 * 圆形
 */
class LocationShape: BaseShape() {
    private val mArrowLength = HomeViewModel.instance().getContext().dp2pxF(10)

    //重写父类方法
    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
        //修改画笔类型
        mPaint.style = Paint.Style.STROKE
    }

    override fun setEndPoint(x: Float, y: Float) {
        super.setEndPoint(x, y)
        mPath.reset()
        //绘制箭头
        //x轴
//        ArrowPath.addArrowToPath(
//            mPath,
//            Math.min(startX,endX),
//            (startY + endY)/2,
//            Math.max(startX,endX),
//            (startY + endY)/2,
//            mArrowLength
//        )
//        //y轴
//        ArrowPath.addArrowToPath(
//            mPath,
//            (startX + endX)/2,
//            Math.max(startY, endY),
//            (startX + endX)/2,
//            Math.min(startY, endY),
//            mArrowLength
//        )
        ArrowPath.addArrowToPath(
            mPath,
            rectF.left,
            (rectF.top+rectF.bottom)/2,
            rectF.right,
            (rectF.top+rectF.bottom)/2,
            mArrowLength
        )
        //y轴
        ArrowPath.addArrowToPath(
            mPath,
            (rectF.left + rectF.right)/2,
            rectF.bottom,
            (rectF.left + rectF.right)/2,
            rectF.top,
            mArrowLength
        )
    }
    override fun draw(canvas: Canvas) {
        //绘制椭圆
        canvas.drawPath(mPath,mPaint)
        //先让父类统一绘制选中时的边框
        super.draw(canvas)
    }


    //tan 比例转度数
    private fun rate2degree(rate: Double):Double{
        //计算弧度
        val radians = Math.atan(rate)
        //计算度数
        val degree = Math.toDegrees(radians)
        return degree
    }

    //sin 度数转比例
    private fun degree2rateSin(degree: Double):Double{
        val radians = Math.toRadians(degree)
        val rate  = Math.sin(radians)
        return rate
    }
    //cos
    private fun degree2rateCos(degree: Double):Double{
        val radians = Math.toRadians(degree)
        val rate  = Math.cos(radians)
        return rate
    }
}