package com.hxs.viewexercise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testView.play()
//        testView.animate(2000)
//        testView.changeColor(Color.GREEN)
    }

    override fun onResume() {
        super.onResume()
        testView.resume()
    }

    override fun onPause() {
        super.onPause()
        testView.pause()
    }
}