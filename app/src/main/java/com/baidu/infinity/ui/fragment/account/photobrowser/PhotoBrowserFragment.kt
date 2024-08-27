package com.baidu.infinity.ui.fragment.account.photobrowser

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.DragEvent
import android.view.View
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.baidu.infinity.R
import com.baidu.infinity.databinding.FragmentPhotoBroswerBinding
import com.baidu.infinity.databinding.PhotoBrowserItemLayoutBinding
import com.baidu.infinity.model.IconModel
import com.baidu.infinity.ui.base.BaseFragment
import com.baidu.infinity.ui.fragment.account.PhotoViewModel
import com.baidu.infinity.ui.fragment.account.album.PhotoModel
import com.baidu.infinity.ui.fragment.home.file.FileManager
import com.baidu.infinity.ui.fragment.home.view.LoadingView
import com.baidu.infinity.ui.util.OperationType
import com.baidu.infinity.ui.util.TimeUtil
import com.baidu.infinity.ui.util.delayTask
import com.bumptech.glide.Glide
import com.drake.brv.listener.DefaultItemTouchCallback
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

class PhotoBrowserFragment: BaseFragment<FragmentPhotoBroswerBinding>() {
    private var mCurrentIndex: Int = 0
    private val mViewModel: PhotoViewModel by activityViewModels()
    private val mLoadingView: LoadingView by lazy {
        LoadingView(requireContext())
    }

    override fun initBinding(): FragmentPhotoBroswerBinding {
        return FragmentPhotoBroswerBinding.inflate(layoutInflater)
    }


    override fun initView() {
        //监听数据
        mViewModel.photoModels.observe(viewLifecycleOwner){ models ->
            mBinding.recyclerView.models = models
            //滚动到选中的那一个
            mBinding.recyclerView.scrollToPosition(mViewModel.selectedIndex)
            //设置虚化背景
            mCurrentIndex = mViewModel.selectedIndex
            mBinding.blurImageView.setImageBitmap(BlurUtil.blur(models[mViewModel.selectedIndex].thumbnailPath, 50f))
        }
        //按页显示
        PagerSnapHelper().attachToRecyclerView(mBinding.recyclerView)
        //配置recyclerView
        mBinding.recyclerView.linear(RecyclerView.HORIZONTAL).setup {
            addType<PhotoModel>(R.layout.photo_browser_item_layout)
            onBind {
                val binding = getBinding<PhotoBrowserItemLayoutBinding>()
                val model = getModel<PhotoModel>()
                Glide.with(context).load(model.thumbnailPath).into(binding.iconImageView)
            }
        }
        //监听滚动时 获得动态的位置
        mBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val index = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                //设置虚化背景
                if (index != -1 && mCurrentIndex != index) {
                    val models = mViewModel.photoModels.value
                    models?.let {
                        mBinding.blurImageView.setImageBitmap(
                            BlurUtil.blur(it[index].thumbnailPath, 50f)
                        )
                    }
                    mCurrentIndex = index
                }
            }
        })
        //配置底部操作按钮
        mBinding.actionMenuView.setIcons(listOf(
            IconModel(OperationType.OPERATION_DELETE,R.string.garbage),
            IconModel(OperationType.MENU_DOWNLOAD,R.string.download),
            IconModel(OperationType.MENU_SHARE,R.string.share),
        ))
        //配置点击事件
        mBinding.actionMenuView.iconClickListener = { type,_ ->
            delayTask(200){
                mBinding.actionMenuView.resetIconState()
            }
            if (type == OperationType.OPERATION_DELETE){
                //删除选中的图片
                mLoadingView.show(mBinding.root)
                lifecycleScope.launch(Dispatchers.IO) {
                    val model = mViewModel.photoModels.value!!.get(mCurrentIndex)
                    FileManager.instance.removeFile(model.originalPath)
                    FileManager.instance.removeFile(model.thumbnailPath)
                    withContext(Dispatchers.Main){
                        mLoadingView.hide{
                            //mViewModel.removeAll(listOf(model))
                            mViewModel.remove(model)
                            //刷新界面
                            mBinding.recyclerView.bindingAdapter.notifyDataSetChanged()
                            if(mViewModel.photoModels.value!!.isNotEmpty()){
                                if (mViewModel.photoModels.value!!.size-1 == mCurrentIndex){
                                    mCurrentIndex = 0
                                }
                                //更新虚化背景
                                val newModel = mViewModel.photoModels.value!![mCurrentIndex]
                                mBinding.blurImageView.setImageBitmap(BlurUtil.blur(newModel.thumbnailPath, 50f))
                            }else{
                                mBinding.blurImageView.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
            }else if(type == OperationType.MENU_DOWNLOAD){
                downloadToPhotoAlbum()
            }else{
                shareImage()
            }
        }
    }

    private fun downloadToPhotoAlbum(){
        mLoadingView.show(mBinding.root)

        //下载本地
        val path = mViewModel.photoModels.value!![mCurrentIndex].originalPath
        val bitmap = BitmapFactory.decodeFile(path)
        //定位图片在系统相册里面的位置
        val imagesUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        }else{
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        //确定插入的数据和对应的字段
        val contentValues = ContentValues().apply {
            //确定名字 注意唯一
            put(MediaStore.Images.Media.DISPLAY_NAME, TimeUtil.getTimeName())
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

    //将图片保存到共享目录
    private fun saveImageToExternalPath(file: File, bitmap: Bitmap){
        FileOutputStream(file).use { fos ->
            BufferedOutputStream(fos).use { bos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos)
            }
        }
    }

    //分享图片
    private fun shareImage() {
        lifecycleScope.launch {

            val path = mViewModel.photoModels.value!![mCurrentIndex].originalPath
            val bitmap = BitmapFactory.decodeFile(path)

            //获取共享文件路径
            val externalDir = requireContext().getExternalFilesDir(null)
            //在根目录下创建共享的文件
            val file = File(externalDir,"infinity.jpg")
            //将图片保存到共享位置
            saveImageToExternalPath(file,bitmap)

            //获取共享文件对应的contenturi
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "com.baidu.infinity.provider",
                file
            )
            //配置意图 打开所有应用里面能够接受action的界面
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_STREAM,uri)

            //发起分享动作
            //弹出可以接受图片分享的所有应用供我选择
            requireContext().startActivity(Intent.createChooser(intent,"分享图片"))
        }

    }
}