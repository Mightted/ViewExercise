package com.hxs.viewexercise

import android.animation.ValueAnimator
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Rect
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlin.random.Random

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
        private val mutex = Mutex()

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible || animator.isPaused) {
                animator.resume()
                nightSky.resume()
            } else {
                animator.pause()
                nightSky.pause()
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
            if (!isVisible) {
                return
            }
            CoroutineScope(Dispatchers.Default).launch {
                surfaceHolder.lockCanvas()?.run {
                    drawBitmap(bitmap, null, bgRect, paint)
                    nightSky.onDraw(this, mutex)
                    surfaceHolder.unlockCanvasAndPost(this)
                }
            }
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
                    nightSky.updateMeteors()

                    if (Random.nextInt(10) % 3 == 0) {
                        nightSky.addMeteor()
                    }
                })


            }
        }

    }


}