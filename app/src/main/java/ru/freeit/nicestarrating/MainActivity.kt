package ru.freeit.nicestarrating

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import ru.freeit.lib.NiceRatingView

class MainActivity : AppCompatActivity() {

    private val Int.dp
        get() = (this * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val linearLayoutView = findViewById<LinearLayout>(R.id.linear_layout)
        val ratingView1 = NiceRatingView(
            ctx = this,
            params = NiceRatingView.Params(
                maxRating = 5,
                horizontalMargin = 8.dp,
                color = Color.rgb(244, 196, 48)
            )
        )
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.margin_medium)
        ratingView1.layoutParams = layoutParams
        linearLayoutView.addView(ratingView1)

        val ratingView2 = NiceRatingView(this)
        ratingView2.layoutParams = layoutParams
        ratingView2.updateParams { params ->
            params.copy(
                rating = 1,
                maxRating = 4,
                horizontalMargin = 24.dp,
                color = Color.rgb(240, 15, 15),
                armNumber = 4,
                strokeWidth = 3.dp
            )
        }
        linearLayoutView.addView(ratingView2)
    }

}