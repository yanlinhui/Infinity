package com.baidu.infinity.ui.fragment.home.draw

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.baidu.infinity.ui.fragment.home.draw.shapes.ArrowLineShape
import com.baidu.infinity.ui.fragment.home.draw.shapes.BezelShape
import com.baidu.infinity.ui.fragment.home.draw.shapes.CircleShape
import com.baidu.infinity.ui.fragment.home.draw.shapes.EraserShape
import com.baidu.infinity.ui.fragment.home.draw.shapes.FreeCurveShape
import com.baidu.infinity.ui.fragment.home.draw.shapes.LineShape
import com.baidu.infinity.ui.fragment.home.draw.shapes.LocationShape
import com.baidu.infinity.ui.fragment.home.draw.shapes.RectangleShape
import com.baidu.infinity.ui.fragment.home.draw.shapes.TextShape
import com.baidu.infinity.ui.fragment.home.draw.shapes.TriangleShape
import com.baidu.infinity.ui.fragment.home.view.ShapeState

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
        var tShape: BaseShape? = null
        tShape = when (type){
            ShapeType.Circle -> {//创建圆形
                CircleShape()
            }
            ShapeType.Rectangle ->{ //矩形
                RectangleShape()
            }
            ShapeType.Line ->{ //直线
                LineShape()
            }
            ShapeType.Curve ->{//随意画
                FreeCurveShape()
            }
            ShapeType.Triangle ->{//三角形
                TriangleShape()
            }
            ShapeType.Bezel ->{ //贝塞尔曲线
                BezelShape()
            }
            ShapeType.Arrow ->{ //箭头
                ArrowLineShape()
            }
            ShapeType.Location ->{ //画坐标轴
                LocationShape()
            }
            ShapeType.Text ->{ //绘制文本
                TextShape()
            }
            ShapeType.Eraser -> { //橡皮擦
                EraserShape()
            }
            else -> {null}
        }

        tShape?.let {
            //设置起始点坐标
            it.setStartPoint(startX,startY)
            //保存这个图形
            mShapes.add(it)
        }

    }
    //修改正在绘制图形的状态
    fun updateShapeState(state: ShapeState){
        currentShape().updateShapeState(state)
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

    //跟新内容
    fun updateText(text: String){
        //拿到绘制文本的shape
        val textShape = currentShape() as TextShape
        textShape.updateText(text)
    }

    //填充颜色
    fun fillColor(x:Float, y:Float){
        //倒叙遍历数组
        for (shape in mShapes.asReversed()){
            if (shape.containsPoint(x,y)){
                shape.fillColor()
            }
        }
    }

    //移动时选中图形
    fun selectShape(x:Float, y:Float){
        //倒叙遍历数组
        for (shape in mShapes.asReversed()){
            if (shape.containsPoint(x,y)){
                shape.selectShape()
            }
        }
    }


    //获取当前正在操作的图形
    private fun currentShape():BaseShape{
        return mShapes.last()
    }


}