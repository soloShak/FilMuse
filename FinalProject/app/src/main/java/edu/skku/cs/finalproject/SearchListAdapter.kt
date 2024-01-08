package edu.skku.cs.finalproject

import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class SearchListAdapter (val context : Context, val items:List<Movie>, val isFromFavList: Boolean): BaseAdapter() {
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(p0: Int): Any {
        return items.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.general_movie_layout, null)
        val movie = items[p0]

        val deleteFromListBtn = view.findViewById<Button>(R.id.DeleteFavListButton)
        val movieLayout = view.findViewById<ConstraintLayout>(R.id.MovieLayout)
        val imageView = view.findViewById<ImageView>(R.id.MoviePoster)
        val titleView = view.findViewById<TextView>(R.id.MovieTitle)
        val rateView = view.findViewById<TextView>(R.id.MovieRate)
        val dateView = view.findViewById<TextView>(R.id.MovieDate)
        var imageUrl = ""

        if (isFromFavList) {
            deleteFromListBtn.isVisible = true
            deleteFromListBtn.isEnabled = true
        }

        val scaledPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            18f,
            context.resources.displayMetrics
        )
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, scaledPixels)
        rateView.setTextSize(TypedValue.COMPLEX_UNIT_PX, scaledPixels)
        dateView.setTextSize(TypedValue.COMPLEX_UNIT_PX, scaledPixels)

        titleView.text = movie.title
        rateView.text = "★ " + movie.rate
        dateView.text = "Release Date: " + movie.release_date

        if (movie.poster_path == "null"){
            imageUrl =
                "https://upload.wikimedia.org/wikipedia/commons/thumb/6/65/No-Image-Placeholder.svg/495px-No-Image-Placeholder.svg.png?20200912122019"
        }
        else
            imageUrl = "https://image.tmdb.org/t/p/original" + movie.poster_path

        Glide.with(context)
            .load(imageUrl)
            .into(imageView)

        movieLayout.setOnClickListener{
            val intent = Intent(context, MovieDetailsActivity::class.java)

            intent.putExtra("movieTitle", movie.title.toString())
            intent.putExtra("posterPath", movie.poster_path.toString())
            intent.putExtra("rate", movie.rate.toString())
            intent.putExtra("releaseDate", movie.release_date.toString())
            intent.putExtra("backPosterPath", movie.backPoster_path.toString())
            intent.putExtra("description", movie.description.toString())
            intent.putExtra("id", movie.id.toString())

            context.startActivity(intent)
        }

        if (isFromFavList){
            deleteFromListBtn.setOnClickListener{
                val fireStore = Firebase.firestore
                fireStore.collection("users")
                    .get()
                    .addOnSuccessListener {documentReference ->
                        for (document in documentReference) {
                            if (document.id == accountEmail){
                                val deleteMovieId = movie.id.toString() + " @, "
                                var movies = document["favouriteMovie"].toString()
                                movies = movies.replace(deleteMovieId, "")
                                //update the DataBase with new favourite movies list
                                fireStore.collection("users").document(accountEmail)
                                    .update("favouriteMovie", movies)
                                val intent = Intent(context, FavouriteMovieActivity::class.java)
                                intent.putExtra("movies", movies)
                                context.startActivity(intent)
                                (context as FavouriteMovieActivity).finish()
                            }
                        }
                    }
                    .addOnFailureListener{
                        it.printStackTrace()
                        Log.w("Failure", "Error adding document")
                    }
            }
        }

        return view
    }
}