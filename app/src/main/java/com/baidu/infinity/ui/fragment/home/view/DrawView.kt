package com.baidu.infinity.ui.fragment.home.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.baidu.infinity.ui.fragment.home.draw.LayerManager
import com.baidu.infinity.ui.fragment.home.draw.ShapeType
import com.baidu.infinity.ui.util.OperationType
import com.baidu.infinity.viewmodel.HomeViewModel

class DrawView(
    context: Context,
    attrs: AttributeSet?
): View(context, attrs) {
    //记录绘图工具
    private var mDrawShapeType: ShapeType = ShapeType.NONE
    //记录操作类型
    private var mActionType: ActionType = ActionType.NONE
    //获取图层管理器
    private val layerManager:LayerManager by lazy {
        HomeViewModel.instance().mLayerManager
    }
    //刷新事件
    var refreshLayerListener:()->Unit = {}

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //创建默认的图层
        layerManager.addLayer(measuredWidth,measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        //每个图层负责把自己图层的内容绘制到Bitmap中
        layerManager.draw()

        //将这些Bitmap内容绘制到这个view上
        layerManager.getLayersBitmap().forEach { bitmap ->
            //在view上绘制这个图画
            canvas.drawBitmap(bitmap,0f,0f,null)
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action){
            MotionEvent.ACTION_DOWN ->{
                //确定在做什么操作1.颜色填充 2.橡皮擦 3.移动 4.双指缩放 5.绘图
                when (mActionType){
                    ActionType.DRAW -> {
                        layerManager.addShape(mDrawShapeType,event.x, event.y)
                    }
                    else -> {}
                }
            }
            MotionEvent.ACTION_MOVE ->{
                when (mActionType){
                    ActionType.DRAW -> {
                        layerManager.addEndPoint(event.x, event.y)
                        invalidate()
                    }
                    else -> {}
                }

            }
            /**
             HomeViewModel-> mLayerManager
             HomeFragment -> loadData
             */
            MotionEvent.ACTION_UP ->{
                //当前绘制完毕，提醒外部刷新layer的数据
                refreshLayerListener()
            }
        }
        return true
    }

    /**
     * 重置当前绘图工具为 NONE
     */
    fun resetDrawToolType(){
        mActionType = ActionType.NONE
    }
    //设置当前选中的绘图工具类型
    fun setCurrentDrawType(type: OperationType){
        when (type){
            OperationType.DRAW_MENU -> mActionType = ActionType.NONE
            OperationType.DRAW_MOVE -> mActionType = ActionType.MOVE
            OperationType.DRAW_ERASER -> mActionType = ActionType.ERASER
            else -> {
                mActionType = ActionType.DRAW
                when (type) {
                    OperationType.DRAW_CIRCLE -> mDrawShapeType = ShapeType.Circle
                    else -> mDrawShapeType = ShapeType.NONE
                }
            }
        }
    }
}