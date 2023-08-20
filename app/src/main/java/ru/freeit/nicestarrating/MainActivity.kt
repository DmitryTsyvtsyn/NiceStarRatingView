package ru.freeit.nicestarrating

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.RadioGroup
import com.google.android.material.slider.Slider
import ru.freeit.lib.NiceStarRatingView

class MainActivity : AppCompatActivity() {

    private val Float.dp
        get() = (this * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val niceStarRatingView1 = findViewById<NiceStarRatingView>(R.id.nice_rating_view)
        niceStarRatingView1.onRatingListener = {
            Log.d(TAG, "nice rating view -> $it")
        }

        val maxRatingGroupView = findViewById<RadioGroup>(R.id.max_rating_group_view)
        maxRatingGroupView.setOnCheckedChangeListener { _, checkedId ->
            val maxRating = when (checkedId) {
                R.id.two_max_rating_view -> 2
                R.id.three_max_rating_view -> 3
                R.id.four_max_rating_view -> 4
                R.id.five_max_rating_view -> 5
                R.id.six_max_rating_view -> 6
                else -> 6
            }
            niceStarRatingView1.updateParams { params ->
                params.copy(maxRating = maxRating)
            }
        }
        maxRatingGroupView.check(R.id.five_max_rating_view)

        val colorGroupView = findViewById<RadioGroup>(R.id.color_group_view)
        colorGroupView.setOnCheckedChangeListener { _, checkedId ->
            val color = when (checkedId) {
                R.id.orange_view -> getColor(R.color.orange)
                R.id.purple_view -> getColor(R.color.purple)
                R.id.teal_view -> getColor(R.color.teal)
                R.id.pink_view -> getColor(R.color.pink)
                R.id.red_view -> getColor(R.color.red)
                else -> 6
            }
            niceStarRatingView1.updateParams { params ->
                params.copy(color = color)
            }
        }
        colorGroupView.check(R.id.orange_view)

        val armsGroupView = findViewById<RadioGroup>(R.id.arm_group_view)
        armsGroupView.setOnCheckedChangeListener { _, checkedId ->
            val armNumber = when (checkedId) {
                R.id.four_arms_view -> 4
                R.id.five_arms_view -> 5
                R.id.six_arms_view -> 6
                R.id.seven_arms_view -> 7
                else -> 5
            }
            niceStarRatingView1.updateParams { params ->
                params.copy(armNumber = armNumber)
            }
        }
        armsGroupView.check(R.id.five_arms_view)

        val startStrokeWidth = 1.5f
        val endStrokeWidth = 5f
        val strokeWidthView = findViewById<Slider>(R.id.stroke_width_view)
        strokeWidthView.addOnChangeListener { _, value, fromUser ->
            val strokeWidth = value * (endStrokeWidth - startStrokeWidth) + startStrokeWidth
            niceStarRatingView1.updateParams { params ->
                params.copy(strokeWidth = strokeWidth.dp)
            }
        }
        strokeWidthView.value = 0f

        val halfOpportunityView = findViewById<CheckBox>(R.id.half_opportunity_view)
        halfOpportunityView.setOnCheckedChangeListener { _, checked ->
            niceStarRatingView1.updateParams { params ->
                params.copy(halfOpportunity = checked)
            }
        }
        halfOpportunityView.isChecked = false

        val animationEnabledView = findViewById<CheckBox>(R.id.animation_enabled_view)
        animationEnabledView.setOnCheckedChangeListener { _, checked ->
            niceStarRatingView1.updateParams { params ->
                params.copy(isAnimatingEnabled = checked)
            }
        }
        animationEnabledView.isChecked = false

    }

    companion object {
        private const val TAG = "MainActivity"
    }

}