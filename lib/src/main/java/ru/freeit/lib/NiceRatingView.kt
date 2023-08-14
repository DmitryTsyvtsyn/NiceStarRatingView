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
import androidx.annotation.Dimension.PX
import kotlin.math.cos
import kotlin.math.sin

class NiceRatingView @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    params: Params = Params()
) : LinearLayout(ctx, attrs, defStyleAttr) {

    private var params = params.updatedParamsFromXmlAttributes(context, attrs)

    var rating: Int = params.rating
        set(value) {
            field = value
            drawState()
        }

    var onRatingListener: (Int) -> Unit = {}

    private val views = mutableListOf<RatingTextView>()

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_HORIZONTAL

        redrawViews()
    }

    fun updateParams(mapper: (Params) -> Params) {
        params = mapper.invoke(params)
        removeAllViews()
        redrawViews()

        val newParamsRating = params.rating
        if (newParamsRating != rating) {
            rating = newParamsRating
        }
    }

    private fun redrawViews() {
        views.clear()

        val ratingViewColor = params.color
        val ratingViewArmNumber = params.armNumber
        val ratingViewStrokeWidth = params.strokeWidth.toFloat()
        val ratingViewHorizontalMargin = params.horizontalMargin

        val ctx = context

        views.addAll(List(params.maxRating) { index ->
            val ratingView = RatingTextView(ctx, ratingViewColor, ratingViewArmNumber, ratingViewStrokeWidth)
            ratingView.hasSelected = index < rating
            ratingView.setOnClickListener {
                val newRating = index + 1
                rating = if (rating == newRating) {
                    0
                } else {
                    newRating
                }
                onRatingListener.invoke(rating)
            }
            val layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT)
            layoutParams.weight = 1f
            layoutParams.marginStart = if (index > 0) ratingViewHorizontalMargin else 0
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

    data class Params(
        val rating: Int = 4,
        val maxRating: Int = 5,
        @Dimension(unit = PX) val horizontalMargin: Int = 0,
        @ColorInt val color: Int = Color.rgb(255, 239, 0),
        val armNumber: Int = 5,
        @Dimension(unit = PX) val strokeWidth: Int = 0
    ) {

        private fun Context.dp(value: Int): Int {
            return (resources.displayMetrics.density * value).toInt()
        }

        private fun Context.dp(value: Float): Int {
            return (resources.displayMetrics.density * value).toInt()
        }

        fun updatedParamsFromXmlAttributes(context: Context, attrs: AttributeSet?): Params {
            val oldHorizontalMargin = if (horizontalMargin <= 0) context.dp(defaultHorizontalMargin) else horizontalMargin
            val oldStrokeWidth = if (strokeWidth <= 0f) context.dp(defaultStrokeWidth) else strokeWidth

            val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.NiceRatingView, 0, 0)
            val rating = typedArray.getInteger(R.styleable.NiceRatingView_rating, rating)
            val maxRating = typedArray.getInteger(R.styleable.NiceRatingView_maxRating, maxRating)
            val horizontalMargin = typedArray.getDimensionPixelSize(R.styleable.NiceRatingView_horizontalMargin, oldHorizontalMargin)
            val color = typedArray.getColor(R.styleable.NiceRatingView_color, color)
            val armNumber = typedArray.getInteger(R.styleable.NiceRatingView_armNumber, armNumber)
            val strokeWidth = typedArray.getDimensionPixelSize(R.styleable.NiceRatingView_strokeWidth, oldStrokeWidth)
            typedArray.recycle()

            return Params(rating, maxRating, horizontalMargin, color, armNumber, strokeWidth)
        }

        private companion object {
            const val defaultHorizontalMargin = 16
            const val defaultStrokeWidth = 1.5f
        }

    }

    private class RatingTextView(
        ctx: Context,
        private val color: Int,
        private val armNumber: Int,
        strokeWidth: Float
    ): View(ctx) {

        var hasSelected: Boolean = false
            set(value) {
                field = value
                drawState()
            }

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val path = Path()

        init {
            paint.strokeWidth = strokeWidth
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)

            val size = w.toFloat()
            val outerRadius = size / 2f
            val innerRadius = size / 4f
            var angle = (Math.PI / 2 * 3).toFloat()
            val step = (Math.PI / armNumber).toFloat()

            path.reset()
            path.moveTo(outerRadius, 0f)
            var pathSegmentIndex = 0
            while (pathSegmentIndex < armNumber) {
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

    }

}