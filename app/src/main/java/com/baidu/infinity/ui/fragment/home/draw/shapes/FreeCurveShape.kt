package com.baidu.infinity.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.graphics.Paint
import com.baidu.infinity.ui.fragment.home.draw.BaseShape

/**
 * 圆形
 */
class FreeCurveShape: BaseShape() {

    //重写父类方法
    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)

        rectF.left = x
        rectF.right = x
        rectF.bottom = y
        rectF.top = y

        //设置路径的起点
        mPath.moveTo(x,y)
        //修改画笔类型
        mPaint.style = Paint.Style.STROKE
    }

    override fun setEndPoint(x: Float, y: Float) {
        when (mMovePosition) {
            MovePosition.NONE -> { //正常绘制
                //判断是不是在Move模式
                if (mIsInMoveMode) return
                //绘制过程中计算出 最左最右最上最下区域
                rectF.left = Math.min(rectF.left,x)
                rectF.right = Math.max(rectF.right,x)
                rectF.bottom = Math.max(rectF.bottom,y)
                rectF.top = Math.min(rectF.top,y)
            }

            MovePosition.CENTER -> {
                //将之前的矩形区域的中心点移到当前的中心点
                mMoveDx = x - mMoveStartx
                mMoveDy = y - mMoveStarty

                //矩形区域也跟着偏移
                rectF.offset(mMoveDx, mMoveDy)
                mPath.offset(mMoveDx, mMoveDy)

                mMoveStartx = x
                mMoveStarty = y
            }
            else -> {}
        }

        //从上一个点到当前点联接一条线
        if (mIsInMoveMode) return
        mPath.lineTo(x,y)
    }

    override fun draw(canvas: Canvas) {
        //绘制椭圆
        canvas.drawPath(mPath,mPaint)
        //先让父类统一绘制选中时的边框
        super.draw(canvas)
    }
}