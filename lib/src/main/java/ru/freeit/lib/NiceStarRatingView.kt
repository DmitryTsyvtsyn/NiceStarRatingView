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

class NiceStarRatingView @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    outerParams: Params = Params()
) : LinearLayout(ctx, attrs, defStyleAttr) {

    private var params = outerParams.updatedParamsFromXmlAttributes(context, attrs)

    private var internalRating: Float = params.rating
        set(value) {
            val newRating = when {
                field == value -> 0f
                params.halfOpportunity && value.checkFraction(0.5f) -> value
                else -> floor(value)
            }
            params = params.copy(rating = newRating)
            field = newRating
        }

    /**
     * The current rating
     */
    var rating: Float
        get() = internalRating
        set(value) {
            internalRating = value
            drawState()
        }

    /**
     * The callback that fires when the rating changes
     */
    var onRatingListener: (Float) -> Unit = {}

    /**
     * Animator for one star view that triggers when the rating increases
     */
    var animationRatingIncrease: (View) -> Animator = { view ->
        val animator = ValueAnimator.ofFloat(1f, 1.5f, 1f)
        animator.addUpdateListener {
            view.scaleX = it.animatedValue as Float
            view.scaleY = it.animatedValue as Float
        }
        animator
    }

    /**
     * Animator for one star view that triggers when the rating decreases
     */
    var animationRatingDecrease: (View) -> Animator = { view ->
        val animator = ValueAnimator.ofFloat(1f, 0.5f, 1f)
        animator.addUpdateListener {
            view.scaleX = it.animatedValue as Float
            view.scaleY = it.animatedValue as Float
        }
        animator
    }

    private val views = mutableListOf<RatingTextView>()

    private var animatorSet: AnimatorSet? = null

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_HORIZONTAL

        clipToPadding = false

        redrawViews()
    }

    /**
     * Changes params using a lambda [mapper] that accepts current params and returns updated ones
     */
    fun updateParams(mapper: (Params) -> Params) {
        val newParams = mapper.invoke(params)

        if (newParams.rating != internalRating) {
            internalRating = params.rating
        }

        if (params.halfOpportunity && newParams.halfOpportunity.not()) {
            val roundedRating = floor(params.rating)
            if (internalRating != roundedRating) {
                internalRating = roundedRating
            }
        }

        if (internalRating > newParams.maxRating) {
            internalRating = newParams.maxRating.toFloat()
        }

        params = newParams

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
        val ratingViewStarWidth = params.starWidth

        val ctx = context

        views.addAll(List(params.maxRating) { index ->
            val ratingView = RatingTextView(
                ctx = ctx,
                color = ratingViewColor,
                armNumber = ratingViewArmNumber,
                strokeWidth = ratingViewStrokeWidth,
                halfOpportunity = ratingViewHalfOpportunity
            )
            ratingView.selectedState = when {
                index < floor(internalRating) -> RatingTextView.RatingStarSelectedState.SELECTED
                internalRating > 0f && internalRating.checkFraction(0.5f) && index == floor(internalRating).toInt() -> RatingTextView.RatingStarSelectedState.HALF_SELECTED
                else -> RatingTextView.RatingStarSelectedState.UNSELECTED
            }
            ratingView.clickListener = { clickType ->
                internalRating = if (params.halfOpportunity && clickType == RatingTextView.RatingStarClickType.FIRST_HALF_CLICK) index + 0.5f else index + 1f
                onRatingListener.invoke(internalRating)
                drawState(isAnimatingEnabled = params.isAnimatingEnabled)
            }
            val layoutParams = LayoutParams(ratingViewStarWidth, LayoutParams.WRAP_CONTENT)
            if (ratingViewStarWidth == 0) {
                layoutParams.weight = 1f
            }
            layoutParams.marginStart = if (index > 0) ratingViewHorizontalMargin else 0
            ratingView.layoutParams = layoutParams
            addView(ratingView)
            ratingView
        })
    }

    private fun drawState(isAnimatingEnabled: Boolean = false) {
        animatorSet?.cancel()

        val animators = mutableListOf<Animator>()
        var animationDirection = RatingTextView.RatingStarAnimationDirection.INCREASE
        views.forEachIndexed { index, view ->
            val newSelectedState = when {
                index < floor(internalRating) -> RatingTextView.RatingStarSelectedState.SELECTED
                internalRating > 0f && internalRating.checkFraction(0.5f) && index == floor(internalRating).toInt() -> RatingTextView.RatingStarSelectedState.HALF_SELECTED
                else -> RatingTextView.RatingStarSelectedState.UNSELECTED
            }
            if (isAnimatingEnabled) {
                val animationDirectionWithAnimatorLambda = view.getAnimationDirectionWithAnimatorLambda(newSelectedState, animationRatingIncrease, animationRatingDecrease)
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
        @ColorInt val color: Int = Color.rgb(255, 215, 0),
        val armNumber: Int = 5,
        @Dimension(unit = PX) val strokeWidth: Int = 0,
        val halfOpportunity: Boolean = false,
        val isAnimatingEnabled: Boolean = false,
        val starAnimationDuration: Int = 60,
        val starWidth: Int = 0,
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

            val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.NiceStarRatingView, 0, 0)
            val newRating = typedArray.getFloat(R.styleable.NiceStarRatingView_rating, rating)
            val rating = if (newRating.checkFraction(0.5f)) {
                newRating
            } else {
                floor(newRating)
            }
            val maxRating = typedArray.getInteger(R.styleable.NiceStarRatingView_maxRating, maxRating)
            val horizontalMargin = typedArray.getDimensionPixelSize(R.styleable.NiceStarRatingView_horizontalMargin, oldHorizontalMargin)
            val color = typedArray.getColor(R.styleable.NiceStarRatingView_color, color)
            val armNumber = typedArray.getInteger(R.styleable.NiceStarRatingView_armNumber, armNumber)
            val strokeWidth = typedArray.getDimensionPixelSize(R.styleable.NiceStarRatingView_strokeWidth, oldStrokeWidth)
            val halfOpportunity = typedArray.getBoolean(R.styleable.NiceStarRatingView_halfOpportunity, halfOpportunity)
            val isAnimatingEnabled = typedArray.getBoolean(R.styleable.NiceStarRatingView_isAnimatingEnabled, isAnimatingEnabled)
            val starAnimationDuration = typedArray.getInteger(R.styleable.NiceStarRatingView_starAnimationDuration, starAnimationDuration)
            val starWidth = typedArray.getDimensionPixelSize(R.styleable.NiceStarRatingView_starWidth, starWidth)
            typedArray.recycle()

            return Params(rating, maxRating, horizontalMargin, color, armNumber, strokeWidth, halfOpportunity, isAnimatingEnabled, starAnimationDuration, starWidth)
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
        private val halfOpportunity: Boolean
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

        fun getAnimationDirectionWithAnimatorLambda(
            newSelectedState: RatingStarSelectedState,
            animationRatingIncrease: (View) -> Animator,
            animationRatingDecrease: (View) -> Animator
        ): Pair<RatingStarAnimationDirection, (View) -> Animator>? =
            when {
                selectedState == RatingStarSelectedState.SELECTED && newSelectedState == RatingStarSelectedState.UNSELECTED -> RatingStarAnimationDirection.DECREASE to animationRatingDecrease
                selectedState == RatingStarSelectedState.SELECTED && newSelectedState == RatingStarSelectedState.HALF_SELECTED -> RatingStarAnimationDirection.DECREASE to animationRatingDecrease
                selectedState == RatingStarSelectedState.HALF_SELECTED && newSelectedState == RatingStarSelectedState.UNSELECTED -> RatingStarAnimationDirection.DECREASE to animationRatingDecrease
                selectedState == RatingStarSelectedState.UNSELECTED && newSelectedState == RatingStarSelectedState.SELECTED -> RatingStarAnimationDirection.INCREASE to animationRatingIncrease
                selectedState == RatingStarSelectedState.UNSELECTED && newSelectedState == RatingStarSelectedState.HALF_SELECTED -> RatingStarAnimationDirection.INCREASE to animationRatingIncrease
                selectedState == RatingStarSelectedState.HALF_SELECTED && newSelectedState == RatingStarSelectedState.SELECTED -> RatingStarAnimationDirection.INCREASE to animationRatingIncrease
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