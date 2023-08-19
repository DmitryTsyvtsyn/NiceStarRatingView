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



# How to add to your project?

It's easy! 

Just copy two files from `lib` module:

![изображение](https://github.com/evitwilly/NiceStarRating/assets/40917658/bbe24a9e-dffd-4ce5-8a21-993bdca3f1dc)

