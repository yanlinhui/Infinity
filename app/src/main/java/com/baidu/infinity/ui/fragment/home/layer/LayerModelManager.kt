package com.baidu.infinity.ui.fragment.home.layer

import android.util.Log
import com.baidu.infinity.ui.fragment.home.draw.Layer


/**
 * 管理图层模型数据
 */
class LayerModelManager private constructor(){
    //准备一个容器 用于保存所有的图层模型
    private val dataList:ArrayList<LayerModel> = arrayListOf()
    //记录之前选中的图层
    private var mLastSelectedLayerModel: LayerModel? = null

    companion object{
        val instance: LayerModelManager by lazy { LayerModelManager() }
    }

    /**
     * 加载所有图层数据
     */
    fun getLayerModels(): List<LayerModel> {
        return dataList
    }

    /**
     * 获取当前图层的ID
     */
    fun getCurrentLayerId(): Int{
        return mLastSelectedLayerModel!!.id
    }

    //添加一个图层
    fun addLayer(layer: Layer){
        //创建模型数据
        val layerModel = LayerModel(layer.id,layer.getBitmap(),LayerState.SELECTED)
        //改变之前选中的状态
        mLastSelectedLayerModel?.state = LayerState.NORMAL
        //插入数据
        dataList.add(0,layerModel)

        mLastSelectedLayerModel = layerModel
    }

    //修改滑动删除之后
    //应该默认操作哪一个
    // a b c
    // 0 1 2 3

    //如果删除的不是当前选中的图层，就不需要处理
    fun resetCurrentSelected(index: Int, model:LayerModel){
        //判断删除的这个layer是不是默认选中的layer
        if (model.id != mLastSelectedLayerModel!!.id) return

        //判断之前删除的是不是最后一个
        //如果删除元素的编号和当前剩余个数相同 那么表名之前删除的是最后一个
        if (index == dataList.size){
            if (dataList.isNotEmpty()){
                //如果删完之后还有内容，就默认第一个是选中状态
                mLastSelectedLayerModel = dataList.first()
                mLastSelectedLayerModel!!.state = LayerState.SELECTED
            }else{
                mLastSelectedLayerModel = null
            }
        }else{
            //删除的不是最后一个，直接让它的下一个作为默认选中
            //下一个的编号和删除的元素的编号一致
            mLastSelectedLayerModel = dataList[index]
            mLastSelectedLayerModel!!.state = LayerState.SELECTED
        }
    }

    //删除图层
    fun removeLayer(id: Int){
        //如果只有一个图层 就不能删除
        if (dataList.size == 1) return

        dataList.forEachIndexed { index, layerModel ->
            if (layerModel.id == id){
                //判断这个图层是不是当前选中的图层
                if (layerModel.state == LayerState.SELECTED){
                    if (index == dataList.size - 1){
                        //如果是最后一个 那么让第一个变成选中状态
                        mLastSelectedLayerModel = dataList.first()
                        mLastSelectedLayerModel!!.state = LayerState.SELECTED
                    }else{
                        //如果不是最后一个 就让下一个成为选中状态
                        mLastSelectedLayerModel = dataList[index+1]
                        mLastSelectedLayerModel!!.state = LayerState.SELECTED
                    }
                }
                //删除模型数据
                dataList.remove(layerModel)
                return
            }
        }
    }

    //交换图层
    fun switchLayer(source: Int, dest: Int){
        val temp = dataList[source]
        dataList[source] = dataList[dest]
        dataList[dest] = temp
    }

    //选中新的一个图层
    fun selectLayer(model: LayerModel){
        if (mLastSelectedLayerModel != model){
            //取消之前的选中状态
            mLastSelectedLayerModel!!.state = LayerState.NORMAL
            //选中当前这个
            model.state = LayerState.SELECTED
            mLastSelectedLayerModel = model
        }
    }
}
