package com.baidu.infinity.ui.fragment.home.draw

import android.graphics.Bitmap

class LayerManager {
    //保存所有的图层
    private val layers: ArrayList<Layer> = arrayListOf()

    //添加图层
    fun addLayer(width: Int, height: Int){
        Layer(layers.size+1,width, height).apply {
            layers.add(this)
        }
    }

    //根据id删除图层
    fun removeLayer(id: Int): Boolean{
        layers.forEach {
            if (it.id == id){
                layers.remove(it)
                return true
            }
        }
        return false
    }

    //删除最上层图层
    fun removeFirstLayer(): Boolean{
        if (layers.isNotEmpty()){
            layers.removeLast()
            return true
        }
        return false
    }

    //删除所有图层
    fun removeLayers(){
        layers.clear()
    }

    //图层交换
    fun switchLayer(fromId: Int, toId: Int):Boolean{
        //判断两个图层是否存在
        val from = getLayerWidthId(fromId)
        val to = getLayerWidthId(toId)
        if (from != null && to != null){
            //TODO
            //找图层对象的索引值
            val fromIndex = layers.indexOf(from)
            val toIndex = layers.indexOf(to)
            //用to和from去替换对应位置的对象
            layers[fromIndex] = to
            layers[toIndex] = from
            return true
        }else{
            return false
        }
    }
    //获取最上层的图层
    fun getCurrentLayer():Layer?{
        if (layers.isEmpty()) return null
        return layers.last()
    }
    //获取所有图层
    fun getLayers(): List<Layer>{
        return layers
    }
    //获取所有图层的Bitmap
    fun getLayersBitmap(): List<Bitmap>{
        val bitmapList = arrayListOf<Bitmap>()
        layers.forEach { layer ->
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
        layers.forEach { layer ->
            layer.draw()
        }
    }

    //撤销 删除最后一个图形
    fun undo(){
        getCurrentLayer()?.undo()
    }

    //清空图层
    //TODO --
    fun clear(){
        getCurrentLayer()?.clear()
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