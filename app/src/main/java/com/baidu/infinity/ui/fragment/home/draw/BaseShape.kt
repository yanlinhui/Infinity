package com.baidu.infinity.ui.fragment.home.draw

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import com.baidu.infinity.R
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
    protected var mIsInMoveMode = false //记录move图标是不是被点击了
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

    //获取选中时四个角对应的图形
    //画四个角
    protected val mCornerBitmap:Bitmap by lazy {
        BitmapFactory.decodeResource(
            HomeViewModel.instance().getContext().resources,
            R.drawable.scale_corner
        )
    }
    //四个角默认绘制时离边的距离
    protected val mCornerSize: Float by lazy {
        HomeViewModel.instance().getContext().dp2pxF(12) / 2
    }
    //推拽时响应范围 尽量大一点，不然收的范围感应不到
    private val mReactSize: Float by lazy {
        HomeViewModel.instance().getContext().dp2pxF(16)
    }

    //记录移动时选中的位置
    protected var mMovePosition = MovePosition.NONE
    protected var mMoveStartx = 0f
    protected var mMoveStarty = 0f
    protected var mMoveDx = 0f
    protected var mMoveDy = 0f

    //初始化广播接收器
    //填充颜色
    open fun fillColor(){
        //只是修改画笔的颜色 画笔的style在各自的内部实现
        mPaint.color = HomeViewModel.instance().mColor
    }

    //选中图形
    fun select(){
        mShapeState = ShapeState.SELECT
    }
    //取消选中
    fun unSelect(){
        mShapeState = ShapeState.NORMAL
        //状态值还原
        mMovePosition = MovePosition.NONE

    }

    //确定移动之前的位置
    open fun calculateMovePosition(x: Float,y: Float){
        //记录移动时的触摸起始点
        mMoveStartx = x
        mMoveStarty = y

        if (x in rectF.left-mReactSize..rectF.left+mReactSize && y in rectF.top-mReactSize..rectF.top+mReactSize){
            //左上角
            mMovePosition = MovePosition.TOP_LEFT
        }else if (x in rectF.right-mReactSize..rectF.right+mReactSize && y in rectF.top-mReactSize..rectF.top+mReactSize){
            //右上角
            mMovePosition = MovePosition.TOP_RIGHT
        }else if (x in rectF.left-mReactSize..rectF.left+mReactSize && y in rectF.bottom-mReactSize..rectF.bottom+mReactSize){
            //左下角
            mMovePosition = MovePosition.BOTTOM_LEFT
        }else if (x in rectF.right-mReactSize..rectF.right+mReactSize && y in rectF.bottom-mReactSize..rectF.bottom+mReactSize){
            //右下角
            mMovePosition = MovePosition.BOTTOM_RIGHT
        }else if (x in rectF.left-mReactSize..rectF.left+mReactSize ){
            //左边
            mMovePosition = MovePosition.LEFT
        }else if (y in rectF.top-mReactSize..rectF.top+mReactSize ){
            //上边
            mMovePosition = MovePosition.TOP
        }
        else if (x in rectF.right-mReactSize..rectF.right+mReactSize ){
            //右边
            mMovePosition = MovePosition.RIGHT
        }else if(y in rectF.bottom-mReactSize..rectF.bottom+mReactSize ){
            //底部
            mMovePosition = MovePosition.BOTTOM
        }else {
            //判断是否在中间区域
            mMovePosition = MovePosition.CENTER
        }
    }

    //设置当前图形所处的状态
    fun updateShapeState(state: ShapeState){
        mShapeState = state
    }

    //修改是否进入Move模式
    fun updateMoveMode(isInMoveMode: Boolean){
        mIsInMoveMode = isInMoveMode
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
        //矩形区域就确定了 适配左到右 右到左 上到下  下到上 拉动图形
        when (mMovePosition){
            MovePosition.NONE -> { //正常绘制
                //判断是不是在Move模式
                if (mIsInMoveMode) return

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

                //矩形区域也跟着偏移
                rectF.offset(mMoveDx,mMoveDy)
            }
            MovePosition.LEFT ->{
                moveLeft(x,y)
            }
            MovePosition.RIGHT ->{
                moveRight(x,y)
            }
            MovePosition.TOP ->{
                moveTop(x,y)
            }
            MovePosition.BOTTOM ->{
                moveBottom(x,y)
            }
            MovePosition.TOP_LEFT ->{
                moveLeft(x,y)
                moveTop(x,y)
            }
            MovePosition.TOP_RIGHT ->{
                moveRight(x,y)
                moveTop(x,y)
            }
            MovePosition.BOTTOM_LEFT ->{
                moveLeft(x,y)
                moveBottom(x,y)
            }
            MovePosition.BOTTOM_RIGHT ->{
                moveRight(x,y)
                moveBottom(x,y)
            }
        }
        //计算中心点
        centerX = (startX + endX)/2
        centerY = (startY + endY)/2

    }

    private fun moveLeft(x: Float, y:Float){
        //计算当前移动的距离
        mMoveDx = x - mMoveStartx
        //判断起点和终点的大小关系
        if (startX < endX){ //从左往右
            startX += mMoveDx
        }else{  //从右往左
            endX += mMoveDx
        }
        //当前这个点就是下一个点的起点
        mMoveStartx = x
        //矩形区域也跟着偏移
        rectF.left += mMoveDx
    }
    private fun moveTop(x: Float, y:Float){
        //计算当前移动的距离
        mMoveDy = y - mMoveStarty
        //判断起点和终点的大小关系
        if (startY < endY){ //从上到下
            startY += mMoveDy
        }else{  //从下到上
            endY += mMoveDy
        }
        //当前这个点就是下一个点的起点
        mMoveStarty = y
        //矩形区域也跟着偏移
        rectF.top += mMoveDy
    }
    private fun moveRight(x: Float, y:Float){
        //计算当前移动的距离
        mMoveDx = x - mMoveStartx
        //判断起点和终点的大小关系
        if (startX < endX){ //从左往右
            endX += mMoveDx
        }else{  //从右往左
            startX += mMoveDx
        }
        //当前这个点就是下一个点的起点
        mMoveStartx = x
        //矩形区域也跟着偏移
        rectF.right += mMoveDx
    }
    private fun moveBottom(x: Float, y:Float){
        //计算当前移动的距离
        mMoveDy = y - mMoveStarty
        //判断起点和终点的大小关系
        if (startY < endY){ //从上到下
            endY += mMoveDy
        }else{  //从下到上
            startY += mMoveDy
        }
        //当前这个点就是下一个点的起点
        mMoveStarty = y
        //矩形区域也跟着偏移
        rectF.bottom += mMoveDy
    }
    //具体绘制的方法
    open fun draw(canvas: Canvas){
        //统一给每个图形绘制矩形边框+四个角
        if (mShapeState == ShapeState.SELECT){
            //画矩形
            canvas.drawRect(rectF,mOutlinePaint)
            //左上角
            canvas.drawBitmap(mCornerBitmap, rectF.left - mCornerSize, rectF.top - mCornerSize, null)
            //右上角
            canvas.drawBitmap(mCornerBitmap, rectF.right - mCornerSize, rectF.top - mCornerSize, null)
            //左下角
            canvas.drawBitmap(mCornerBitmap, rectF.left - mCornerSize, rectF.bottom - mCornerSize, null)
            //右下角
            canvas.drawBitmap(mCornerBitmap, rectF.right - mCornerSize, rectF.bottom - mCornerSize, null)
        }
    }

    //判断触摸点是否在当前这个图形的Path内部
    //填充颜色时使用
    open fun containsPointInPath(x:Float, y:Float):Boolean{
        //路径对应的区域
        val pathRegion = Region()
        //起点到终点的区域 就是我们的裁剪区域
        val clipRegion = Region(
            rectF.left.toInt(),
            rectF.top.toInt(),
            rectF.right .toInt(),
            rectF.bottom.toInt(),
        )
        //将path路径转化为 path的矩形区域
        pathRegion.setPath(mPath,clipRegion)
        //判断点是否在region内部
        return pathRegion.contains(x.toInt(),y.toInt())
    }

    //判断触摸点是否在矩形区域内部
    open fun containsPointInRect(x: Float, y: Float):Boolean{
        //创建包含选择区域的rect
        val outRectF = RectF().apply {
            left = rectF.left - mCornerSize
            top = rectF.top - mCornerSize
            right = rectF.right + mCornerSize
            bottom = rectF.bottom + mCornerSize
        }
        return outRectF.contains(x,y)
    }

    //定义一个枚举 用于表示移动之前点击的位置
    enum class MovePosition{
        NONE,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        LEFT,
        TOP,
        RIGHT,
        BOTTOM,
        CENTER
    }
}