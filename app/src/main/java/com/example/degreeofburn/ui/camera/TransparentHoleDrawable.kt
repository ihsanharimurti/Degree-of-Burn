package com.example.degreeofburn.ui.camera

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View

/**
 * Custom drawable that creates a semi-transparent overlay with a transparent "hole"
 * for the camera preview area
 */
class TransparentHoleDrawable(
    private val width: Int,
    private val height: Int,
    private val centerView: View
) : Drawable() {

    private val overlayPaint = Paint().apply {
        color = Color.argb(153, 0, 0, 0)  // Semi-transparent grey (#99808080)
        style = Paint.Style.FILL
    }

    private val transparentPaint = Paint().apply {
        color = Color.TRANSPARENT
        style = Paint.Style.FILL
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun draw(canvas: Canvas) {
        // Get the bounds of the center view relative to the parent
        val centerBounds = Rect()
        centerView.getGlobalVisibleRect(centerBounds)

        // Get the bounds of the parent view (the overlay)
        val parentBounds = Rect()
        centerView.parent.let { parentView ->
            if (parentView is View) {
                parentView.getGlobalVisibleRect(parentBounds)
            }
        }

        // Adjust the center view bounds to be relative to the parent
        centerBounds.offset(-parentBounds.left, -parentBounds.top)

        // Save the current canvas state
        val saveCount = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)

        // Draw the semi-transparent overlay
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)

        // Punch a hole in the overlay for the center view
        canvas.drawRect(
            centerBounds.left.toFloat(),
            centerBounds.top.toFloat(),
            centerBounds.right.toFloat(),
            centerBounds.bottom.toFloat(),
            transparentPaint
        )

        // Restore the canvas state
        canvas.restoreToCount(saveCount)
    }

    override fun setAlpha(alpha: Int) {
        overlayPaint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        overlayPaint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}