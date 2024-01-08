package edu.skku.cs.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class FavouriteMovieActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite_movie)

        val movies = intent.getStringExtra("movies")
        val movieList = movies.toString().split("@, ")
            .map { it.trim().removeSuffix("@") }
            .filter { it.isNotEmpty() }

        val favListView = findViewById<ListView>(R.id.FavoriteMovieList)

        val client = OkHttpClient()
        val host = "https://api.themoviedb.org/3/movie/"
        val favMovieList = mutableListOf<Movie>()

        // getting the movie details of the favourite list
        // by sending an http request to a TMDB.
        for (movie in movieList) {
            val searchQuery = movie + "?api_key="
            var req = Request.Builder().url(host + searchQuery + apiKey).build()

            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")
                        val data = response.body!!.string()
                        val jsonObject = JSONObject(data)

                        CoroutineScope(Dispatchers.Main).launch {
                            val originalTitle: String =
                                jsonObject.getString("original_title")
                            val posterPath: String = jsonObject.getString("poster_path")
                            val avgScore: String = jsonObject.getString("vote_average")
                            val date: String = jsonObject.getString("release_date")
                            val backPoster: String = jsonObject.getString("backdrop_path")
                            val description: String = jsonObject.getString("overview")
                            val id: String = jsonObject.getString("id")

                            val movie = Movie(
                                originalTitle,
                                posterPath,
                                avgScore,
                                date,
                                backPoster,
                                description,
                                id
                            )
                            favMovieList.add(movie)
                            if (favMovieList.size > 0) {
                                val favMovieAdapter =
                                    SearchListAdapter(this@FavouriteMovieActivity, favMovieList, true)
                                favListView.adapter = favMovieAdapter
                            }
                        }
                    }
                }
            })
        }
    }
}