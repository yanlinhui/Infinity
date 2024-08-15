package com.baidu.infinity.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import com.baidu.infinity.ui.fragment.home.draw.BaseShape
import com.baidu.infinity.ui.fragment.home.view.ShapeState
import com.baidu.infinity.ui.util.dp2pxF
import com.baidu.infinity.viewmodel.HomeViewModel

/**
 * 圆形
 */
class EraserShape: BaseShape() {
    //橡皮擦的圆
    private val mEraserCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#806375FE")
        style = Paint.Style.STROKE
    }
    private var mCircleCenterX = 0f
    private var mCircleCenterY = 0f

    //橡皮擦默认的尺寸
    private var mEraserSize = HomeViewModel.instance().getContext().dp2pxF(10)

    //重写父类方法
    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
        //橡皮擦的中心点
        mCircleCenterX = x
        mCircleCenterY = y
        //设置路径的起点
        mPath.moveTo(x,y)
        //修改画笔类型
        mPaint.style = Paint.Style.STROKE
        //修改画笔的混合模式
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        mPaint.strokeWidth = HomeViewModel.instance().getContext().dp2pxF(10)
    }

    override fun setEndPoint(x: Float, y: Float) {
        super.setEndPoint(x, y)
        //从上一个点到当前点联接一条线
        mPath.lineTo(x,y)

        //橡皮擦的中心点
        mCircleCenterX = x
        mCircleCenterY = y
    }

    override fun draw(canvas: Canvas) {
        //绘制路径
        canvas.drawPath(mPath,mPaint)

        //绘制橡皮擦的圆
        if (mShapeState == ShapeState.DRAWING) {
            //在擦除过程中 才需要绘制这个圆
            canvas.drawCircle(mCircleCenterX, mCircleCenterY, mEraserSize / 2f, mEraserCirclePaint)
        }
    }
}