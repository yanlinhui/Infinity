package com.baidu.infinity.ui.fragment.account.album


import android.graphics.Color
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.baidu.infinity.R
import com.baidu.infinity.databinding.FragmentMyWorksBinding
import com.baidu.infinity.databinding.MyworkAlbumItemLayoutBinding
import com.baidu.infinity.ui.base.BaseFragment
import com.baidu.infinity.ui.fragment.account.PhotoViewModel
import com.baidu.infinity.ui.fragment.home.file.FileManager
import com.baidu.infinity.ui.fragment.home.view.LoadingView
import com.bumptech.glide.Glide
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyWorksFragment: BaseFragment<FragmentMyWorksBinding>() {
    private val mViewModel: PhotoViewModel by activityViewModels()

    private var mEditingState = EditingState.NORMAL
    private var mSelectedModel = arrayListOf<PhotoModel>()
    private val mLoadingView: LoadingView by lazy {
        LoadingView(requireContext())
    }

    override fun initBinding(): FragmentMyWorksBinding {
        return FragmentMyWorksBinding.inflate(layoutInflater)
    }

    override fun initView() {
        //加载以下数据
        mViewModel.reloadData()

        //监听数据
        mViewModel.photoModels.observe(viewLifecycleOwner){ models ->
            mBinding.recyclerView.models = models
        }

        //加载数据源
        mBinding.recyclerView.grid(5).setup {
            addType<PhotoModel>(R.layout.mywork_album_item_layout)
            onBind {
                val binding = getBinding<MyworkAlbumItemLayoutBinding>()
                val model = getModel<PhotoModel>()
                //使用glide加载图片
                Glide.with(requireContext()).load(model.thumbnailPath).into(binding.ivThumbnail)
                //编辑状态
                if (mEditingState == EditingState.NORMAL) {
                    binding.ivUnChoose.visibility = View.INVISIBLE
                }else{
                    binding.ivUnChoose.visibility = View.VISIBLE
                }
                //选中状态
                if (model.selectState == SelectState.NORMAL) {
                    binding.ivSelected.visibility = View.INVISIBLE
                }else{
                    binding.ivSelected.visibility = View.VISIBLE
                }
                //添加点击事件
                binding.root.setOnClickListener {
                    if (mEditingState == EditingState.NORMAL){
                        //进入照片浏览界面
                        mViewModel.selectedIndex = modelPosition
                        findNavController().navigate(R.id.action_myWorksFragment_to_photoBrowserFragment)
                    }else{
                        if (model.selectState == SelectState.NORMAL) {
                            //选中
                            model.selectState = SelectState.SELECTED
                            mSelectedModel.add(model)
                        }else{
                            //取消选中
                            model.selectState = SelectState.NORMAL
                            mSelectedModel.remove(model)
                        }
                        notifyItemChanged(modelPosition)
                    }
                }
            }
        }

        //删除按钮显示状态
        mBinding.tvEdit.setOnClickListener {
            if (mEditingState == EditingState.NORMAL){
                //进入编辑状态
                mBinding.tvEdit.text = "Done"
                mBinding.tvEdit.setTextColor(Color.WHITE)
                mBinding.deleteImageView.visibility = View.VISIBLE
                mEditingState = EditingState.EDIT
            }else{
                //从编辑状态进入正常状态
                mBinding.tvEdit.text = "Edit"
                mBinding.tvEdit.setTextColor(Color.parseColor("#B4B4B5"))
                mBinding.deleteImageView.visibility = View.INVISIBLE
                mEditingState = EditingState.NORMAL
            }
            mBinding.recyclerView.bindingAdapter.notifyDataSetChanged()
        }

        //返回按钮
        mBinding.backImageView.setOnClickListener {
            findNavController().navigateUp()
        }

        //删除按钮添加点击事件
        mBinding.deleteImageView.setOnClickListener {
            if (mSelectedModel.isEmpty()) return@setOnClickListener

            //删除选中的图片
            mLoadingView.show(mBinding.root)
            lifecycleScope.launch(Dispatchers.IO) {
                mSelectedModel.forEach { model ->
                    FileManager.instance.removeFile(model.originalPath)
                    FileManager.instance.removeFile(model.thumbnailPath)
                }
                withContext(Dispatchers.Main){
                    mLoadingView.hide{
                        //从数据源中清空选中的图片
                        mViewModel.removeAll(mSelectedModel)
                        mSelectedModel.clear()
                        //刷新界面
                        mBinding.recyclerView.bindingAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}











