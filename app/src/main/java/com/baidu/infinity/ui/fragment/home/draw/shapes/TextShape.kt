package com.baidu.infinity.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.text.TextPaint
import android.util.Log
import android.util.TypedValue
import com.baidu.infinity.R
import com.baidu.infinity.ui.fragment.home.draw.BaseShape
import com.baidu.infinity.ui.util.dp2pxF
import com.baidu.infinity.viewmodel.HomeViewModel

/**
 * 圆形
 */
class TextShape: BaseShape() {
    //绘制一个矩形边框
    private val mBorderPath = Path()
    private val mBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = HomeViewModel.instance().getContext().dp2pxF(1)
        color = HomeViewModel.instance().getContext().resources.getColor(R.color.light_blue,null)
        style = Paint.Style.STROKE
    }

    //绘制文本的画笔
    private val mTextPaint = TextPaint().apply {
        color = HomeViewModel.instance().mColor
        textSize = HomeViewModel.instance().mTextSize
    }

    //记录每一行组成的数组
    private var mTextLines:List<String> = emptyList()
    //记录一行的高度
    private var oneLineHeight = 0f
    //文本内容
    private var mText:String = ""

    //计算绘制中心点
    private var cy = 0f

    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
        rectF.top = y
        rectF.left = x
        rectF.right = x
        rectF.bottom = y
    }


    //处理文本的尺寸
    private fun changeBorderSize(){
        //支持换行
        //将传递过来的字符串用 \n 分割一下
        mTextLines = mText.split('\n')
        if (mTextLines.isEmpty()) return

        val metrics = mTextPaint.fontMetrics

        //获取文本尺寸
        //计算最宽的哪一行的宽度
        var maxWidth = 0f
        mTextLines.forEach { line ->
            val w = mTextPaint.measureText(line)
            maxWidth = Math.max(maxWidth, w)
        }

        oneLineHeight =  metrics.bottom - metrics.top
        val height = oneLineHeight * mTextLines.size

        //矩形区域
        val space = (rectF.height() - height)/2
        rectF.top = rectF.top + space - mPadding
        rectF.right = rectF.left + 2*mPadding + maxWidth
        rectF.bottom = rectF.top + height + 2*mPadding

        //修改绘制矩形
        mBorderPath.reset()
        //绘制边框矩形区域
        mBorderPath.addRect(rectF,Path.Direction.CW)

        //计算文字baseline 到 中心点的距离
        val offsetY = (metrics.descent - metrics.ascent)/2 - metrics.descent
        //计算中心坐标
        cy = rectF.top + mPadding + oneLineHeight/2 + offsetY
    }

    //上下左右间距
    private val mPadding = HomeViewModel.instance().getContext().dp2pxF(5)

    override fun setEndPoint(x: Float, y: Float) {
        super.setEndPoint(x, y)
        mBorderPath.reset()

        //绘制边框矩形区域
        mBorderPath.addRect(rectF,Path.Direction.CW)
    }

    //更新文本
    fun updateText(text: String){
        mText = text
        //文本确定的时候 内容的宽度和高度确定了
        changeBorderSize()
    }

    override fun draw(canvas: Canvas) {
        //绘制边框
        canvas.drawPath(mBorderPath,mBorderPaint)
        //绘制文本
        //修改颜色
        mTextPaint.color = HomeViewModel.instance().mColor
        mTextLines.forEachIndexed { index, line ->
            canvas.drawText(line,rectF.left+mPadding,cy + index*oneLineHeight,mTextPaint)
        }
    }

    override fun containsPoint(x: Float, y: Float): Boolean {
        return true
    }
}