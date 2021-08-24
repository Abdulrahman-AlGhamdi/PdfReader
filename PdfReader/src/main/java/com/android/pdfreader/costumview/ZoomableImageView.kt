package com.android.pdfreader.costumview

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.OnDoubleTapListener
import android.view.GestureDetector.OnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.widget.AppCompatImageView

internal class ZoomableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr),
    View.OnTouchListener,
    OnGestureListener,
    OnDoubleTapListener {

    //shared constructing
    private var scaleDetector: ScaleGestureDetector
    private var gestureDetector: GestureDetector
    private var matrixValues: FloatArray
    private var newMatrix: Matrix
    private var mode = NONE

    // Scales
    private var saveScale = 1f
    private var minimumScale = 1f
    private var maximumScale = 3f

    // view dimensions
    private var originalWidth = 0f
    private var originalHeight = 0f
    private var viewWidth = 0
    private var viewHeight = 0
    private var last = PointF()
    private var start = PointF()

    init {
        super.setClickable(true)
        scaleDetector = ScaleGestureDetector(context, ScaleListener())
        newMatrix = Matrix()
        matrixValues = FloatArray(9)
        imageMatrix = newMatrix
        scaleType = ScaleType.MATRIX
        gestureDetector = GestureDetector(context, this)
        setOnTouchListener(this)
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mode = ZOOM
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var scaleFactor = detector.scaleFactor
            val prevScale = saveScale
            saveScale *= scaleFactor

            if (saveScale > maximumScale) {
                saveScale = maximumScale
                scaleFactor = maximumScale / prevScale
            } else if (saveScale < minimumScale) {
                saveScale = minimumScale
                scaleFactor = minimumScale / prevScale
            }

            if (originalWidth * saveScale <= viewWidth || originalHeight * saveScale <= viewHeight)
                newMatrix.postScale(scaleFactor, scaleFactor, viewWidth / 2.toFloat(), viewHeight / 2.toFloat())
            else newMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)

            fixTranslation()
            return true
        }
    }

    override fun onDoubleTap(motionEvent: MotionEvent): Boolean {
        fitToScreen()
        return false
    }

    private  fun fitToScreen() {
        saveScale = 1f
        val drawable = drawable
        if (drawable != null && drawable.intrinsicWidth != 0 && drawable.intrinsicHeight != 0) {
            val scaleX = viewWidth.toFloat() / drawable.intrinsicWidth.toFloat()
            val scaleY = viewHeight.toFloat() / drawable.intrinsicHeight.toFloat()
            val scale  = scaleX.coerceAtMost(scaleY)
            newMatrix.setScale(scale, scale)

            // Center the image
            val redundantYSpace = (viewHeight.toFloat() - scale * drawable.intrinsicHeight.toFloat()) / 2f
            val redundantXSpace = (viewWidth.toFloat() - scale * drawable.intrinsicWidth.toFloat()) / 2f
            newMatrix.postTranslate(redundantXSpace, redundantYSpace)
            originalWidth = viewWidth - 2 * redundantXSpace
            originalHeight = viewHeight - 2 * redundantYSpace
            imageMatrix = newMatrix
        }
    }

    fun fixTranslation() {
        newMatrix.getValues(matrixValues) //put matrix values into a float array so we can analyze
        val transX = matrixValues[Matrix.MTRANS_X] //get the most recent translation in x direction
        val transY = matrixValues[Matrix.MTRANS_Y] //get the most recent translation in y direction
        val fixTransX = getFixTranslation(transX, viewWidth.toFloat(), originalWidth * saveScale)
        val fixTransY = getFixTranslation(transY, viewHeight.toFloat(), originalHeight * saveScale)
        if (fixTransX != 0f || fixTransY != 0f) newMatrix.postTranslate(fixTransX, fixTransY)
    }

    private fun getFixTranslation(trans: Float, viewSize: Float, contentSize: Float): Float {
        val minTrans: Float
        val maxTrans: Float

        if (contentSize <= viewSize) { // case: NOT ZOOMED
            minTrans = 0f
            maxTrans = viewSize - contentSize
        } else { //CASE: ZOOMED
            minTrans = viewSize - contentSize
            maxTrans = 0f
        }

        if (trans < minTrans) { // negative x or y translation (down or to the right)
            return -trans + minTrans
        }
        if (trans > maxTrans) { // positive x or y translation (up or to the left)
            return -trans + maxTrans
        }
        return 0F
    }

    private fun getFixDragTrans(delta: Float, viewSize: Float, contentSize: Float): Float {
        return if (contentSize <= viewSize) 0F else delta
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        viewHeight = MeasureSpec.getSize(heightMeasureSpec)
        if (saveScale == 1f) fitToScreen()
    }

    override fun onTouch(view: View?, event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        val currentPoint = PointF(event.x, event.y)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                last.set(currentPoint)
                start.set(last)
                mode = DRAG
            }
            MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                val dx = currentPoint.x - last.x
                val dy = currentPoint.y - last.y
                val fixTransX = getFixDragTrans(dx, viewWidth.toFloat(), originalWidth * saveScale)
                val fixTransY = getFixDragTrans(dy, viewHeight.toFloat(), originalHeight * saveScale)
                newMatrix.postTranslate(fixTransX, fixTransY)
                fixTranslation()
                last[currentPoint.x] = currentPoint.y
            }
            MotionEvent.ACTION_POINTER_UP -> mode = NONE
        }
        imageMatrix = newMatrix
        return false
    }

    override fun onDown(motionEvent: MotionEvent): Boolean { return false }
    override fun onShowPress(motionEvent: MotionEvent) {}
    override fun onSingleTapUp(motionEvent: MotionEvent): Boolean { return false }
    override fun onScroll(motionEvent: MotionEvent, motionEvent1: MotionEvent, v: Float, v1: Float): Boolean { return false }
    override fun onLongPress(motionEvent: MotionEvent) {}
    override fun onFling(motionEvent: MotionEvent, motionEvent1: MotionEvent, v: Float, v1: Float): Boolean { return false }
    override fun onSingleTapConfirmed(motionEvent: MotionEvent): Boolean { return false }
    override fun onDoubleTapEvent(motionEvent: MotionEvent): Boolean { return false }

    companion object {
        private const val NONE = 0
        private const val DRAG = 1
        private const val ZOOM = 2
    }
}