package ru.semper_viventem.roundedbackgroundspan

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.text.style.LineBackgroundSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import kotlin.math.abs

/**
 * Draw rounded background for text
 *
 * @author Konstantin Kulikov (aka Semper-Viventem)
 */
class RoundedBackgroundColorSpan(
    @ColorInt backgroundColor: Int,
    @Px private val padding: Float,
    @Px private val radius: Float
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
    private var prevLeft = NO_INIT
    private var prevRight = NO_INIT
    private var prevBottom = NO_INIT
    private var prevTop = NO_INIT

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
        lnum: Int
    ) {

        val actualWidth = p.measureText(text, start, end) + 2f * padding

        val width = if (lnum == 0) {
            actualWidth
        } else if ((actualWidth < prevWidth) && (abs(prevWidth - actualWidth) < 2f * radius)) {
            prevWidth
        } else if ((actualWidth > prevWidth) && (abs(prevWidth - actualWidth) < 2f * radius)) {
            actualWidth + (2f * radius - abs(prevWidth - actualWidth))
        } else {
            actualWidth
        }

        val shiftLeft = 0f - padding
        val shiftRight = width + shiftLeft

        rect.set(shiftLeft, top.toFloat(), shiftRight, bottom.toFloat())

        c.drawRoundRect(rect, radius, radius, paint)

        if (lnum > 0) {
            drawCornerType1(c, path, rect, radius)

            when {
                prevWidth < width -> drawCornerType2(c, path, rect, radius)
                prevWidth > width -> drawCornerType3(c, path, rect, radius)
                else -> drawCornerType4(c, path, rect, radius)
            }
        }

        prevWidth = width
        prevLeft = rect.left
        prevRight = rect.right
        prevBottom = rect.bottom
        prevTop = rect.top
    }

    /**
     *
     *  Draw left corner
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
    private fun drawCornerType1(c: Canvas, path: Path, rect: RectF, radius: Float) {
        path.reset()
        path.moveTo(rect.left, rect.top + radius)
        path.lineTo(rect.left, rect.top - radius)
        path.lineTo(rect.left + radius, rect.top)
        path.lineTo(rect.left, rect.top + radius)

        c.drawPath(path, paint)
    }

    /**
     *
     * Draw top corner
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
    private fun drawCornerType2(c: Canvas, path: Path, rect: RectF, radius: Float) {
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
     * Draw bottom corner
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
    private fun drawCornerType3(c: Canvas, path: Path, rect: RectF, radius: Float) {
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
     * Draw right corner
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
    private fun drawCornerType4(c: Canvas, path: Path, rect: RectF, radius: Float) {
        path.reset()
        path.moveTo(rect.right, rect.top - radius)
        path.lineTo(rect.right, rect.top + radius)
        path.lineTo(rect.right - radius, rect.top)
        path.lineTo(rect.right, rect.top - radius)

        c.drawPath(path, paint)
    }
}
