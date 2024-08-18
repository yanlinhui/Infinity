package com.baidu.infinity.ui.fragment.home.view.bg_image

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager.LayoutParams
import android.widget.PopupWindow
import androidx.annotation.DrawableRes
import com.baidu.infinity.R
import com.baidu.infinity.databinding.LayerItemLayoutBinding
import com.baidu.infinity.databinding.PickimagePopupViewLayoutBinding
import com.baidu.infinity.ui.fragment.home.layer.LayerState
import com.bumptech.glide.Glide
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup

/**
 * 将PopupWindow封装在内部
 * 方便外部调用
 * 降低耦合性
 */
class PickBackgroundImagePopupWindow(val context: Context) {
    private var mBinding: PickimagePopupViewLayoutBinding? = null
    private val mImageModelManager = ImageModelManager()
    var addImageSelectListener:(Int)->Unit = {}

    private val popupWindow:PopupWindow by lazy {
        val inflater = LayoutInflater.from(context)
        mBinding = PickimagePopupViewLayoutBinding.inflate(inflater)
        initRecyclerView()
        PopupWindow(context).apply {
            contentView = mBinding!!.root
            width = LayoutParams.WRAP_CONTENT
            height = LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    //show
    fun showAsDropDown(view: View, offsetX:Int = 0, offsetY: Int = 0){
        popupWindow.showAsDropDown(view,offsetX,offsetY)
    }
    fun showAtLocation(parent: View, gravity: Int = Gravity.CENTER, offsetX: Int = 0, offsetY: Int = 0){
        popupWindow.showAtLocation(parent,gravity,offsetX,offsetY)
    }
    //hide
    fun hide(){
        popupWindow.dismiss()
    }
    //配置recyclerView BRV
    private fun initRecyclerView(){
        mBinding?.apply {
            recyclerView.linear().setup {
                addType<ImageModel>(R.layout.layer_item_layout)
                onBind {
                    val binding = getBinding<LayerItemLayoutBinding>()
                    val model = getModel<ImageModel>()
                    Glide.with(binding.root)
                        .load(model.id)
                        .into(binding.ivLayer)
                    //配置coverView是否显示
                    binding.coverView.visibility = if (model.state == LayerState.SELECTED)
                        View.VISIBLE
                    else
                        View.INVISIBLE
                    //配置点击事件
                    binding.root.setOnClickListener {
                        mImageModelManager.select(model)
                        addImageSelectListener(model.id)
                        refreshRecyclerView()
                    }
                }
            }.models = mImageModelManager.getModels()
        }
    }

    private fun refreshRecyclerView(){
        mBinding!!.recyclerView.models = mImageModelManager.getModels()
    }
}