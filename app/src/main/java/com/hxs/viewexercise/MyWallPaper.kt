package com.hxs.viewexercise

import android.animation.ValueAnimator
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Rect
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener

class MyWallPaper : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return MyEngine()
    }


    inner class MyEngine : Engine() {


        private val paint = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
        }
        private val bitmap = BitmapFactory.decodeResource(resources, R.drawable.bg_night_sky)

        private lateinit var bgRect: Rect
        private lateinit var nightSky: NightSky
        private val animator = initAnimator()


        //        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
//            super.onSurfaceDestroyed(holder)
//        }
//
        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible || animator.isPaused) {
                animator.resume()
                nightSky.resume()
            } else {
                animator.pause()
                nightSky.resume()
            }
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            bgRect = Rect(0, 0, width, height)
            nightSky = NightSky(width.toFloat(), height * 0.5f)
            animator.start()
        }

        private fun updateFrame() {
            val canvas = surfaceHolder.lockCanvas() ?: return

            canvas.drawBitmap(bitmap, null, bgRect, paint)
            nightSky.onDraw(canvas)

            surfaceHolder.unlockCanvasAndPost(canvas)
        }


        private fun initAnimator(): ValueAnimator {
            return ValueAnimator.ofInt(0, 999).apply {
                duration = 1000
                interpolator = LinearInterpolator()
                repeatCount = ValueAnimator.INFINITE

                var frame = 0
                val frameUnit = 1000 / 60f

                addUpdateListener {
                    if (it.animatedValue as Int / frameUnit > frame) {
                        frame++
                        updateFrame()
                    } else {
                        println("${it.animatedValue as Int / frameUnit} : $frame")
                    }
                }
                addListener(onRepeat = {
                    frame = 0
                    nightSky.addMeteor()
                })


            }
        }

    }


}