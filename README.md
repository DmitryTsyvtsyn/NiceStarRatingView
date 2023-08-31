# NiceStarRating
A simple view to display the rating with stars

[preview](https://github.com/evitwilly/NiceStarRating/assets/40917658/f7da01e5-c415-4bf6-8ffd-0c5d743ee563)

## NiceStarRatingView options

`rating` - current rating value

`maxRating` - max rating value

`starWidth` - star width in dp (by default the star is stretched, layout_weight=1)

    <ru.freeit.lib.NiceStarRatingView
        android:id="@+id/nice_rating_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:rating="4"
        app:maxRating="5"
        app:starWidth="48dp" />        

Output:

<img alt="screen1" src="https://github.com/evitwilly/NiceStarRating/assets/40917658/ee4d9dee-a030-4ce6-ab7f-772ac7bed19e" width="480px" />

___

`horizontalMargin` - margin between two neighboring stars 

`color` - color for drawing stars

    <ru.freeit.lib.NiceStarRatingView
        android:id="@+id/nice_rating_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:rating="4"
        app:maxRating="5"
        app:starWidth="48dp"
        app:horizontalMargin="16dp"
        app:color="?colorPrimary" />

Output:

<img alt="screen2" src="https://github.com/evitwilly/NiceStarRating/assets/40917658/dba8a4fd-232a-42c0-b9a2-55fe0fbc29fe" width="480px" />

___

`armNumber` - number of vertices on a star

`strokeWidth` - line thickness when the star is not filled

    <ru.freeit.lib.NiceStarRatingView
        android:id="@+id/nice_rating_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:rating="2"
        app:maxRating="4"
        app:starWidth="48dp"
        app:horizontalMargin="12dp"
        app:color="?colorSecondary"
        app:armNumber="6"
        app:strokeWidth="3dp" />

Output:

<img alt="screen3" src="https://github.com/evitwilly/NiceStarRating/assets/40917658/5eea0524-49c9-4dc4-b65d-0dab5edd4369" width="480px" />

___

`halfOpportunity` - the ability to put half the rating

    <ru.freeit.lib.NiceStarRatingView
        android:id="@+id/nice_rating_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:rating="3.5"
        app:starWidth="48dp"
        app:halfOpportunity="true" />

Output:

<img alt="screen4" src="https://github.com/evitwilly/NiceStarRating/assets/40917658/fdf064a5-8342-4023-a29e-cd6680eebde4" width="480px" />

___

`isAnimatingEnabled` - animation enabled or not

`starAnimationDuration` - animation duration for one star

    <ru.freeit.lib.NiceStarRatingView
        android:id="@+id/nice_rating_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:rating="3"
        app:starWidth="48dp"
        app:isAnimatingEnabled="true"
        app:starAnimationDuration="60" />

Output:

[scaling_animation.webm](https://github.com/evitwilly/NiceStarRating/assets/40917658/0123f729-ce50-46b1-8029-11f88aba9813)

Custom animator:

    // when we set a higher rating value than the previous one
    niceStarRatingView.animationRatingIncrease = { view ->
        val animator = ValueAnimator.ofFloat(0f, 45f, 0f)
        animator.addUpdateListener {
            view.rotation = it.animatedValue as Float
        }
        animator
    }
    
    // when we set a lower rating value than the previous one
    niceStarRatingView.animationRatingDecrease = { view ->
        val animator = ValueAnimator.ofFloat(0f, -45f, 0f)
        animator.addUpdateListener {
            view.rotation = it.animatedValue as Float
        }
        animator
    }

Output:

[rotation_animation.webm](https://github.com/evitwilly/NiceStarRating/assets/40917658/dd93d5c3-8e65-47b4-af35-03d871ad53b3)

# Demo app

You can clone the repository and open it in Android Studio then run:

![изображение](https://github.com/evitwilly/NiceStarRating/assets/40917658/5b963c2c-344c-4014-98ad-fe6433e4a58e)

# How to add to your project?

It's easy! 

Just copy two files from `lib` module:

![изображение](https://github.com/evitwilly/NiceStarRating/assets/40917658/3c1f9cff-56bb-426c-ad69-c05ff1690bc5)

Enjoy!
