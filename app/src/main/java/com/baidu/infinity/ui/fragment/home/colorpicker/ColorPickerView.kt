package com.baidu.infinity.ui.fragment.home.colorpicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.baidu.infinity.ui.util.dp2px
import com.baidu.infinity.ui.util.dp2pxF

class ColorPickerView(
    context: Context,
    attrs: AttributeSet? = null
): View(context, attrs) {
    private var defaultWidth = dp2px(200)
    private var defaultHeight = dp2px(200)
    private var centerX = 0f
    private var centerY = 0f
    private var mColorPickerRadius = 0f
    private var mColorPickerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }
    //颜色渐变方式为 扇形渐变
    private lateinit var mSweepGradient: SweepGradient

    //选取颜色的圆
    private var mTouchX = 0f
    private var mTouchY = 0f
    private val mSelectRadius = dp2pxF(20)
    private val mSelectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        setShadowLayer(20f,0f,0f,Color.BLACK)
    }

    //记录色相值
    private var mHue = 0f
    //饱和度
    private var mSturation = 1f
    //明度
    private var mLightness = 1f
    //记录当前选中的颜色
    private var mSelectedColor: Int = Color.BLACK
    //使用高阶函数回调数据
    private var mCallBack:(Int)->Unit = {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = MeasureSpec.getSize(widthMeasureSpec)
        val widthmode = MeasureSpec.getMode(widthMeasureSpec)
        if (widthmode != MeasureSpec.EXACTLY){
            width = defaultWidth
        }

        var height = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (widthmode != MeasureSpec.EXACTLY){
            height = defaultHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = measuredWidth/2f
        centerY = measuredHeight/2f
        mColorPickerRadius = Math.min(measuredWidth,measuredHeight)/2f
        mSweepGradient = SweepGradient(
            centerX,
            centerY,
            intArrayOf(
                0xFFFF0000.toInt(),
                0xFFFFFF00.toInt(),
                0xFF00FF00.toInt(),
                0xFF00FFFF.toInt(),
                0xFF0000FF.toInt(),
                0xFFFF00FF.toInt(),
                0xFFFF0000.toInt(),
            ),
            floatArrayOf(
                1f/6 * 0,
                1f/6 * 1,
                1f/6 * 2,
                1f/6 * 3,
                1f/6 * 4,
                1f/6 * 5,
                1.0f
            )
        )
        mColorPickerPaint.shader = mSweepGradient

        mTouchX = centerX
        mTouchY = centerY
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(centerX,centerY,mColorPickerRadius,mColorPickerPaint)

        //将HSV转化为Color
        mSelectedColor = Color.HSVToColor(floatArrayOf(mHue,mSturation,mLightness))
        //随时将最新的颜色回传
        mCallBack(mSelectedColor)

        mSelectPaint.color = mSelectedColor
        canvas.drawCircle(mTouchX,mTouchY,mSelectRadius,mSelectPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action){
            MotionEvent.ACTION_DOWN,MotionEvent.ACTION_MOVE ->{
                mTouchX = event.x
                mTouchY = event.y

                //计算色相值 0-360
                //计算触摸点的角度
                val radians = Math.atan2((mTouchY-centerY).toDouble(),(mTouchX-centerX).toDouble())
                //将弧度转化为角度
                var degree = Math.toDegrees(radians)
                if (degree < 0){
                    degree = 360 - Math.abs(degree)
                }
                mHue = degree.toFloat()
                invalidate()
            }
            MotionEvent.ACTION_UP ->{
                mCallBack(mSelectedColor)
            }
        }
        return true
    }

    //获取颜色
    fun getCurrentColor():Int{
        return mSelectedColor
    }

    //监听颜色选中事件
    fun addPickColorListener(listener:(Int)->Unit){
        mCallBack = listener
    }
    //设置饱和度
    fun setSaturation(value: Float){
        mSturation = value
        invalidate()
    }
    //设置亮度
    fun setLightness(value: Float){
        mLightness = value
        invalidate()
    }
}










