package com.baidu.infinity.ui.fragment.home.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Region
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.baidu.infinity.ui.fragment.home.draw.LayerManager
import com.baidu.infinity.ui.fragment.home.draw.ShapeType
import com.baidu.infinity.ui.util.OperationType
import com.baidu.infinity.ui.util.toast
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

    //记录接收者对象 （匿名类对象）
    private val mTextColorChangeReceiver:BroadcastReceiver by lazy {
        object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                //刷新文字的颜色
                refreshTextColor()
            }
        }

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //这个视图添加到窗口上时 注册广播
        //定义广播的类型
        val intentFilter = IntentFilter(BroadCastCenter.TEXT_COLOR_CHANGE_NAME)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.applicationContext.registerReceiver(
                mTextColorChangeReceiver,
                intentFilter,
                Context.RECEIVER_EXPORTED
            )
        }else{
            context.applicationContext.registerReceiver(
                mTextColorChangeReceiver,
                intentFilter
            )
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //视图从窗口中分离时，取消注册广播
        context.unregisterReceiver(mTextColorChangeReceiver)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //创建默认的图层
        layerManager.addLayer(measuredWidth,measuredHeight)
    }

    //颜色改变时，如果当前时文本，需要刷新界面
    fun refreshTextColor(){
        if (mDrawShapeType == ShapeType.Text && mTextState == TextSate.EDITING){
            invalidate()
        }
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
                        if (mDrawShapeType == ShapeType.Text && mTextState == TextSate.EDITING){
                            //取消文字的编辑
                            //编辑完毕
                            //退出编辑状态
                            //隐藏键盘
                            addShowKeyboardListener(false)
                            //退出编辑状态
                            mTextState = TextSate.NONE
                            //修改文字图形的状态
                            layerManager.updateShapeState(ShapeState.NORMAL)
                            invalidate()
                        }else {
                            layerManager.addShape(mDrawShapeType, event.x, event.y)
                            //修改图形所处的状态
                            //确保这个图形已经添加了
                            layerManager.updateShapeState(ShapeState.DRAWING)

                            //如果是文本就弹出键盘
                            if (mDrawShapeType == ShapeType.Text) {
                                //判断当前文本的状态
                                if (mTextState == TextSate.NONE) {
                                    //现在需要绘制文本
                                    //弹出键盘
                                    addShowKeyboardListener(true)
                                    //进入文本的编辑状态
                                    mTextState = TextSate.EDITING
                                }
                            }
                        }
                    }
                    ActionType.FILL -> {
                        layerManager.fillColor(event.x, event.y)
                        invalidate()
                    }
                    ActionType.MOVE -> {
                        layerManager.selectShape(event.x, event.y)
                        invalidate()
                    }
                    else -> {}
                }
            }
            MotionEvent.ACTION_MOVE ->{
                when (mActionType){
                    ActionType.DRAW,ActionType.MOVE -> {
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
                //修改当前图形的编辑状态为正常
                //文字的状态不在这里修改，除了文字都需要在这里修改状态
                if (mDrawShapeType != ShapeType.Text && mActionType != ActionType.MOVE) {
                    layerManager.updateShapeState(ShapeState.NORMAL)
                    invalidate()
                }
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
            OperationType.NONE -> {
                mActionType = ActionType.NONE
                mDrawShapeType = ShapeType.NONE
                mTextState = TextSate.NONE
            }
            OperationType.DRAW_MENU -> {
                mActionType = ActionType.NONE
                mDrawShapeType = ShapeType.NONE
                mTextState = TextSate.NONE
            }
            OperationType.DRAW_MOVE -> {
                mActionType = ActionType.MOVE
                mDrawShapeType = ShapeType.NONE
                mTextState = TextSate.NONE
            }
            OperationType.DRAW_BRUSH -> {
                mActionType = ActionType.FILL
                mDrawShapeType = ShapeType.NONE
                mTextState = TextSate.NONE
            }
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
                    OperationType.DRAW_ERASER -> mDrawShapeType = ShapeType.Eraser
                    else -> mDrawShapeType = ShapeType.NONE
                }
            }
        }
    }
}