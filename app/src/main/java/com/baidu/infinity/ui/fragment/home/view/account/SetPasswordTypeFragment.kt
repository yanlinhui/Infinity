package com.baidu.infinity.ui.fragment.home.view.account

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.baidu.infinity.databinding.ChoosePasswordTypeLayoutBinding
import com.baidu.infinity.ui.base.BaseFragment
import com.baidu.infinity.ui.fragment.home.view.LoadingView
import com.baidu.infinity.ui.util.PasswordType
import com.baidu.infinity.ui.util.delayTask
import com.baidu.infinity.viewmodel.UserViewModel

class SetPasswordTypeFragment: BaseFragment<ChoosePasswordTypeLayoutBinding>(){
    private val mUserViewModel: UserViewModel by activityViewModels()
    private val mLoadingView: LoadingView by lazy {
        LoadingView(requireContext())
    }

    override fun initBinding(): ChoosePasswordTypeLayoutBinding {
        return ChoosePasswordTypeLayoutBinding.inflate(layoutInflater)
    }

    override fun onResume() {
        super.onResume()
        delayTask(300){
            //设置默认选中的密码类型
            if(mUserViewModel.currentUser?.passwordType == 0){
                mBinding.outLineView.x = mBinding.pic.x
            }else{
                mBinding.outLineView.x = mBinding.pin.x
            }
        }

    }

    override fun initView() {
        mBinding.pic.setOnClickListener {
            mBinding.outLineView.x = mBinding.pic.x
            mUserViewModel.changePasswordType(PasswordType.PIN)
            startUpdatingAnimation()
        }
        mBinding.pin.setOnClickListener {
            mBinding.outLineView.x = mBinding.pin.x
            mUserViewModel.changePasswordType(PasswordType.PIC)
            startUpdatingAnimation()
        }

        mBinding.cancleBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun startUpdatingAnimation(){
        mLoadingView.show(mBinding.root)
        delayTask(200){
            mLoadingView.hide {
                //返回主界面
                findNavController().navigateUp()
            }
        }
    }
}