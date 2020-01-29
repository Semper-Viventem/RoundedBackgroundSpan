package ru.semper_viventem.roundedbackgroundspan

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.text.style.LineBackgroundSpan
import kotlin.math.abs

class RoundedBackgroundColorSpan(
    backgroundColor: Int,
    private val padding: Float,
    private val radius: Float
) : LineBackgroundSpan {

    companion object {
        private const val NO_INIT = -1f
    }

    private val rect = RectF()
    private val paint = Paint().apply {
        color = backgroundColor
        isAntiAlias = true
    }
    private val path = Path()

    private var prevWidth = NO_INIT
    private var prevRight = NO_INIT

    override fun drawBackground(
        c: Canvas,
        p: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lineNumber: Int
    ) {

        val actualWidth = p.measureText(text, start, end) + 2f * padding
        val widthDiff = abs(prevWidth - actualWidth)
        val diffIsShort = widthDiff < 2f * radius

        val width = if (lineNumber == 0) {
            actualWidth
        } else if ((actualWidth < prevWidth) && diffIsShort) {
            prevWidth
        } else if ((actualWidth > prevWidth) && diffIsShort) {
            actualWidth + (2f * radius - widthDiff)
        } else {
            actualWidth
        }

        val shiftLeft = 0f - padding
        val shiftRight = width + shiftLeft

        rect.set(shiftLeft, top.toFloat(), shiftRight, bottom.toFloat())

        c.drawRoundRect(rect, radius, radius, paint)

        if (lineNumber > 0) {
            drawLeftFillShape(c, rect, radius)

            when {
                prevWidth < width -> drawBottomFillShape(c, rect, radius)
                prevWidth > width -> drawTopFillShape(c, rect, radius)
                else -> drawRightFillShape(c, rect, radius)
            }
        }

        prevWidth = width
        prevRight = rect.right
    }

    /**
     *
     *  Draw shape for fill left rounded-space
     *
     *  +X
     *  | XX
     *  |   XX
     *  |     XX
     *  +-------X---------->
     *  |     XX
     *  |   XX
     *  | XX
     *  +X
     *
     */
    private fun drawLeftFillShape(c: Canvas, rect: RectF, radius: Float) {
        path.reset()
        path.moveTo(rect.left, rect.top + radius)
        path.lineTo(rect.left, rect.top - radius)
        path.lineTo(rect.left + radius, rect.top)
        path.lineTo(rect.left, rect.top + radius)

        c.drawPath(path, paint)
    }

    /**
     *
     * Draw shape for fill bottom rounded-space
     *
     *   ^
     *   |
     *   |          X
     *   |          X
     *   |         X|X
     *   |       XX | XX
     *   |     XX   |   XX
     *   |   XX     |     XX
     *   +--X-------+-------X------>
     *   |
     *   v
     *
     */
    private fun drawBottomFillShape(c: Canvas, rect: RectF, radius: Float) {
        path.reset()
        path.moveTo(prevRight + radius, rect.top)
        path.lineTo(prevRight - radius, rect.top)
        path.lineTo(prevRight, rect.top - radius)
        path.cubicTo(
            prevRight, rect.top - radius,
            prevRight, rect.top,
            prevRight + radius, rect.top
        )

        c.drawPath(path, paint)
    }

    /**
     *
     * Draw shape for fill top rounded-space
     *
     *  ^
     *  |
     *  +--X-------+-------X------>
     *  |   XX     |     XX
     *  |     XX   |   XX
     *  |       XX | XX
     *  |         X|X
     *  |          X
     *  v          X
     *
     */
    private fun drawTopFillShape(c: Canvas, rect: RectF, radius: Float) {
        path.reset()
        path.moveTo(rect.right + radius, rect.top)
        path.lineTo(rect.right - radius, rect.top)
        path.lineTo(rect.right, rect.top + radius)
        path.cubicTo(
            rect.right, rect.top + radius,
            rect.right, rect.top,
            rect.right + radius, rect.top
        )

        c.drawPath(path, paint)
    }

    /**
     *
     * Draw shape for right left rounded-space
     *
     *  ^
     *  |          X
     *  |         X|
     *  |       XX |
     *  |     XX   |
     *  |   XX     |
     *  +-XX-------+
     *  |   XX     |
     *  |     XX   |
     *  |       XX |
     *  |         X|
     *  |          X
     *  v
     *
     */
    private fun drawRightFillShape(c: Canvas, rect: RectF, radius: Float) {
        path.reset()
        path.moveTo(rect.right, rect.top - radius)
        path.lineTo(rect.right, rect.top + radius)
        path.lineTo(rect.right - radius, rect.top)
        path.lineTo(rect.right, rect.top - radius)

        c.drawPath(path, paint)
    }
}
