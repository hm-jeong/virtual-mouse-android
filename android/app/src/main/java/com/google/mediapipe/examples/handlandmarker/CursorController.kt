package com.google.mediapipe.examples.handlandmarker

import android.annotation.SuppressLint
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.widget.ImageView
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlin.math.hypot

class CursorController private constructor() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: CursorController? = null

        @Synchronized
        fun getInstance() = instance ?: CursorController().also { instance = it }
    }

    private lateinit var cursor: ImageView
    private lateinit var cursorLayout: View
    private var clicked: Boolean = false


    fun setCursor(cursor : ImageView, cursorLayout: View) {
        this.cursor = cursor
        this.cursorLayout = cursorLayout
    }

    fun setHandLandmark(
        handLandmarkerResults: HandLandmarkerResult,
    ) {
        handLandmarkerResults.let { handLandmarkResult ->
            for (landmark in handLandmarkResult.landmarks()) {
                move(landmark[8].x() * cursorLayout.width, landmark[8].y() * cursorLayout.height)
            }
        }

        handLandmarkerResults.let { handLandmarkResult ->
            for (landmark in handLandmarkResult.landmarks()) {
                if (getDistance(landmark[8], landmark[12]) < getDistance(landmark[8], landmark[7])) {
                    if (!clicked) {
                        clicked = true
                        Log.d("virtual-mouse", "click!!!")
                        makeClickEvent(landmark[8].x() * cursorLayout.width, landmark[8].y() * cursorLayout.height)
                    }
                }
                else
                {
                    clicked = false
                }
            }
        }
    }

    private fun makeClickEvent(x: Float, y: Float) {
        val downTime = SystemClock.uptimeMillis()
        val metaState = 0

        val motionEvent1 = MotionEvent.obtain(downTime, downTime + 1000, ACTION_DOWN, x, y, metaState)
        val motionEvent2 = MotionEvent.obtain(downTime + 1000, downTime + 2000, ACTION_UP, x, y, metaState)

        cursorLayout.dispatchTouchEvent(motionEvent1)
        cursorLayout.dispatchTouchEvent(motionEvent2)
    }

    private fun getDistance(a: NormalizedLandmark, b: NormalizedLandmark) : Float {
        return hypot((a.x() - b.x()) * cursorLayout.width, (a.y() - b.y()) * cursorLayout.height)
    }

    private fun move(x: Float, y: Float) {
        Log.d("CursorController", "x: $cursor.x y: $cursor.y")
        cursor.x = x;
        cursor.y = y;
    }
}
