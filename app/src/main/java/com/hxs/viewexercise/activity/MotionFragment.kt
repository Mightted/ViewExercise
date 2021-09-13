package com.hxs.viewexercise.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hxs.viewexercise.R
import kotlinx.android.synthetic.main.fragment_motion.view.*

class MotionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_motion, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.button.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    (view as MyMotionLayout).needIntercept = true
                }
                MotionEvent.ACTION_UP -> {
                    (view as MyMotionLayout).needIntercept = false
                }
            }
            false
        }

    }
}