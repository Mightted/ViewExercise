package com.hxs.viewexercise.activity

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.hxs.viewexercise.R
import kotlinx.android.synthetic.main.activity_test.*

/**
 * Time: 2020/8/8
 * Author: Mightted
 * Description:
 */
class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                testView.drag(progress)
//                testView.setOffset(progress / 100f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.progress = (testView.reboundIndex() * 100).toInt()

            }
        })


        beforeBtn.setOnClickListener {
//            testView.offsetIndex(-1)
        }

        afterBtn.setOnClickListener {
//            testView.offsetIndex(1)
        }

//        testView.play()
    }


}