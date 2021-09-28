package com.hxs.viewexercise.motionlayout

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import com.hxs.viewexercise.R
import kotlinx.android.synthetic.main.activity_motion2.*

class Motion2Activity : AppCompatActivity() {

    private var curIndex = 2

    private val colors = listOf(
        R.color.colorAccent,
        R.color.colorPrimaryDark,
        R.color.colorPrimary,
        R.color.colorAccent,
        R.color.colorPrimary
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_motion2)
        updateIndex()
        motionLayout.setTransitionListener(object : TransitionAdapter() {

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                super.onTransitionCompleted(motionLayout, currentId)
                when (currentId) {
                    R.id.leftCard -> {
                        curIndex = (++curIndex + 5) % 5
                        updateIndex()
                        motionLayout?.progress = 0f
                        motionLayout?.setTransition(R.id.start, R.id.leftCard)

                    }
                    R.id.rightCard -> {
                        curIndex = (--curIndex + 5) % 5
                        updateIndex()
                        motionLayout?.progress = 0f
                        motionLayout?.setTransition(R.id.start, R.id.rightCard)

                    }
                }
            }
        })
    }


    private fun updateIndex() {
        cardLeft2.setCardBackgroundColor(resources.getColor(colors[(curIndex - 2 + 5) % 5]))
        cardLeft1.setCardBackgroundColor(resources.getColor(colors[(curIndex - 1 + 5) % 5]))
        centerContent.setBackgroundColor(Color.parseColor("#03DAC5"))
        cardRight1.setCardBackgroundColor(resources.getColor(colors[(curIndex + 1) % 5]))
        cardRight2.setCardBackgroundColor(resources.getColor(colors[(curIndex + 2) % 5]))

    }
}