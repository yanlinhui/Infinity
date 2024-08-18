package com.baidu.infinity.ui.fragment.home.view.bg_image

import com.baidu.infinity.R
import com.baidu.infinity.ui.fragment.home.layer.LayerState

class ImageModelManager {
    private val mImageModels:ArrayList<ImageModel> = arrayListOf()
    private var mLastSelected: ImageModel? = null

    init {
        //第一次读取数据
        mImageModels.add(ImageModel(R.drawable.bg_1))
        mImageModels.add(ImageModel(R.drawable.bg_2))
        mImageModels.add(ImageModel(R.drawable.bg_3))
        mImageModels.add(ImageModel(R.drawable.bg_4))
        mImageModels.add(ImageModel(R.drawable.bg_5))
        mImageModels.add(ImageModel(R.drawable.bg_6))
        mImageModels.add(ImageModel(R.drawable.bg_7))
        mImageModels.add(ImageModel(R.drawable.bg_8))
        mImageModels.add(ImageModel(R.drawable.bg_9))
        mImageModels.add(ImageModel(R.drawable.bg_10))
        mImageModels.add(ImageModel(R.drawable.bg_11))
        mImageModels.add(ImageModel(R.drawable.bg_12))
        mImageModels.add(ImageModel(R.drawable.bg_13))
        mImageModels.add(ImageModel(R.drawable.bg_14))
        mImageModels.add(ImageModel(R.drawable.bg_15))
        mImageModels.add(ImageModel(R.drawable.bg_16))
        mImageModels.add(ImageModel(R.drawable.bg_17))
        mImageModels.add(ImageModel(R.drawable.bg_18))
        mImageModels.add(ImageModel(R.drawable.bg_19))
        mImageModels.add(ImageModel(R.drawable.bg_20))
        mImageModels.add(ImageModel(R.drawable.bg_21))
        mImageModels.add(ImageModel(R.drawable.bg_22))
        mImageModels.add(ImageModel(R.drawable.bg_23))
        mImageModels.add(ImageModel(R.drawable.bg_24))
        mImageModels.add(ImageModel(R.drawable.bg_25))
    }

    fun getModels():List<ImageModel>{
        return mImageModels
    }

    fun select(model: ImageModel){
        if (mLastSelected == null){
            //之前没有选中任何一个
            model.state = LayerState.SELECTED
            mLastSelected = model
        }else{
            //之前有选中的
            if (mLastSelected != model){
                mLastSelected?.state = LayerState.NORMAL
                model.state = LayerState.SELECTED
                mLastSelected = model
            }
        }
    }
}