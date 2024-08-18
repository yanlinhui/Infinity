package com.baidu.infinity.ui.fragment.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.TimeUtils
import android.view.Gravity
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager.LayoutParams
import android.widget.PopupWindow
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.baidu.infinity.R
import com.baidu.infinity.databinding.ColorPickerLayoutBinding
import com.baidu.infinity.databinding.FragmentHomeBinding
import com.baidu.infinity.databinding.LayerItemLayoutBinding
import com.baidu.infinity.databinding.LayerPopupViewLayoutBinding
import com.baidu.infinity.databinding.StrokeBarVewLayoutBinding
import com.baidu.infinity.model.IconModel
import com.baidu.infinity.ui.base.BaseFragment
import com.baidu.infinity.ui.fragment.home.draw.LayerManager
import com.baidu.infinity.ui.fragment.home.layer.LayerModel
import com.baidu.infinity.ui.fragment.home.layer.LayerModelManager
import com.baidu.infinity.ui.fragment.home.layer.LayerState
import com.baidu.infinity.ui.fragment.home.view.BroadCastCenter
import com.baidu.infinity.ui.fragment.home.view.HSVColorPickerView
import com.baidu.infinity.ui.fragment.home.view.LoadingView
import com.baidu.infinity.ui.fragment.home.view.bg_image.PickBackgroundImagePopupWindow
import com.baidu.infinity.ui.fragment.home.view.strokesize.StrokeBarView
import com.baidu.infinity.ui.util.IconState
import com.baidu.infinity.ui.util.OperationType
import com.baidu.infinity.ui.util.TimeUtil
import com.baidu.infinity.ui.util.delayTask
import com.baidu.infinity.ui.util.dp2px
import com.baidu.infinity.ui.util.dp2pxF
import com.baidu.infinity.ui.util.getDrawToolIconModels
import com.baidu.infinity.ui.util.getHomeMenuIconModels
import com.baidu.infinity.ui.util.getMenuIconModel
import com.baidu.infinity.ui.util.getOperationToolIconModels
import com.baidu.infinity.ui.util.toast
import com.baidu.infinity.viewmodel.HomeViewModel
import com.drake.brv.BindingAdapter
import com.drake.brv.listener.DefaultItemTouchCallback
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setDifferModels
import com.drake.brv.utils.setup
import com.skydoves.colorpickerview.ColorPickerDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            mBinding.drawView.refreshTextColor()
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

    private val mStrokeBarPopupWindow: PopupWindow by lazy {
        val barView = StrokeBarView(requireContext())
        PopupWindow(requireContext()).apply {
            contentView = barView
            width = LayoutParams.WRAP_CONTENT
            height = LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
    //选择背景图片的弹窗
    private val pickImagePopupWindow: PickBackgroundImagePopupWindow by lazy {
        PickBackgroundImagePopupWindow(requireContext()).apply {
            addImageSelectListener = { resId ->
                mBinding.drawView.changeBackgroundImage(resId)
            }
        }
    }
    //加载动画
    private val mLoadingView: LoadingView by lazy {
        LoadingView(requireContext())
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
            //发送按钮点击的广播 取消选中状态
            sendUnselectShapeBroadCast()

            if (type == OperationType.DRAW_MOVE){
                //更新move模式
                HomeViewModel.instance()
                    .mLayerManager
                    .updateMoveMode(state == IconState.SELECTED)
            }
            if (state == IconState.NORMAL) {
                mBinding.drawView.setCurrentDrawType(OperationType.NONE)
            }else{
                mBinding.drawView.setCurrentDrawType(type)
            }
        }
        //绘制工具栏的menu按钮被点击
        mBinding.menuIconView.clickCallback = {
            //发送按钮点击的广播 取消选中状态
            sendUnselectShapeBroadCast()

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
            //发送按钮点击的广播 取消选中状态
            sendUnselectShapeBroadCast()

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
            //发送按钮点击的广播 取消选中状态
            sendUnselectShapeBroadCast()

            when (type) {
                OperationType.OPERATION_PALETTE -> {
                    //颜色选择器
                    if (state == IconState.NORMAL){
                        hideColorPicker()
                    }else {
                        showColorPicker()
                    }
                }
                OperationType.OPERATION_UNDO ->{
                    //重置按钮的状态
                    delayTask(200){
                        mBinding.actionMenuView.resetIconState()
                    }

                    //撤销
                    //修改当前layer对应的shapes 从中移除
                    HomeViewModel.instance().mLayerManager.undo()
                    //刷新DrawView
                    mBinding.drawView.refresh()
                    //刷新图层视图
                    refreshLayerRecyclerView()
                }
                OperationType.OPERATION_DELETE ->{
                    //重置按钮的状态
                    delayTask(200){
                        mBinding.actionMenuView.resetIconState()
                    }

                    //撤销
                    //修改当前layer对应的shapes 从中移除
                    HomeViewModel.instance().mLayerManager.clearLayer()
                    //刷新DrawView
                    mBinding.drawView.refresh()
                    //刷新图层视图
                    refreshLayerRecyclerView()
                }
                OperationType.OPERATION_PENCIL-> {
                    if (state == IconState.NORMAL){
                        hideStrokeBarView()
                    }else {
                        showStokeBarView()
                    }
                }
                else -> {}
            }
        }
        //监听主菜单的事件
        mBinding.mainMenuView.iconClickListener = { type, state ->
            //发送按钮点击的广播 取消选中状态
            sendUnselectShapeBroadCast()

            when (type) {
                OperationType.MENU_LAYER ->{
                    //图层
                    if (state == IconState.NORMAL){
                        hideLayerView()
                    }else {
                        showLayerView()
                    }
                }
                OperationType.MENU_PICTURE ->{
                    if (state == IconState.NORMAL){
                        pickImagePopupWindow.hide()
                    }else{
                        pickImagePopupWindow.showAsDropDown(
                            mBinding.mainMenuView,
                            requireContext().dp2px(100)
                        )
                    }
                }
                OperationType.MENU_DOWNLOAD ->{ //下载到本地相册
                    //隐藏弹窗的内容
                    hideLayerView()
                    pickImagePopupWindow.hide()

                    saveDrawViewToAlbum()
                    delayTask(200){
                        mBinding.mainMenuView.resetIconState()
                    }
                }
                else -> {}
            }
        }
        //监听是否应该刷新Layer视图
        mBinding.drawView.refreshLayerListener = {
            refreshLayerRecyclerView()
        }

        //监听输入框的内容改变的事件
        mBinding.edInput.addTextChangedListener(afterTextChanged = {
            //将现有的文本拿给DrawView显示
            mBinding.drawView.refreshText(it.toString())
        })
        //监听drawView上要绘制文本的事件
        mBinding.drawView.addShowKeyboardListener = { isShow ->
            if (isShow) {
                //让输入框获得焦点
                mBinding.edInput.requestFocus()
                //弹出键盘
                showKeyboard()
            }else{
                //隐藏
                mBinding.edInput.clearFocus()
                hideKeyboard()
                //清空文本内容
                mBinding.edInput.text.clear()
            }
        }
    }

    private fun saveDrawViewToAlbum() {
        //将DrawView上所有的背景Bitmap和图层Bitmap绘制到一个同意的Bitmap中
        //insert into userTable age=30
        //uri 需要定位哪个应用的那张表   Pictures/
        //contentValues 插入的字段名和数据
        //MediaStore: 媒体 Images Video Audio

        //显示加载
        mLoadingView.show(mBinding.root)

        lifecycleScope.launch {
            //生成图片
            mBinding.drawView.getBitmap().collect{ bitmap ->
                //下载本地
                //定位图片在系统相册里面的位置
                val imagesUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                }else{
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                //确定插入的数据和对应的字段
                val contentValues = ContentValues().apply {
                    //确定名字 注意唯一
                    put(MediaStore.Images.Media.DISPLAY_NAME,TimeUtil.getTimeName())
                    put(MediaStore.Images.Media.WIDTH,"${bitmap.width}")
                    put(MediaStore.Images.Media.HEIGHT,"${bitmap.height}")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                }
                //获取即将插入的图片在系统相册里面最准确的内容地址uri
                val imgUri = requireContext().contentResolver.insert(imagesUri, contentValues)
                imgUri?.let {
                    //获取对应输出流，
                    requireContext().contentResolver.openOutputStream(imgUri)?.use {
                        //将bitmap通过这个输出流写入imgUri指定的位置
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,it)

                        //关闭加载动画
                        delayTask(1000){
                            mLoadingView.hide()
                        }

                    }
                }
            }
        }

    }

    //弹出键盘
    private fun showKeyboard(){
        val insetsController = WindowCompat.getInsetsController(requireActivity().window,mBinding.edInput)
        insetsController.show(WindowInsetsCompat.Type.ime())
    }
    private fun hideKeyboard(){
        val insetsController = WindowCompat.getInsetsController(requireActivity().window,mBinding.edInput)
        insetsController.hide(WindowInsetsCompat.Type.ime())
    }
    //刷新recyclerView
    private fun refreshLayerRecyclerView(){
        if (mLayerPopupViewBinding != null){
            val datas = LayerModelManager.instance.getLayerModels()
            mLayerPopupViewBinding!!.recyclerView.models = datas
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
                //绑定内容
                binding.ivLayer.setImageBitmap(data.bitmap)
                //设置边框是否显示
                binding.coverView.visibility = if (data.state == LayerState.NORMAL){
                    View.INVISIBLE
                }else{
                    View.VISIBLE
                }
                //添加点击事件
                binding.root.setOnClickListener {
                    //修改数据源
                    LayerModelManager.instance.selectLayer(data)
                    //刷新界面
                    refreshLayerRecyclerView()
                }
            }

            //处理拖拽和滑动事件
            itemTouchHelper = ItemTouchHelper(object : DefaultItemTouchCallback(){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    super.onSwiped(viewHolder, direction)
                    //获取索引值
                    val index = viewHolder.layoutPosition
                    //获取对应的数据模型
                    val model = (viewHolder as BindingAdapter.BindingViewHolder).getModel<LayerModel>()
                    //删除数据源中对应的数据
                    LayerModelManager.instance.resetCurrentSelected(index,model)
                    //删除对应的layer图层
                    HomeViewModel.instance().mLayerManager.removeLayer(model.id)
                    //刷新数据
                    refreshLayerRecyclerView()
                    //DrawView重新绘制
                    mBinding.drawView.refresh()
                }

                override fun onDrag(
                    source: BindingAdapter.BindingViewHolder,
                    target: BindingAdapter.BindingViewHolder
                ) {
                    super.onDrag(source, target)
                    //获取交换的对象的索引值
                    val sourceIndex = source.layoutPosition
                    val targetIndex = target.layoutPosition
                    //获取模型数据
                    val sLayerModel = source.getModel<LayerModel>()
                    val tLayerModel = target.getModel<LayerModel>()

                    //不需要修改数据，BRV自动实现了
                    //LayerModelManager.instance.switchLayer(sourceIndex,targetIndex)
                    //修改图层
                    HomeViewModel.instance().mLayerManager.switchLayer(sourceIndex,targetIndex)
                    //刷新DrawView
                    mBinding.drawView.refresh()
                }
            })
        }.models = LayerModelManager.instance.getLayerModels()
    }
    //隐藏图层视图
    private fun hideLayerView(){
        mLayerPopupWindow.dismiss()
    }
    //隐藏颜色选择器
    private fun hideColorPicker(){
        mColorPickerPopupWindow.dismiss()
    }
    //隐藏画笔粗细视图
    private fun hideStrokeBarView(){
        mStrokeBarPopupWindow.dismiss()
    }
    //显示画笔粗细视图
    private fun showStokeBarView(){
        mStrokeBarPopupWindow.showAtLocation(
            mBinding.root,
            Gravity.END,
            mBinding.root.width - mBinding.actionMenuView.left,
            0
        )
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

    //发送广播 通知取消选中
    private fun sendUnselectShapeBroadCast(){
        //发送按钮点击的广播 取消选中状态
        requireActivity().sendBroadcast(Intent(BroadCastCenter.ICON_CLICK_BROADCAST_NAME))
        delayTask(200){
            mBinding.drawView.refresh()
        }
    }
}

