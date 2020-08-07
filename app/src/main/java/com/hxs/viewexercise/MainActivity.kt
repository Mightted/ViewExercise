package com.hxs.viewexercise

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                testView.setOffset(progress / 100f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        beforeBtn.setOnClickListener {
            testView.offsetIndex(-1)
        }

        afterBtn.setOnClickListener {
            testView.offsetIndex(1)
        }

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