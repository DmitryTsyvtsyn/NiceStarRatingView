package ru.freeit.lib

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.Dimension.PX
import androidx.core.animation.doOnEnd
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

class NiceRatingView @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    params: Params = Params()
) : LinearLayout(ctx, attrs, defStyleAttr) {

    private var params = params.updatedParamsFromXmlAttributes(context, attrs)

    private var internalRating: Float = params.rating
        set(value) {
            field = when {
                field == value -> 0f
                params.halfOpportunity && value.checkFraction(0.5f) -> value
                else -> floor(value)
            }
        }
    var rating: Float
        get() = internalRating
        set(value) {
            internalRating = value
            drawState()
        }

    var onRatingListener: (Float) -> Unit = {}
    var animationRatingIncrease: (View) -> Animator = { view ->
        val animator = ValueAnimator.ofFloat(1f, 1.5f, 1f)
        animator.addUpdateListener {
            view.scaleX = it.animatedValue as Float
            view.scaleY = it.animatedValue as Float
        }
        animator
    }
    var animationRatingDecrease: (View) -> Animator = { view ->
        val animator = ValueAnimator.ofFloat(1f, 0.5f, 1f)
        animator.addUpdateListener {
            view.scaleX = it.animatedValue as Float
            view.scaleY = it.animatedValue as Float
        }
        animator
    }

    private val views = mutableListOf<RatingTextView>()

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_HORIZONTAL

        redrawViews()
    }

    fun updateParams(mapper: (Params) -> Params) {
        params = mapper.invoke(params)
        internalRating = params.rating
        removeAllViews()
        redrawViews()
    }

    private fun redrawViews() {
        views.clear()

        val ratingViewColor = params.color
        val ratingViewArmNumber = params.armNumber
        val ratingViewStrokeWidth = params.strokeWidth.toFloat()
        val ratingViewHorizontalMargin = params.horizontalMargin
        val ratingViewHalfOpportunity = params.halfOpportunity

        val ctx = context

        views.addAll(List(params.maxRating) { index ->
            val ratingView = RatingTextView(
                ctx = ctx,
                color = ratingViewColor,
                armNumber = ratingViewArmNumber,
                strokeWidth = ratingViewStrokeWidth,
                halfOpportunity = ratingViewHalfOpportunity,
                animationRatingIncrease = animationRatingIncrease,
                animationRatingDecrease = animationRatingDecrease
            )
            ratingView.selectedState = when {
                index < floor(rating) -> RatingTextView.RatingStarSelectedState.SELECTED
                rating != 0f && rating.checkFraction(0.5f) && index == floor(rating).toInt() -> RatingTextView.RatingStarSelectedState.HALF_SELECTED
                else -> RatingTextView.RatingStarSelectedState.UNSELECTED
            }
            ratingView.clickListener = { clickType ->
                internalRating = if (params.halfOpportunity && clickType == RatingTextView.RatingStarClickType.FIRST_HALF_CLICK) index + 0.5f else index + 1f
                onRatingListener.invoke(rating)
                drawState(isAnimatingEnabled = params.isAnimatingEnabled)
            }
            val layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT)
            layoutParams.weight = 1f
            layoutParams.marginStart = if (index > 0) ratingViewHorizontalMargin else 0
            ratingView.layoutParams = layoutParams
            addView(ratingView)
            ratingView
        })
    }

    private var animatorSet: AnimatorSet? = null

    private fun drawState(isAnimatingEnabled: Boolean = false) {
        animatorSet?.cancel()

        val animators = mutableListOf<Animator>()
        var animationDirection = RatingTextView.RatingStarAnimationDirection.INCREASE
        views.forEachIndexed { index, view ->
            val oldSelectedState = view.selectedState
            val newSelectedState = when {
                index < floor(rating) -> RatingTextView.RatingStarSelectedState.SELECTED
                rating != 0f && rating.checkFraction(0.5f) && index == floor(rating).toInt() -> RatingTextView.RatingStarSelectedState.HALF_SELECTED
                else -> RatingTextView.RatingStarSelectedState.UNSELECTED
            }
            if (isAnimatingEnabled) {
                val animationDirectionWithAnimatorLambda = view.getAnimationDirectionWithAnimatorLambda(oldSelectedState, newSelectedState)
                if (animationDirectionWithAnimatorLambda != null) {
                    animationDirection = animationDirectionWithAnimatorLambda.first
                    val animator = animationDirectionWithAnimatorLambda.second.invoke(view)
                    animator.duration = params.starAnimationDuration.toLong()
                    animator.doOnEnd { view.selectedState = newSelectedState }
                    animators.add(animator)
                }
            } else {
                view.selectedState = newSelectedState
            }
        }
        if (animators.isNotEmpty()) {
            val animatorSet = AnimatorSet()
            animatorSet.playSequentially(if (animationDirection == RatingTextView.RatingStarAnimationDirection.INCREASE) animators else animators.reversed())
            animatorSet.start()
            this.animatorSet = animatorSet
        }
    }

    data class Params(
        val rating: Float = 4f,
        val maxRating: Int = 5,
        @Dimension(unit = PX) val horizontalMargin: Int = 0,
        @ColorInt val color: Int = Color.rgb(255, 239, 0),
        val armNumber: Int = 5,
        @Dimension(unit = PX) val strokeWidth: Int = 0,
        val halfOpportunity: Boolean = false,
        val isAnimatingEnabled: Boolean = false,
        val starAnimationDuration: Int = 60
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
            val newRating = typedArray.getFloat(R.styleable.NiceRatingView_rating, rating)
            val rating = if (newRating.checkFraction(0.5f)) {
                newRating
            } else {
                floor(newRating)
            }
            val maxRating = typedArray.getInteger(R.styleable.NiceRatingView_maxRating, maxRating)
            val horizontalMargin = typedArray.getDimensionPixelSize(R.styleable.NiceRatingView_horizontalMargin, oldHorizontalMargin)
            val color = typedArray.getColor(R.styleable.NiceRatingView_color, color)
            val armNumber = typedArray.getInteger(R.styleable.NiceRatingView_armNumber, armNumber)
            val strokeWidth = typedArray.getDimensionPixelSize(R.styleable.NiceRatingView_strokeWidth, oldStrokeWidth)
            val halfOpportunity = typedArray.getBoolean(R.styleable.NiceRatingView_halfOpportunity, halfOpportunity)
            val isAnimatingEnabled = typedArray.getBoolean(R.styleable.NiceRatingView_isAnimatingEnabled, isAnimatingEnabled)
            val starAnimationDuration = typedArray.getInteger(R.styleable.NiceRatingView_starAnimationDuration, starAnimationDuration)
            typedArray.recycle()

            return Params(rating, maxRating, horizontalMargin, color, armNumber, strokeWidth, halfOpportunity, isAnimatingEnabled, starAnimationDuration)
        }

        private companion object {
            const val defaultHorizontalMargin = 16
            const val defaultStrokeWidth = 1.5f
        }

    }

    private class RatingTextView(
        ctx: Context,
        color: Int,
        private val armNumber: Int,
        private val strokeWidth: Float,
        private val halfOpportunity: Boolean,
        private val animationRatingIncrease: (View) -> Animator,
        private val animationRatingDecrease: (View) -> Animator
    ): View(ctx) {

        var selectedState: RatingStarSelectedState = RatingStarSelectedState.UNSELECTED
            set(value) {
                field = value
                invalidate()
            }

        var clickListener: (RatingStarClickType) -> Unit = {}

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val path = Path()
        private val filledHalfPath = Path()

        init {
            paint.color = color
            paint.strokeWidth = strokeWidth

            isClickable = true
            isFocusable = true
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)

            val size = w.toFloat()
            val padding = strokeWidth

            val outerRadius = size / 2f - padding
            val innerRadius = size / 4f - padding
            var angle = (Math.PI / 2 * 3).toFloat()
            val step = (Math.PI / armNumber).toFloat()

            val isOddNumberOfArms = armNumber % 2 != 0
            val halfSegmentIndex = armNumber / 2
            var halfSegmentX = 0f
            var halfSegmentY = 0f
            var halfAngle = angle

            path.reset()
            path.moveTo(outerRadius + padding, padding)
            var pathSegmentIndex = 0
            while (pathSegmentIndex < armNumber) {
                val startSegmentX = outerRadius + cos(angle) * outerRadius + padding
                val startSegmentY = outerRadius + sin(angle) * outerRadius + padding
                path.lineTo(startSegmentX, startSegmentY)

                if (pathSegmentIndex == halfSegmentIndex) {
                    halfSegmentX = startSegmentX
                    halfSegmentY = startSegmentY
                    halfAngle = angle
                }

                angle += step

                val endSegmentX = outerRadius + cos(angle) * innerRadius + padding
                val endSegmentY = outerRadius + sin(angle) * innerRadius + padding
                path.lineTo(endSegmentX, endSegmentY)

                if (isOddNumberOfArms && pathSegmentIndex == halfSegmentIndex) {
                    halfSegmentX = endSegmentX
                    halfSegmentY = endSegmentY
                }

                angle += step

                pathSegmentIndex++
            }
            path.lineTo(outerRadius + padding, padding)
            path.close()

            if (halfOpportunity) {
                filledHalfPath.reset()
                filledHalfPath.moveTo(halfSegmentX, halfSegmentY)
                angle = halfAngle
                pathSegmentIndex = halfSegmentIndex
                while (pathSegmentIndex < armNumber) {

                    val startSegmentX = outerRadius + cos(angle) * outerRadius + padding
                    val startSegmentY = outerRadius + sin(angle) * outerRadius + padding
                    filledHalfPath.lineTo(startSegmentX, startSegmentY)

                    angle += step

                    val endSegmentX = outerRadius + cos(angle) * innerRadius + padding
                    val endSegmentY = outerRadius + sin(angle) * innerRadius + padding
                    filledHalfPath.lineTo(endSegmentX, endSegmentY)

                    angle += step

                    pathSegmentIndex++
                }
                filledHalfPath.lineTo(outerRadius + padding, padding)
            }

        }

        override fun onDraw(canvas: Canvas) {
            paint.style = when {
                selectedState == RatingStarSelectedState.SELECTED -> Paint.Style.FILL_AND_STROKE
                !halfOpportunity && selectedState == RatingStarSelectedState.HALF_SELECTED -> Paint.Style.FILL
                else -> Paint.Style.STROKE
            }
            canvas.drawPath(path, paint)

            if (halfOpportunity && selectedState == RatingStarSelectedState.HALF_SELECTED) {
                paint.style = Paint.Style.FILL
                canvas.drawPath(filledHalfPath, paint)
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouchEvent(event: MotionEvent): Boolean {
            return when (event.action) {
                MotionEvent.ACTION_UP -> {
                    clickListener.invoke(if (event.x <= width / 2f) {
                        RatingStarClickType.FIRST_HALF_CLICK
                    } else {
                        RatingStarClickType.SECOND_HALF_CLICK
                    })
                    performClick()
                    true
                }
                else -> super.onTouchEvent(event)
            }
        }

        fun getAnimationDirectionWithAnimatorLambda(oldSelectedState: RatingStarSelectedState, newSelectedState: RatingStarSelectedState): Pair<RatingStarAnimationDirection, (View) -> Animator>? =
            when {
                oldSelectedState == RatingStarSelectedState.SELECTED && newSelectedState == RatingStarSelectedState.UNSELECTED -> RatingStarAnimationDirection.DECREASE to animationRatingDecrease
                oldSelectedState == RatingStarSelectedState.SELECTED && newSelectedState == RatingStarSelectedState.HALF_SELECTED -> RatingStarAnimationDirection.DECREASE to animationRatingDecrease
                oldSelectedState == RatingStarSelectedState.HALF_SELECTED && newSelectedState == RatingStarSelectedState.UNSELECTED -> RatingStarAnimationDirection.DECREASE to animationRatingDecrease
                oldSelectedState == RatingStarSelectedState.UNSELECTED && newSelectedState == RatingStarSelectedState.SELECTED -> RatingStarAnimationDirection.INCREASE to animationRatingIncrease
                oldSelectedState == RatingStarSelectedState.UNSELECTED && newSelectedState == RatingStarSelectedState.HALF_SELECTED -> RatingStarAnimationDirection.INCREASE to animationRatingIncrease
                oldSelectedState == RatingStarSelectedState.HALF_SELECTED && newSelectedState == RatingStarSelectedState.SELECTED -> RatingStarAnimationDirection.INCREASE to animationRatingIncrease
                else -> null
            }

        enum class RatingStarAnimationDirection {
            INCREASE, DECREASE
        }

        enum class RatingStarSelectedState {
            SELECTED, HALF_SELECTED, UNSELECTED
        }

        enum class RatingStarClickType {
            FIRST_HALF_CLICK, SECOND_HALF_CLICK
        }

    }

}

private fun Float.checkFraction(value: Float): Boolean {
    return (this - floor(this)) == value
}