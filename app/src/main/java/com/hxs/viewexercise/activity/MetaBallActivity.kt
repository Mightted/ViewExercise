package com.hxs.viewexercise.activity

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.hxs.viewexercise.R
import kotlinx.android.synthetic.main.activity_metaball.*

/**
 * Time: 2020/8/8
 * Author: Mightted
 * Description:
 */
class MetaBallActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metaball)


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

}