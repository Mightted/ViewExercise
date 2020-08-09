package com.hxs.viewexercise

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.hxs.viewexercise.activity.MetaBallActivity
import com.hxs.viewexercise.activity.TestActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun gotoMetaBall(view:View) {

        startActivity(Intent(this, MetaBallActivity::class.java))
    }

    fun gotoTestPage(view: View) {
        startActivity(Intent(this, TestActivity::class.java))
    }
}