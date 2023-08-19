# NiceStarRating
A simple view to display the rating with stars

![изображение](https://github.com/evitwilly/NiceStarRating/assets/40917658/3b66e150-9bd9-4680-9c31-c3e9e4b80bfe)

## NiceStarRatingView options

`rating` - current rating value

`maxRating` - max rating value

    <ru.freeit.lib.NiceStarRatingView
        android:id="@+id/nice_rating_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:rating="4"
        app:maxRating="5" />        

Output:

![изображение](https://github.com/evitwilly/NiceStarRating/assets/40917658/d01ffbf7-62e9-49af-8dfd-38947d92f1f4)

___

`horizontalMargin` - margin between two neighboring stars 

`color` - color for drawing stars

    <ru.freeit.lib.NiceStarRatingView
        android:id="@+id/nice_rating_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:rating="4"
        app:maxRating="5"
        app:horizontalMargin="16dp"
        app:color="?colorPrimary" />

Output:

![изображение](https://github.com/evitwilly/NiceStarRating/assets/40917658/acaea073-01e3-42aa-a3e9-3fd8e2a60892)

___

`armNumber` - number of vertices on a star

`strokeWidth` - line thickness when the star is not filled

    <ru.freeit.lib.NiceStarRatingView
        android:id="@+id/nice_rating_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:rating="2"
        app:maxRating="4"
        app:horizontalMargin="12dp"
        app:color="?colorSecondary"
        app:armNumber="6"
        app:strokeWidth="3dp" />

Output:

![изображение](https://github.com/evitwilly/NiceStarRating/assets/40917658/8940c52f-8d7d-487d-b92b-18864779daa8)

___

`halfOpportunity` - the ability to put half the rating

    <ru.freeit.lib.NiceStarRatingView
        android:id="@+id/nice_rating_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:rating="3.5"
        app:halfOpportunity="true" />

Output:

![изображение](https://github.com/evitwilly/NiceStarRating/assets/40917658/cbe18b5c-ab62-47c0-bbbe-1d0496b08621)

___

`isAnimatingEnabled` - animation enabled or not

`starAnimationDuration` - animation duration for one star

    <ru.freeit.lib.NiceStarRatingView
        android:id="@+id/nice_rating_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:rating="3"
        app:isAnimatingEnabled="true"
        app:starAnimationDuration="60" />

Output:

[scaling_animation.webm](https://github.com/evitwilly/NiceStarRating/assets/40917658/0123f729-ce50-46b1-8029-11f88aba9813)

# How to add to your project?

It's easy! 

Just copy two files from `lib` module:

![изображение](https://github.com/evitwilly/NiceStarRating/assets/40917658/3c1f9cff-56bb-426c-ad69-c05ff1690bc5)

Enjoy!
