package com.baidu.infinity.ui.activity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.animation.BounceInterpolator
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.baidu.infinity.R


class MainActivity : AppCompatActivity() {
    private lateinit var btn:Button
    val tAnimator: Animator by lazy {
        ObjectAnimator.ofInt(btn,"bottom",btn.bottom,btn.bottom+dp2px(500)).apply {
            duration = 1000
            interpolator = BounceInterpolator()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    fun dp2px(dp:Int) = (resources.displayMetrics.density * dp).toInt()
}