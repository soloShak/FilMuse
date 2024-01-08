package edu.skku.cs.finalproject

import android.content.res.ColorStateList
import android.graphics.Color
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MovieDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        val movie = Movie(
            intent.getStringExtra("movieTitle"),
            intent.getStringExtra("posterPath"),
            intent.getStringExtra("rate"),
            intent.getStringExtra("releaseDate"),
            intent.getStringExtra("backPosterPath"),
            intent.getStringExtra("description"),
            intent.getStringExtra("id")
        )

        val backPosterView = findViewById<ImageView>(R.id.MovieBackPoster)
        val mainPosterView = findViewById<ImageView>(R.id.MovieDetailPoster)
        val titleView = findViewById<TextView>(R.id.MovieDetailTitle)
        val dateView = findViewById<TextView>(R.id.MovieDetailDate)
        val rateView = findViewById<TextView>(R.id.MovieDetailRate)
        val descrpView = findViewById<TextView>(R.id.MovieDetailDescrp)
        val addFavorBtn = findViewById<Button>(R.id.AddFavouriteButton)

        var imageMainUrl = ""
        var imageBackUrl = ""

        if (movie.poster_path == "null"){
            imageMainUrl =
                "https://upload.wikimedia.org/wikipedia/commons/thumb/6/65/No-Image-Placeholder.svg/495px-No-Image-Placeholder.svg.png?20200912122019"
        }
        else
            imageMainUrl = "https://image.tmdb.org/t/p/original" + movie.poster_path

        if (movie.poster_path == "null"){
            imageBackUrl =
                "https://upload.wikimedia.org/wikipedia/commons/thumb/6/65/No-Image-Placeholder.svg/495px-No-Image-Placeholder.svg.png?20200912122019"
        }
        else
            imageBackUrl = "https://image.tmdb.org/t/p/original" + movie.backPoster_path

        Glide.with(this)
            .load(imageBackUrl)
            .into(backPosterView)
        Glide.with(this)
            .load(imageMainUrl)
            .into(mainPosterView)

        titleView.text = movie.title
        dateView.text = "Release Date: " + movie.release_date
        descrpView.text = "Description: \n" + movie.description

        if (movie.rate!!.toFloat() >= 8.0)
            rateView.text = movie.rate+ " ★★★★★"
        else if (movie.rate!!.toFloat() >= 6.0)
            rateView.text = movie.rate+ " ★★★★"
        else if (movie.rate!!.toFloat() >= 4.0)
            rateView.text = movie.rate+ " ★★★"
        else if (movie.rate!!.toFloat() >= 2.0)
            rateView.text = movie.rate+ " ★★"
        else if (movie.rate!!.toFloat() >= 0)
            rateView.text = movie.rate+ " ★"

        addFavorBtn.setOnClickListener{
            if (!ifLoggedIn) {
                Toast.makeText(
                    this, "You have to Login first to add movies to your favourite " +
                            "list", Toast.LENGTH_SHORT
                ).show()
            }
            else {
                val fireStore = Firebase.firestore

                //var movieList: MutableList<String> = mutableListOf()
                fireStore.collection("users")
                    .get()
                    .addOnSuccessListener {documentReference ->
                        for (document in documentReference) {
                            if (document.id == accountEmail){
                                var movies = document["favouriteMovie"].toString()
                                if (!movies.contains(movie.id.toString())) {
                                    movies += movie.id + " @, "
                                    fireStore.collection("users").document(accountEmail)
                                        .update("favouriteMovie", movies)
                                }
                                else {
                                    Toast.makeText(
                                        this,
                                        "The movie is already in the favourite list",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                addFavorBtn.isClickable = false
                                addFavorBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#656363"))
                            }
                        }
                    }
                    .addOnFailureListener{
                        it.printStackTrace()
                        Log.w("Failure", "Error adding document")
                    }
            }
        }
    }
}