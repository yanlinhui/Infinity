package com.baidu.infinity.ui.view

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.baidu.infinity.R
import com.baidu.infinity.databinding.LayoutInputViewBinding

/**
 * 自定义组合
 *  将多个系统已有的控件使用组合的方式组合为一个整体
 *  将事件绑定到组合控件上
 *
 * 1.显示提示文本
 * 2.可以输入内容
 * 3.有分割线
 * 4. 输入时改颜色
 * 5. 失去焦点时恢复默认色
 *
 * 实现步骤
 *  方式1. 分析多个控件的组合方式 ，确定使用的容器的类型，使用代码添加控件
 *  方式2. 直接使用xml文件将自己组合的样式布局好，添加到一个已有的容器中FrameLayout
 *
 *  自定义属性
 *      自定义完控件之后，需要在xml中根据自己的要求去设定对应属性的值
 *  步骤：1. style中定义样式
 *       2. xml中使用对应的属性给这个自定义的view赋值
 *       3. 在代码中解析xml中配置的自定义属性的值 给对应的控件设置相应的值
 */
class PXDInputView: FrameLayout{
    private lateinit var binding: LayoutInputViewBinding

    constructor(context: Context):super(context){
        initView()
    }
    //attrs:AttributeSet?参数就是用来接收自定义属性的值
    constructor(context:Context,attrs:AttributeSet?):super(context,attrs){
        initView()
        parseXMLAttribute(attrs)
    }

    private fun parseXMLAttribute(attrs: AttributeSet?) {
        //解析属性
        //从attrs中解析出PXDInputView中自定义的属性 放到TypedArray中
        val typedArray = context.obtainStyledAttributes(attrs,R.styleable.PXDInputView)
        //解析自己关心的属性
        //获取提示文本
        val info = typedArray.getString(R.styleable.PXDInputView_info_text)
        //获取hint文本
        val hint = typedArray.getString(R.styleable.PXDInputView_hint_text)
        //输入类型
        val inputType = typedArray.getInt(R.styleable.PXDInputView_input_type,0)
        //修改控件
        binding.tvInfo.text = info
        binding.etInput.hint = hint
        //设置输入类型
        if (inputType == 1){
            //设置为密码输入
            binding.etInput.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        typedArray.recycle()
    }

    private fun initView(){
        //计息xml中的view
        val layoutInflater = LayoutInflater.from(context)
        binding = LayoutInputViewBinding.inflate(layoutInflater,this,false)

        //创建布局参数
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.CENTER

        addView(binding.root,layoutParams)

        initEvent()
    }

    private fun initEvent(){
        val blue = resources.getColor(R.color.blue,null)
        val default = resources.getColor(R.color.light_black,null)
        binding.etInput.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                binding.tvInfo.setTextColor(blue)
                binding.etInput.setTextColor(blue)
                binding.view.setBackgroundColor(blue)
            }else{
                binding.tvInfo.setTextColor(default)
                binding.etInput.setTextColor(default)
                binding.view.setBackgroundColor(default)
            }
        }
    }

    //提供给外部一个访问内容的方法
    fun text():String{
        return binding.etInput.text.toString()
    }

    //监听内容发生改变的事件
    fun addTextChangeListener(afterTextChanged:(String)->Unit){
        binding.etInput.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                afterTextChanged(s.toString())
            }
        })
    }

    //外部设置当前显示的状态为错误状态
    fun showError(){
        binding.tvInfo.setTextColor(Color.RED)
        binding.etInput.setTextColor(Color.RED)
    }
}











