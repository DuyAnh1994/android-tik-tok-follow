package com.solar.dev.tiktok.app.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Interpolator
import android.widget.ImageView
import kotlin.math.pow


object AnimUtils {

    fun doBounceAnimation(target: View) {
        val interpolator = Interpolator { v ->
            getPowIn(v.toDouble(), 3.0)
        }
        val animator = ObjectAnimator.ofFloat(target, "translationY", 0f, 25f, 0f)
        animator.apply {
            setInterpolator(interpolator)
            startDelay = 200
            duration = 500
            repeatCount = 6
            start()
        }
    }

    private fun getPowIn(elapsedTimeRate: Double, pow: Double): Float {
        return elapsedTimeRate.pow(pow).toFloat()
    }

    fun animateView(
        targetView: View,
        viewCopy: ImageView,
        destination: View,
        onStart: () -> Unit,
        onSuccess: () -> Unit
    ) {
        onStart.invoke()
        val arrTarget = IntArray(2)
        targetView.getLocationOnScreen(arrTarget)

        val arrDestination = IntArray(2)
        destination.getLocationOnScreen(arrDestination)

        val bitmap = loadBitmapFromView(targetView, targetView.width, targetView.height)
        viewCopy.apply {
            setImageBitmap(bitmap)
            visibility = View.VISIBLE
        }
        viewCopy.apply {
            left = arrTarget[0]
            top = arrTarget[1]
        }
        val animSetXY = AnimatorSet()
        val y = ObjectAnimator.ofFloat(viewCopy, "translationY", viewCopy.top.toFloat(), (arrDestination[1] + (destination.height * 0.5)).toFloat()) as ObjectAnimator
        val x = ObjectAnimator.ofFloat(viewCopy, "translationX", viewCopy.left.toFloat(), (arrDestination[0] + (destination.width * 1)).toFloat()) as ObjectAnimator
        val sy = ObjectAnimator.ofFloat(viewCopy, "scaleY", 0.8f, 0.1f) as ObjectAnimator
        val sx = ObjectAnimator.ofFloat(viewCopy, "scaleX", 0.8f, 0.1f) as ObjectAnimator


        animSetXY.apply {
            playTogether(x, y, sx, sy)
            duration = 700
            start()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationStart(animation)
                    val alpha = AlphaAnimation(1f, 0.2f)
                    alpha.apply {
                        duration = 700
                        fillAfter = true
                    }
                    viewCopy.startAnimation(alpha)
                }

                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    onSuccess.invoke()
                    viewCopy.visibility = View.GONE
                }
            })
        }

    }

    private fun loadBitmapFromView(view: View, width: Int, height: Int): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        Log.e("width", "=$width")
        Log.e("height", "=$height")
        return returnedBitmap
    }
}