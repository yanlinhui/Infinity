package com.baidu.infinity.ui.fragment.home.draw

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log

/**
 * 管理图层
 */
class Layer(val id: Int,val width:Int, val height:Int) {
    //这个图层对应的canvas对象
    private var mCanvas: Canvas
    //这个图层绘制的形状都在mBitmap中
    private var mBitmap: Bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
    //记录当前这一层绘制的所有形状
    private val mShapes: ArrayList<BaseShape> = arrayListOf()

    init {
        mCanvas = Canvas(mBitmap)
    }

    //获取当前图层的bitmap对象
    fun getBitmap():Bitmap{
        return mBitmap
    }

    //当手触摸到屏幕 并且 是在绘制形状时 添加图形
    fun addShape(type: ShapeType, startX: Float, startY: Float){
        when (type){
            ShapeType.Circle -> {//创建圆形
                CircleShape().apply {
                    //设置起始点坐标
                    setStartPoint(startX,startY)
                    //保存这个图形
                    mShapes.add(this)
                }
            }
            else -> {}
        }
    }
    //设置当前移动过程中的触摸点
    fun addEndPoint(endX: Float, endY: Float){
        currentShape().setEndPoint(endX,endY)
    }

    //提供给外部统一绘制
    fun draw(){
        mBitmap.eraseColor(Color.TRANSPARENT)
        mShapes.forEach { shape ->
            shape.draw(mCanvas)
        }
    }

    //撤销 删除最后一个图形
    fun undo(){
        if (mShapes.isNotEmpty()){
            mShapes.removeLast()
        }
    }

    //清空图层
    //TODO --
    fun clear(){
        mShapes.clear()
    }

    //获取当前正在操作的图形
    private fun currentShape():BaseShape{
        return mShapes.last()
    }


}