package ru.freeit.lib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.Dimension.DP
import kotlin.math.cos
import kotlin.math.sin

class NiceRatingView @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    params: Params = Params()
) : LinearLayout(ctx, attrs, defStyleAttr) {

    var rating: Int = 3
        set(value) {
            field = value
            drawState()
        }

    private val density = resources.displayMetrics.density
    private val Int.dp
        get() = (this * density).toInt()

    private val views = mutableListOf<RatingTextView>()

    private var maxRating = params.maxRating
    private var horizontalMargin = params.horizontalMargin.dp
    private var color = params.color

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_HORIZONTAL

        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.NiceRatingView, 0, 0)
        maxRating = typedArray.getInteger(R.styleable.NiceRatingView_maxRating, maxRating)
        horizontalMargin = typedArray.getDimensionPixelSize(R.styleable.NiceRatingView_horizontalMargin, horizontalMargin)
        color = typedArray.getColor(R.styleable.NiceRatingView_color, color)
        typedArray.recycle()

        views.addAll(List(maxRating) { index ->
            val ratingView = RatingTextView(context, color)
            ratingView.hasSelected = index < rating
            ratingView.setOnClickListener {
                val newRating = index + 1
                rating = if (rating == newRating) {
                    0
                } else {
                    newRating
                }
            }
            val layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT)
            layoutParams.weight = 1f
            layoutParams.marginStart = if (index > 0) horizontalMargin else 0
            ratingView.layoutParams = layoutParams
            addView(ratingView)
            ratingView
        })
    }

    private fun drawState() {
        views.forEachIndexed { index, view ->
            view.hasSelected = index < rating
        }
    }

    class Params(
        val maxRating: Int = 5,
        @Dimension(unit = DP) val horizontalMargin: Int = 16,
        @ColorInt val color: Int = Color.rgb(255, 239, 0)
    )

    private class RatingTextView(
        ctx: Context,
        private val color: Int
    ): View(ctx) {

        var hasSelected: Boolean = false
            set(value) {
                field = value
                drawState()
            }

        private val density = resources.displayMetrics.density
        private val Float.dp
            get() = this * density

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 1.5f.dp
        }
        private val path = Path()

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)

            val size = w.toFloat()
            val outerRadius = size / 2f
            val innerRadius = size / 4f
            var angle = (Math.PI / 2 * 3).toFloat()
            path.reset()

            path.moveTo(outerRadius, 0f)
            var pathSegmentIndex = 0
            while (pathSegmentIndex < arm_number) {
                val startSegmentX = outerRadius + cos(angle) * outerRadius
                val startSegmentY = outerRadius + sin(angle) * outerRadius
                path.lineTo(startSegmentX, startSegmentY)

                angle += step

                val endSegmentX = outerRadius + cos(angle) * innerRadius
                val endSegmentY = outerRadius + sin(angle) * innerRadius
                path.lineTo(endSegmentX, endSegmentY)

                angle += step

                pathSegmentIndex++
            }
            path.lineTo(outerRadius, 0f)
            path.close()

        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawPath(path, paint)
        }

        private fun drawState() {
            paint.color = color
            paint.style = if (hasSelected) {
                Paint.Style.FILL
            } else {
                Paint.Style.STROKE
            }
            invalidate()
        }

        private companion object {
            const val arm_number = 5
            const val step = (Math.PI / arm_number).toFloat()
        }

    }

}