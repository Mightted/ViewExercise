package com.hxs.viewexercise

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        testView.play()
    }

    override fun onResume() {
        super.onResume()
//        testView.resume()
    }

    override fun onPause() {
        super.onPause()
//        testView.pause()
    }
}