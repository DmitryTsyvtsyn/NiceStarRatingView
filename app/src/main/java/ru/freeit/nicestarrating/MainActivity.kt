package ru.freeit.nicestarrating

import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ru.freeit.lib.NiceRatingView

class MainActivity : AppCompatActivity() {

    private val Int.dp
        get() = (this * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val niceRatingView1 = findViewById<NiceRatingView>(R.id.nice_rating_view_1)
        niceRatingView1.onRatingListener = {
            Log.d(TAG, "nice rating view 1 -> $it")
        }

        val niceRatingView2 = findViewById<NiceRatingView>(R.id.nice_rating_view_2)
        niceRatingView2.animationRatingIncrease = { view ->
            val animator = ValueAnimator.ofFloat(0f, 45f, 0f)
            animator.addUpdateListener {
                view.rotation = it.animatedValue as Float
            }
            animator
        }
        niceRatingView2.animationRatingDecrease = { view ->
            val animator = ValueAnimator.ofFloat(0f, -45f, 0f)
            animator.addUpdateListener {
                view.rotation = it.animatedValue as Float
            }
            animator
        }
        niceRatingView2.updateParams { params ->
            params.copy(
                rating = 1f,
                maxRating = 3,
                horizontalMargin = 24.dp,
                starAnimationDuration = 100
            )
        }
        niceRatingView2.onRatingListener = {
            Log.d(TAG, "nice rating view 2 -> $it")
        }

        val niceRatingView3 = findViewById<NiceRatingView>(R.id.nice_rating_view_3)
        niceRatingView3.onRatingListener = {
            Log.d(TAG, "nice rating view 3 -> $it")
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}