package com.baidu.infinity.ui.fragment.home.view.account

import android.animation.Animator
import android.animation.Animator.AnimatorListener
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
import com.baidu.infinity.ui.util.toast
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
        initEvent()
        PopupWindow(context).apply {
            contentView = mBinding!!.root
            width = LayoutParams.WRAP_CONTENT
            height = LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun initEvent(){
        //给CoverImageView添加点击进入登录的事件
        mBinding!!.coverImageView.setOnClickListener {
            hide()
            //进入登录界面
            userViewModel.resetLoginState()
            val direction = HomeFragmentDirections.actionHomeFragmentToPicLoginFragment(true)
            mView?.findNavController()?.navigate(direction)
        }
        mBinding!!.workLayout.setOnClickListener {
            //让指示器移到当前这个layout的位置
            mBinding!!.bgIndicaterView.y = mBinding!!.workLayout.y
            //隐藏自己
            hide()
            //跳转到我的作品
            mView?.findNavController()?.navigate(R.id.action_homeFragment_to_myWorksFragment)
        }
        mBinding!!.settingLayout.setOnClickListener {
            //设置密码类型
            mBinding!!.bgIndicaterView.y = mBinding!!.settingLayout.y
            //隐藏自己
            hide()
            //弹出选择密码类型的窗口
            mView!!.findNavController().navigate(R.id.action_homeFragment_to_setPasswordTypeFragment)
        }
        mBinding!!.logoutLayout.setOnClickListener {
            mBinding!!.bgIndicaterView.y = mBinding!!.logoutLayout.y
            mBinding!!.loadingView.visibility = View.VISIBLE
            userViewModel.logout()
        }
        mBinding!!.logoffLayout.setOnClickListener {
            mBinding!!.bgIndicaterView.y = mBinding!!.logoffLayout.y
        }

        //动画事件
        mBinding!!.loadingView.addAnimatorListener(object : AnimatorListener{
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                //转动动画结束
                mBinding!!.coverImageView.visibility = View.VISIBLE
                mBinding!!.loadingView.visibility = View.INVISIBLE
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
    }

    //界面初始化
    private fun initUI(){
        val user = userViewModel.currentUser
        if (user == null){
            context.toast("用户为空")
            //没有用户信息
            mBinding!!.coverImageView.visibility = View.VISIBLE
        }else{
            //有用户 需要检查是否登录时间过期
            if (Date().time - user.loginDate.time > user.validate){
                //过期了
                mBinding!!.coverImageView.visibility = View.VISIBLE
            }else{
                //有登录用户了
                mBinding!!.coverImageView.visibility = View.INVISIBLE
                //需要获取登录用户信息 进行显示
                mBinding!!.tvName.text = user.name
                mBinding!!.tvIconName.text = user.name.first().uppercase()
            }
        }
    }

    //show
    fun showAsDropDown(view: View, offsetX:Int = 0, offsetY: Int = 0){
        //实时保存当前这个Activity里面的子视图
        //为的就是当需要跳转时，能够得到Activity里面的子视图
        mView = view
        popupWindow.showAsDropDown(view,offsetX,offsetY)
        initUI()
    }
    fun showAtLocation(parent: View, gravity: Int = Gravity.CENTER, offsetX: Int = 0, offsetY: Int = 0){
        mView = parent
        popupWindow.showAtLocation(parent,gravity,offsetX,offsetY)
        initUI()
    }
    //hide
    fun hide(){
        popupWindow.dismiss()
    }
}