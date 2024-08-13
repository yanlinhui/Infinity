package com.baidu.infinity.ui.fragment.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager.LayoutParams
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.baidu.infinity.R
import com.baidu.infinity.databinding.ColorPickerLayoutBinding
import com.baidu.infinity.databinding.FragmentHomeBinding
import com.baidu.infinity.databinding.LayerItemLayoutBinding
import com.baidu.infinity.databinding.LayerPopupViewLayoutBinding
import com.baidu.infinity.model.IconModel
import com.baidu.infinity.ui.base.BaseFragment
import com.baidu.infinity.ui.fragment.home.layer.LayerModel
import com.baidu.infinity.ui.fragment.home.layer.getLayerData
import com.baidu.infinity.ui.fragment.home.view.HSVColorPickerView
import com.baidu.infinity.ui.util.IconState
import com.baidu.infinity.ui.util.OperationType
import com.baidu.infinity.ui.util.dp2px
import com.baidu.infinity.ui.util.dp2pxF
import com.baidu.infinity.ui.util.getDrawToolIconModels
import com.baidu.infinity.ui.util.getHomeMenuIconModels
import com.baidu.infinity.ui.util.getMenuIconModel
import com.baidu.infinity.ui.util.getOperationToolIconModels
import com.baidu.infinity.ui.util.toast
import com.baidu.infinity.viewmodel.HomeViewModel
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setDifferModels
import com.drake.brv.utils.setup
import com.skydoves.colorpickerview.ColorPickerDialog

class HomeFragment: BaseFragment<FragmentHomeBinding>() {
    private val closeBottom: Int by lazy {
        mBinding.drawLayout.top + mBinding.iconMenuView.top
    }
    private val openBottom: Int by lazy {
        mBinding.drawLayout.bottom
    }
    private val closeLeft: Int by lazy {
        mBinding.mainMenuView.right
    }
    private val openLeft: Int by lazy {
        mBinding.mainMenuView.left
    }
    private val hideLeft: Int by lazy {
        mBinding.root.width
    }
    private val showLeft: Int by lazy {
        mBinding.actionMenuView.left
    }

    private var isDrawMenuOpen = true //记录绘制工具menu的打开关闭状态
    private var isArrowRightOpen = true //记录绘制工具menu的打开关闭状态

    //绘制工具展开和收回动画
    private val drawMenuCloseAnim: AnimatorSet by lazy {
        val r = ObjectAnimator.ofFloat(mBinding.menuIconView,"rotation",0f,360f).apply{
            duration = 400
        }
        val m = ObjectAnimator.ofInt(mBinding.drawLayout,"bottom",openBottom,closeBottom).apply {
            duration = 400
        }
        AnimatorSet().apply {
            playTogether(r,m)
        }
    }
    private val drawMenuOpenAnim: AnimatorSet by lazy {
        val r = ObjectAnimator.ofFloat(mBinding.menuIconView,"rotation",360f,0f).apply{
            duration = 400
        }
        val m = ObjectAnimator.ofInt(mBinding.drawLayout,"bottom",closeBottom,openBottom).apply {
            duration = 400
        }
        AnimatorSet().apply {
            playTogether(r,m)
        }
    }
    //主菜单展开收回动画
    private val mainMenuCloseAnim: ObjectAnimator by lazy {
        ObjectAnimator.ofInt(mBinding.mainMenuView,"left",openLeft,closeLeft).apply {
            duration = 400
        }
    }
    private val mainMenuOpenAnim: ObjectAnimator by lazy {
        ObjectAnimator.ofInt(mBinding.mainMenuView,"left",closeLeft,openLeft).apply {
            duration = 400
        }
    }
    //工具操作菜单隐藏和显示动画
    private val actionMenuHideAnim: AnimatorSet by lazy {
        val alpha = ObjectAnimator.ofFloat(mBinding.actionMenuView,"alpha",1f,0f).apply {
            duration = 400
        }
        val move = ObjectAnimator.ofInt(mBinding.actionMenuView,"left",showLeft,hideLeft).apply {
            duration = 400
        }
        AnimatorSet().apply {
            playTogether(move)
        }
    }
    private val actionMenuShowAnim: AnimatorSet by lazy {
        val alpha = ObjectAnimator.ofFloat(mBinding.actionMenuView,"alpha",0f,1f).apply {
            duration = 400
        }
        val move = ObjectAnimator.ofInt(mBinding.actionMenuView,"left",hideLeft,showLeft).apply {
            duration = 400
        }
        AnimatorSet().apply {
            playTogether(move)
        }
    }
    //箭头左右旋转动画
    private val arrowRightAnim: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(mBinding.arrowImageView,"rotation",180f,0f).apply{
            duration = 400
        }
    }
    private val arrowLeftAnim: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(mBinding.arrowImageView,"rotation",0f,180f).apply{
            duration = 400
        }
    }

    //弹出颜色选择器的对象
    private lateinit var mHSVColorPickerView: HSVColorPickerView
    private val mColorPickerPopupWindow: PopupWindow by lazy {
        val colorPickerBinding = ColorPickerLayoutBinding.inflate(layoutInflater)
        mHSVColorPickerView = colorPickerBinding.root
        mHSVColorPickerView.pickColorCallback = { color ->
            HomeViewModel.instance().mColor = color
        }
        PopupWindow(requireContext()).apply {
            contentView = colorPickerBinding.root
            width = LayoutParams.WRAP_CONTENT
            height = LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //isFocusable = true
            //isOutsideTouchable = true
        }
    }

    //弹出一个图层的显示视图
    private var mLayerPopupViewBinding: LayerPopupViewLayoutBinding? = null
    private val mLayerPopupWindow: PopupWindow by lazy {
        mLayerPopupViewBinding = LayerPopupViewLayoutBinding.inflate(layoutInflater)
        mLayerPopupViewBinding!!.addBtn.clickCallback = {
            //添加一个新的图层
            HomeViewModel.instance()
                .mLayerManager
                .addLayer(mBinding.drawView.width, mBinding.drawView.height)
            //刷新
            refreshLayerRecyclerView()
        }
        initLayerRecyclerView()
        PopupWindow(requireContext()).apply {
            contentView = mLayerPopupViewBinding!!.root
            width = LayoutParams.WRAP_CONTENT
            height = LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
    override fun initBinding(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //配置HomeViewModel
        HomeViewModel.init(this)
    }

    override fun initView() {
        //配置绘制工具栏
        mBinding.menuIconView.setIconModel(getMenuIconModel())
        mBinding.iconMenuView.setIcons(getDrawToolIconModels())
        mBinding.actionMenuView.setIcons(getOperationToolIconModels())
        mBinding.mainMenuView.setIcons(getHomeMenuIconModels())
        //监听工具点击事件
        mBinding.iconMenuView.iconClickListener = { type,state ->
            mBinding.drawView.setCurrentDrawType(type)
        }
        //绘制工具栏的menu按钮被点击
        mBinding.menuIconView.clickCallback = {
            //如果有正在做动画 就不响应
            //if (drawMenuCloseAnim.isRunning || drawMenuOpenAnim.isRunning) return@setOnClickListener
            if (isDrawMenuOpen){
                //关闭动画
                drawMenuCloseAnim.start()
                actionMenuHideAnim.start()
                //如果有弹出色盘就隐藏
                hideColorPicker()
                //处理action图标的状态
                mBinding.actionMenuView.resetIconState()
                mBinding.iconMenuView.resetIconState()
                //重置一下当前绘图工具为None
                mBinding.drawView.resetDrawToolType()
            }else{
                drawMenuOpenAnim.start()
                actionMenuShowAnim.start()
            }
            isDrawMenuOpen = !isDrawMenuOpen
        }
        //主菜单arrow箭头点击
        mBinding.arrowImageView.setOnClickListener {
            if (isArrowRightOpen){ //关闭动画
                arrowLeftAnim.start()
                mainMenuCloseAnim.start()
                //隐藏图层视图
                hideLayerView()
                //重置选中图标
                mBinding.mainMenuView.resetIconState()
            }else{ //展开动画
                arrowRightAnim.start()
                mainMenuOpenAnim.start()
            }
            isArrowRightOpen = !isArrowRightOpen
        }
        //配置actionMenu回调事件
        mBinding.actionMenuView.iconClickListener = { type,state ->
            when (type) {
                OperationType.OPERATION_PALETTE -> {
                    //颜色选择器
                    if (state == IconState.NORMAL){
                        hideColorPicker()
                    }else {
                        showColorPicker()
                    }
                }
                else -> {}
            }
        }
        //监听主菜单的事件
        mBinding.mainMenuView.iconClickListener = { type, state ->
            when (type) {
                OperationType.MENU_LAYER ->{
                    //图层
                    if (state == IconState.NORMAL){
                        hideLayerView()
                    }else {
                        showLayerView()
                    }
                }
                else -> {}
            }
        }
        //监听是否应该刷新Layer视图
        mBinding.drawView.refreshLayerListener = {
            refreshLayerRecyclerView()
        }
    }

    //刷新recyclerView
    private fun refreshLayerRecyclerView(){
        if (mLayerPopupViewBinding != null){
            val datas = getLayerData()
            //mBinding.testiv.setImageBitmap(datas.last().bitmap)
            mLayerPopupViewBinding!!.recyclerView.setDifferModels(datas)
        }
    }

    //初始化图层的RecyclerView
    private fun initLayerRecyclerView(){
        mLayerPopupViewBinding!!.recyclerView.linear().setup {
            addType<LayerModel>(R.layout.layer_item_layout)
            //绑定数据 LayerModel和layout中相关联
            onBind {
                //获取item绑定类对象
                val binding = getBinding<LayerItemLayoutBinding>()
                //获取当前这个数据模型
                val data =  getModel<LayerModel>()
                mBinding.testiv.setImageBitmap(data.bitmap)
                //绑定内容
                binding.layerImageView.setImageBitmap(data.bitmap)
            }
        }.models = getLayerData()
    }
    //隐藏图层视图
    private fun hideLayerView(){
        mLayerPopupWindow.dismiss()
    }
    //隐藏颜色选择器
    private fun hideColorPicker(){
        mColorPickerPopupWindow.dismiss()
    }

    //显示图层视图
    private fun showLayerView(){
        mLayerPopupWindow.showAsDropDown(
            mBinding.mainMenuView,
            requireContext().dp2px(100),
            0,
        )
    }
    //显示颜色选择器
    private fun showColorPicker(){
        mColorPickerPopupWindow.showAtLocation(
            mBinding.root,
            Gravity.END,
            mBinding.root.width - mBinding.actionMenuView.left,
            0
        )
    }

}

