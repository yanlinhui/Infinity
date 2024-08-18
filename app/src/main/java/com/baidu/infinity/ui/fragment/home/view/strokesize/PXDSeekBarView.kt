package com.baidu.infinity.ui.fragment.home.view.strokesize

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.baidu.infinity.R
import com.baidu.infinity.ui.util.dp2pxF

class PXDSeekBarView(
    context: Context,
    attrs: AttributeSet? = null
): View(context, attrs){

    private var mMin: Int = 0
    private var mMax: Int = 0
    private var mOrientation = Orientation.VERTICAL
    private var mProgressBarWidth = dp2pxF(8)
    private var mDotSize = dp2pxF(26)
    private var mDefaultHeight = dp2pxF(100)
    private var mProgressBackgroundColor = Color.parseColor("#D1D1D1")
    private var mProgressColor = Color.parseColor("#6375FE")
    private var mDotColor = Color.WHITE
    private var mPadding = dp2pxF(1)
    var addProgressChangeListener:(Int)->Unit = {}
    var addTouchStateListener:(Boolean)->Unit = {}

    private var mProgress: Int = 0
        set(value) {
            field = value
            mTextWidth = mTextPaint.measureText("$value")
            addProgressChangeListener(value)
        }

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val mBgRect = RectF()
    private val mProgressRect = RectF()
    private var mCx = 0f
    private var mCy = 0f

    private val mTextPaint = TextPaint().apply {
        color = Color.BLACK
        textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            14f,
            resources.displayMetrics
        )
    }
    private var mOffset = 0f
    private var mTextWidth = 0f

    init {
        //解析自定义的属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PXDSeekBarView)
        mMin = typedArray.getInteger(R.styleable.PXDSeekBarView_min, 1)
        mMax = typedArray.getInteger(R.styleable.PXDSeekBarView_max, 10)
        mProgress = typedArray.getInteger(R.styleable.PXDSeekBarView_progress, 1)
        val value = typedArray.getInteger(R.styleable.PXDSeekBarView_orientation, 0)
        mOrientation = if (value == 0) Orientation.VERTICAL else Orientation.HORIZONTAL
        typedArray.recycle()

        if (mProgress < mMin){
            mProgress = mMin
        }else if (mProgress > mMax){
            mProgress = mMax
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        if (widthMode != MeasureSpec.EXACTLY){
            if (mOrientation == Orientation.VERTICAL) {
                widthSize = (mDotSize + mPadding * 2).toInt()
            }else{
                widthSize = mDefaultHeight.toInt()
            }
        }

        var heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (heightMode != MeasureSpec.EXACTLY){
            if (mOrientation == Orientation.VERTICAL) {
                heightSize = mDefaultHeight.toInt()
            }else{
                heightSize = (mDotSize + mPadding * 2).toInt()
            }
        }

        setMeasuredDimension(widthSize,heightSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //确定背景的矩形区域
        if (mOrientation == Orientation.VERTICAL){
            val hspace = (measuredWidth - mProgressBarWidth )/2
            mBgRect.apply {
                left = hspace
                top = 0f
                right = measuredWidth - hspace
                bottom = measuredHeight.toFloat()
            }
            mProgressRect.apply {
                left = hspace
                top = 0f
                right = measuredWidth - hspace
                bottom = mProgress.toFloat() / (mMax - mMin)  * measuredHeight
            }
            //中心点坐标
            mCx = measuredWidth / 2f
            mCy = mProgressRect.bottom
        }else{
            val vspace = (measuredHeight - mProgressBarWidth )/2
            mBgRect.apply {
                left = 0f
                top = vspace
                right = measuredWidth.toFloat()
                bottom = measuredHeight - vspace
            }
            mProgressRect.apply {
                left = 0f
                top = vspace
                right = mProgress.toFloat() / (mMax-mMin) * measuredWidth
                bottom = measuredHeight - vspace
            }
            //中心点坐标
            mCx = mProgressRect.right
            mCy = measuredHeight / 2f
        }

        val fontMetrics = mTextPaint.fontMetrics
        mOffset = (fontMetrics.descent - fontMetrics.ascent)/2 - fontMetrics.descent
    }

    override fun onDraw(canvas: Canvas) {
        //绘制背景进度槽
        mPaint.color = mProgressBackgroundColor
        canvas.drawRoundRect(mBgRect,mProgressBarWidth/2,mProgressBarWidth/2,mPaint)
        //绘制进度
        mPaint.color = mProgressColor
        canvas.drawRoundRect(mProgressRect,mProgressBarWidth/2,mProgressBarWidth/2,mPaint)
        //绘制圆
        if (mOrientation == Orientation.VERTICAL){
            if (mProgressRect.bottom <= mDotSize/2){
                mCy = mDotSize/2
            }else if (mProgressRect.bottom >= height - mDotSize/2){
                mCy = height - mDotSize/2
            }else{
                mCy = mProgressRect.bottom
            }
        }else{
            if (mProgressRect.right <= mDotSize/2){
                mCx = mDotSize/2
            }else if (mProgressRect.right >= width - mDotSize/2){
                mCx = width - mDotSize/2
            }else{
                mCx = mProgressRect.right
            }
        }
        mPaint.color = mDotColor
        canvas.drawCircle(mCx,mCy,mDotSize/2f,mPaint)
        //绘制文本
        canvas.drawText("$mProgress",mCx-mTextWidth/2,mCy+mOffset,mTextPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action){
            MotionEvent.ACTION_DOWN,MotionEvent.ACTION_MOVE ->{
                if (mOrientation == Orientation.VERTICAL){
                    if (event.y in 0f..measuredHeight.toFloat()) {
                        mProgressRect.bottom = event.y
                        mProgress = ((event.y / measuredHeight) * (mMax-mMin)).toInt() + 1
                        addTouchStateListener(true)
                        invalidate()
                    }
                }else{
                    if (event.x in 0f..measuredWidth.toFloat()) {
                        mProgressRect.right = event.x
                        mProgress = ((event.x / measuredWidth) * (mMax-mMin)).toInt() + 1
                        addTouchStateListener(true)
                        invalidate()
                    }
                }

            }
            MotionEvent.ACTION_UP ->{
                addTouchStateListener(false)
            }
        }
        return true
    }
    enum class Orientation{
        HORIZONTAL,
        VERTICAL
    }
}