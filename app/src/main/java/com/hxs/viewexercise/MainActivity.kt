package com.hxs.viewexercise

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnRepeat
import androidx.core.animation.doOnStart
import com.hxs.viewexercise.activity.MetaBallActivity
import com.hxs.viewexercise.activity.MotionActivity
import com.hxs.viewexercise.activity.TestActivity
import com.hxs.viewexercise.calendar.CalendarActivity
import com.hxs.viewexercise.expandlist.ExpandableListViewActivity
import kotlinx.android.synthetic.main.activity_main.*

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
}