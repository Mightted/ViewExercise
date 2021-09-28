package com.hxs.viewexercise

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.hxs.viewexercise.activity.MetaBallActivity
import com.hxs.viewexercise.calendar.CalendarActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        animatorView.animator()
        ObjectAnimator.ofFloat(0f, 1f).run {
            duration = 3500
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE

            addUpdateListener {

            }

            start()
        }
    }

    fun gotoMetaBall(view: View) {

        startActivity(Intent(this, MetaBallActivity::class.java))
    }

    fun gotoTestPage(view: View) {
        startActivity(Intent(this, CalendarActivity::class.java))
//        startActivity(Intent(this, TestActivity::class.java))
    }

    fun toMotion1(view: View) {
        startActivity(Intent(this, com.hxs.viewexercise.motionlayout.Motion1Activity::class.java))
    }

    fun toMotion2(view: View) {
        startActivity(Intent(this, com.hxs.viewexercise.motionlayout.Motion2Activity::class.java))
    }
}