package com.baidu.infinity.ui.fragment.home.draw

import android.graphics.Bitmap
import androidx.room.util.foreignKeyCheck
import com.baidu.infinity.ui.fragment.home.layer.LayerModelManager
import com.baidu.infinity.ui.fragment.home.view.ShapeState

class LayerManager {
    //保存所有的图层
    private val layers: ArrayList<Layer> = arrayListOf()

    //添加图层
    fun addLayer(width: Int, height: Int){
        Layer(layers.size+1,width, height).apply {
            //新的图层添加在第一个
            layers.add(0,this)

            //修改模型数据
            LayerModelManager.instance.addLayer(this)
        }
    }

    //根据id删除图层
    fun removeLayer(id: Int): Boolean{
        layers.forEach {
            if (it.id == id){
                it.onDestroy()
                layers.remove(it)
                //修改模型数据
                //LayerModelManager.instance.removeLayer(id)
                return true
            }
        }
        return false
    }

    //图层交换
    fun switchLayer(from: Int, target: Int):Boolean{
        val temp = layers[from]
        //用to和from去替换对应位置的对象
        layers[from] = layers[target]
        layers[target] = temp
        return true
    }

    //获取当前选中的layer
    fun getCurrentLayer():Layer?{
        layers.forEach { layer ->
            //查找数据源中选中的layer的id相同的layer
            if (LayerModelManager.instance.getCurrentLayerId() == layer.id){
                return layer
            }
        }
        return null
    }

    //获取所有图层
    fun getLayers(): List<Layer>{
        return layers
    }
    //获取所有图层的Bitmap
    fun getLayersBitmap(): List<Bitmap>{
        val bitmapList = arrayListOf<Bitmap>()
        //注意反序 3 2 1
        //1  2  3
        for (layer in layers.asReversed()){
            bitmapList.add(layer.getBitmap())
        }
        return bitmapList
    }

    //创建图形
    //当手触摸到屏幕 并且 是在绘制形状时 添加图形
    fun addShape(type: ShapeType, startX: Float, startY: Float){
        getCurrentLayer()?.addShape(type, startX, startY)
    }
    //设置当前移动过程中的触摸点
    fun addEndPoint(endX: Float, endY: Float){
        getCurrentLayer()?.addEndPoint(endX, endY)
    }

    //提供给外部统一绘制
    //绘制所有图层
    fun draw(){
        for (layer in layers.asReversed()){
            layer.draw()
        }
    }

    //撤销 删除最后一个图形
    fun undo(){
        getCurrentLayer()?.undo()
    }

    //清空图层
    fun clearLayer(){
        getCurrentLayer()?.clear()
    }

    //修改正在绘制图形的状态
    fun updateShapeState(state: ShapeState){
        getCurrentLayer()?.updateShapeState(state)
    }

    //修改绘制的文本
    fun updateText(text: String){
        getCurrentLayer()?.updateText(text)
    }

    //填充颜色
    fun fillColor(x:Float, y:Float){
        getCurrentLayer()?.fillColor(x,y)
    }

    //移动时选中图形
    fun selectShape(x:Float, y:Float){
        getCurrentLayer()?.selectShape(x,y)
    }

    //通过id找一个图层
    private fun getLayerWidthId(id:Int):Layer?{
        layers.forEach {
            if (it.id == id){
                return it
            }
        }
        return null
    }
}