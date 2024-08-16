package com.baidu.infinity.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.util.Log
import com.baidu.infinity.ui.fragment.home.draw.BaseShape
import com.baidu.infinity.ui.fragment.home.view.ShapeState
import com.baidu.infinity.ui.util.toast
import com.baidu.infinity.viewmodel.HomeViewModel

/**
 * 圆形
 */
class LineShape: BaseShape() {
    //记录左边的点是不是起点
    private var leftIsStart = true

    override fun calculateMovePosition(x: Float, y: Float) {
        //记录移动时的触摸起始点
        mMoveStartx = x
        mMoveStarty = y

        //定死左边是起点还是终点
        leftIsStart = startX < endX

        //确定左右的坐标
        var leftx = 0f
        var rightx = 0f
        var lefty = 0f
        var righty = 0f

        if (startX < endX){
            leftx = startX
            lefty = startY
            rightx = endX
            righty = endY
        }else{
            leftx = endX
            lefty = endY
            rightx = startX
            righty = startY
        }

        if (x in leftx - mCornerSize.. leftx + mCornerSize && y in lefty - mCornerSize..lefty+mCornerSize){
            mMovePosition = MovePosition.LEFT
        }else if (x in rightx - mCornerSize.. rightx + mCornerSize && y in righty - mCornerSize..righty+mCornerSize){
            mMovePosition = MovePosition.RIGHT
        }else{
            mMovePosition = MovePosition.CENTER
        }

        HomeViewModel.instance().getContext().toast("$mMovePosition")
    }
    override fun setEndPoint(x: Float, y: Float) {
        //矩形区域就确定了 适配左到右 右到左 上到下  下到上 拉动图形
        when (mMovePosition) {
            MovePosition.NONE -> { //正常绘制
                endX = x
                endY = y
                rectF.left = Math.min(startX, endX)
                rectF.top = Math.min(startY, endY)
                rectF.right = Math.max(startX, endX)
                rectF.bottom = Math.max(startY, endY)
            }

            MovePosition.CENTER -> {
                //将之前的矩形区域的中心点移到当前的中心点
                mMoveDx = x - mMoveStartx
                mMoveDy = y - mMoveStarty
                //修改起始点坐标
                startX += mMoveDx
                startY += mMoveDy
                //修改终点的坐标
                endX += mMoveDx
                endY += mMoveDy
                //当前这个点就是下一个点的起点
                mMoveStartx = x
                mMoveStarty = y
            }
            MovePosition.LEFT ->{
                if (leftIsStart){
                    startX = x
                    startY = y
                }else {
                    endX = x
                    endY = y
                }
            }
            MovePosition.RIGHT ->{
                if (leftIsStart){
                    endX = x
                    endY = y
                }else {
                    startX = x
                    startY = y
                }
            }
            else -> {}
        }

        mPath.reset()
        mPath.moveTo(startX,startY)
        mPath.lineTo(endX,endY)
    }
    override fun draw(canvas: Canvas) {
        //绘制两端的选择点
        if (mShapeState == ShapeState.SELECT) {
            //在起点画一个矩形
            canvas.drawBitmap(
                mCornerBitmap,
                startX - mCornerSize,
                startY - mCornerSize,
                null
            )
            //在终点画一个矩形
            canvas.drawBitmap(
                mCornerBitmap,
                endX - mCornerSize,
                endY - mCornerSize,
                null
            )
        }

        canvas.drawPath(mPath,mPaint)
    }

    override fun containsPointInPath(x: Float, y: Float): Boolean {
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













