package com.baidu.infinity.ui.fragment.home.draw

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import com.baidu.infinity.ui.fragment.home.view.ShapeState
import com.baidu.infinity.ui.util.dp2pxF
import com.baidu.infinity.viewmodel.HomeViewModel

/**
 * 绘制图形的抽象类
 * 定义起点
 * 终点
 * 矩形区域
 * 绘制方法
 * 判断是否在区域内
 */
abstract class BaseShape {
    protected var startX:Float = 0f
    protected var startY:Float = 0f
    protected var endX:Float = 0f
    protected var endY:Float = 0f
    protected var mShapeState = ShapeState.NORMAL //记录图形状态

    //绘制图形的path
    protected val mPath = Path()
    //中心点
    var centerX:Float = 0f
    var centerY:Float = 0f
    //矩形区域
    val rectF: RectF = RectF()
    //话图形的画笔画笔
    val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = HomeViewModel.instance().mStrokeWidth
        color = HomeViewModel.instance().mColor
        style = HomeViewModel.instance().mStrokeStyle
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    //画选中时边框的画笔
    val mOutlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#6375FE")
        strokeWidth = HomeViewModel.instance().getContext().dp2pxF(2)
        style = Paint.Style.STROKE
    }

    //填充颜色
    open fun fillColor(){
        //只是修改画笔的颜色 画笔的style在各自的内部实现
        mPaint.color = HomeViewModel.instance().mColor
    }

    //选中图形
    fun selectShape(){
        if (mShapeState == ShapeState.NORMAL){
            mShapeState = ShapeState.SELECT
        }else{
            mShapeState = ShapeState.NORMAL
        }
    }

    //设置当前图形所处的状态
    fun updateShapeState(state: ShapeState){
        mShapeState = state
    }

    //设置图形起始坐标
    open fun setStartPoint(x:Float,y:Float){
        startX = x
        startY = y
        //保存当前选择的颜色
        mPaint.color = HomeViewModel.instance().mColor
        mPaint.strokeWidth = HomeViewModel.instance().mStrokeWidth
        mPaint.style = HomeViewModel.instance().mStrokeStyle
    }
    //设置图形的终点坐标
    open fun setEndPoint(x:Float,y:Float){
        endX = x
        endY = y
        //计算中心点
        centerX = (startX + endX)/2
        centerY = (startY + endY)/2
        //矩形区域就确定了 适配左到右 右到左 上到下  下到上 拉动图形
        rectF.left = Math.min(startX, endX)
        rectF.top = Math.min(startY, endY)
        rectF.right = Math.max(startX, endX)
        rectF.bottom = Math.max(startY, endY)
    }


    //具体绘制的方法
    open fun draw(canvas: Canvas){
        //统一给每个图形绘制矩形边框+四个角
        if (mShapeState == ShapeState.SELECT){
            canvas.drawRect(rectF,mOutlinePaint)
        }
    }

    //判断触摸点是否在当前这个图形内部
    open fun containsPoint(x:Float, y:Float):Boolean{
        //路径对应的区域
        val pathRegion = Region()
        //起点到终点的区域 就是我们的裁剪区域
        val clipRegion = Region(
            rectF.left.toInt(),
            rectF.top.toInt(),
            rectF.right.toInt(),
            rectF.bottom.toInt(),
        )
        //将path路径转化为 path的矩形区域
        pathRegion.setPath(mPath,clipRegion)
        //判断点是否在region内部
        return pathRegion.contains(x.toInt(),y.toInt())
    }
}