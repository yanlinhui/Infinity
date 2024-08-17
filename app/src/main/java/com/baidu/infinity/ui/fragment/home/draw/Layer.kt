package com.baidu.infinity.ui.fragment.home.draw

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
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
import com.baidu.infinity.ui.fragment.home.view.BroadCastCenter
import com.baidu.infinity.ui.fragment.home.view.ShapeState
import com.baidu.infinity.viewmodel.HomeViewModel

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
    //记录当前选中正在编辑的图形对象
    private var mLastSelectedShape: BaseShape? = null

    private val mIconClickReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                if (mLastSelectedShape != null){
                    mLastSelectedShape?.unSelect()
                    mLastSelectedShape = null
                }
            }
        }
    }

    init {
        mCanvas = Canvas(mBitmap)

        val intentFilter = IntentFilter(BroadCastCenter.ICON_CLICK_BROADCAST_NAME)
        //注册广播
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            HomeViewModel.instance()
                .getContext()
                .registerReceiver(mIconClickReceiver,intentFilter, Context.RECEIVER_EXPORTED)
        }else{
            HomeViewModel.instance()
                .getContext()
                .registerReceiver(mIconClickReceiver,intentFilter)
        }
    }

    fun onDestroy(){
        //取消注册
        HomeViewModel.instance().getContext().unregisterReceiver(mIconClickReceiver)
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
            if (shape.containsPointInPath(x,y)){
                shape.fillColor()
            }
        }
    }

    //修改模式
    fun updateMoveMode(isInMoveMode: Boolean){
        mShapes.forEach { shape ->
            shape.updateMoveMode(isInMoveMode)
        }
    }

    //移动时选中图形
    fun selectShape(x:Float, y:Float){
        //获取触摸点对应的图形
        val selectedShape = findSelectedShape(x,y)
        if (selectedShape == null){
            //点击在空白区域
            if (mLastSelectedShape != null){
                //之前选中某个图形，现在需要取消选中
                mLastSelectedShape?.unSelect()
                //修改值
                mLastSelectedShape = null
            }
        }else{
            //点击在某个图形中
            //判断之前是否有选中的图形
            if (mLastSelectedShape == null){
                //选中当前这个图形  第一次选中
                selectedShape.select()
                mLastSelectedShape = selectedShape
            }else{
                //判断是不是同一个图形 同一个不做任何事情
                if (mLastSelectedShape != selectedShape){
                    //取消之前的
                    mLastSelectedShape?.unSelect()
                    //选中现在的
                    selectedShape.select()
                    mLastSelectedShape = selectedShape
                }else{
                    //第二次选中
                    selectedShape.calculateMovePosition(x,y)
                }
            }
        }
    }

    //获取触摸点在当前图层里面的哪个Shape中
    private fun findSelectedShape(x:Float, y:Float):BaseShape?{
        //倒叙遍历数组
        for (shape in mShapes.asReversed()){
            if (shape.containsPointInRect(x,y)){
                return shape
            }
        }
        //没有点击在某个图形中 点击在空白区域
        return null
    }


    //获取当前正在操作的图形
    private fun currentShape():BaseShape{
        //先判断是不是有当前选中的
        if (mLastSelectedShape != null) return mLastSelectedShape!!

        return mShapes.last()
    }

}