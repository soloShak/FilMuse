package edu.skku.cs.finalproject

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide

class TopRatedListAdapter (val context : Context, val items:List<Movie>): BaseAdapter() {
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

        val movieLayout = view.findViewById<ConstraintLayout>(R.id.MovieLayout)
        val imageView = view.findViewById<ImageView>(R.id.MoviePoster)
        val titleView = view.findViewById<TextView>(R.id.MovieTitle)
        val rateView = view.findViewById<TextView>(R.id.MovieRate)

        val layoutParams: ViewGroup.LayoutParams = titleView.layoutParams
        layoutParams.width = 200
        titleView.layoutParams = layoutParams

        titleView.text = movie.title
        rateView.text = "★ " + movie.rate

        val imageUrl = "https://image.tmdb.org/t/p/original" + movie.poster_path

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

        return view
    }
}