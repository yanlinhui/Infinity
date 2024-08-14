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
    //回调显示键盘的消息
    var addShowKeyboardListener:(Boolean)->Unit = {}

    //记录文本的输入状态
    private var mTextState = TextSate.NONE

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //创建默认的图层
        layerManager.addLayer(measuredWidth,measuredHeight)
    }

    //刷新
    fun refresh(){
        invalidate()
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
                        //如果是文本就弹出键盘
                        if (mDrawShapeType == ShapeType.Text){
                            //判断当前文本的状态
                            if (mTextState == TextSate.NONE){
                                //现在需要绘制文本
                                //弹出键盘
                                addShowKeyboardListener(true)
                                //进入文本的编辑状态
                                mTextState = TextSate.EDITING
                            }else{
                                //编辑完毕
                                //退出编辑状态
                                //隐藏键盘
                                addShowKeyboardListener(false)
                                //退出编辑状态
                                mTextState = TextSate.NONE
                            }
                        }
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
     * 接收实时文本
     */
    fun refreshText(text: String){
        //显示文本
        HomeViewModel.instance().mLayerManager.updateText(text)
        //重绘
        invalidate()
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
            OperationType.NONE -> mActionType = ActionType.NONE
            OperationType.DRAW_MENU -> mActionType = ActionType.NONE
            OperationType.DRAW_MOVE -> mActionType = ActionType.MOVE
            OperationType.DRAW_ERASER -> mActionType = ActionType.ERASER
            else -> {
                mActionType = ActionType.DRAW
                when (type) {
                    OperationType.DRAW_CIRCLE -> mDrawShapeType = ShapeType.Circle
                    OperationType.DRAW_RECTANGLE -> mDrawShapeType = ShapeType.Rectangle
                    OperationType.DRAW_LINE -> mDrawShapeType = ShapeType.Line
                    OperationType.DRAW_CURVE -> mDrawShapeType = ShapeType.Curve
                    OperationType.DRAW_TRIANGLE -> mDrawShapeType = ShapeType.Triangle
                    OperationType.DRAW_BEZEL -> mDrawShapeType = ShapeType.Bezel
                    OperationType.DRAW_LINE_ARROW -> mDrawShapeType = ShapeType.Arrow
                    OperationType.DRAW_LOCATION -> mDrawShapeType = ShapeType.Location
                    OperationType.DRAW_TEXT -> mDrawShapeType = ShapeType.Text
                    else -> mDrawShapeType = ShapeType.NONE
                }
            }
        }
    }
}