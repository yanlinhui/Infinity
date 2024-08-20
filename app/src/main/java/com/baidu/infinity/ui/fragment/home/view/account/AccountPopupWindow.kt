package com.baidu.infinity.ui.fragment.home.view.account

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager.LayoutParams
import android.widget.PopupWindow
import androidx.navigation.findNavController
import com.baidu.infinity.R
import com.baidu.infinity.databinding.AccountLayoutBinding
import com.baidu.infinity.ui.fragment.home.HomeFragmentDirections
import com.baidu.infinity.viewmodel.UserViewModel
import java.util.Date

/**
 * 将PopupWindow封装在内部
 * 方便外部调用
 * 降低耦合性
 */
class AccountPopupWindow(val context: Context, val userViewModel: UserViewModel) {
    private var mBinding: AccountLayoutBinding? = null
    var addImageSelectListener:(Int)->Unit = {}
    private var mView:View? = null
    private val popupWindow:PopupWindow by lazy {
        val inflater = LayoutInflater.from(context)
        mBinding = AccountLayoutBinding.inflate(inflater)
        initUI()
        PopupWindow(context).apply {
            contentView = mBinding!!.root
            width = LayoutParams.WRAP_CONTENT
            height = LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    //界面初始化
    private fun initUI(){
        val user = userViewModel.currentUser
        if (user == null){
            //没有用户信息
            mBinding!!.coverImageView.visibility = View.VISIBLE
        }else{
            //有用户 需要检查是否登录时间过期
            if (Date().time - user.loginDate.time > user.validate){
                //过期了
                mBinding!!.coverImageView.visibility = View.VISIBLE
            }else{
                mBinding!!.coverImageView.visibility = View.INVISIBLE
            }
        }

        //给CoverImageView添加点击进入登录的事件
        mBinding!!.coverImageView.setOnClickListener {
            hide()
            //进入登录界面
            userViewModel.resetLoginState()
            val direction = HomeFragmentDirections.actionHomeFragmentToPicLoginFragment(true)
            mView?.findNavController()?.navigate(direction)
        }
    }

    //show
    fun showAsDropDown(view: View, offsetX:Int = 0, offsetY: Int = 0){
        //实时保存当前这个Activity里面的子视图
        //为的就是当需要跳转时，能够得到Activity里面的子视图
        mView = view
        popupWindow.showAsDropDown(view,offsetX,offsetY)
    }
    fun showAtLocation(parent: View, gravity: Int = Gravity.CENTER, offsetX: Int = 0, offsetY: Int = 0){
        popupWindow.showAtLocation(parent,gravity,offsetX,offsetY)
    }
    //hide
    fun hide(){
        popupWindow.dismiss()
    }
}