package edu.skku.cs.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView
import android.widget.LinearLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
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

public val apiKey = "6e91a39015831c5e762559276cf43f53"

class MainPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        // API key is 6e91a39015831c5e762559276cf43f53

        // To get the popular movies JSON
        // https://api.themoviedb.org/3/movie/popular?api_key=6e91a39015831c5e762559276cf43f53

        // To get the movie using its id
        // https://api.themoviedb.org/3/movie/{movie_id}?api_key=6e91a39015831c5e762559276cf43f53

        // To get top rated JSON
        // https://api.themoviedb.org/3/movie/top_rated?api_key=6e91a39015831c5e762559276cf43f53

        // To search for a movie do this(in this case for john wick chapter 4)
        // https://api.themoviedb.org/3/search/movie?api_key=6e91a39015831c5e762559276cf43f53&query=john-wick-chapter-4
        // or just this https://api.themoviedb.org/3/search/movie?api_key=6e91a39015831c5e762559276cf43f53&query=John Wick: Chapter 4

        // To get a poster of the movie use
        // https://image.tmdb.org/t/p/original + {poster_path}

        // To get a background of the movie use
        // https://image.tmdb.org/t/p/original + {backdrop_path}

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.HomeButton

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.SearchButton -> {
                    val intent = Intent(this, SearchActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out)
                    true
                }
                R.id.AccountButton -> {
                    if (!ifLoggedIn){
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out)
                        true
                    }
                    else {
                        val intent = Intent(this , LoggedinActivity::class.java)
                        intent.putExtra("email" , accountEmail)
                        intent.putExtra("name" , accountName)
                        intent.putExtra("imgUrl", accountImg)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out)
                        true
                    }
                }
                else -> false
            }
        }

        //===========================================================================
        // Getting json files of the movies and calling adapters for each view
        //===========================================================================
        val client = OkHttpClient()
        val host = "https://api.themoviedb.org/3/movie/"
        val popular_path = "popular?api_key="
        var req = Request.Builder().url(host + popular_path + apiKey).build()

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
                        val resultsArray: JSONArray = jsonObject.getJSONArray("results")

                        val popularMovieList = mutableListOf<Movie>()

                        // Iterate over the array and retrieve the values
                        for (i in 0 until resultsArray.length()) {
                            val movieObject: JSONObject = resultsArray.getJSONObject(i)

                            val originalTitle: String = movieObject.getString("original_title")
                            val posterPath: String = movieObject.getString("poster_path")
                            val avgScore: String = movieObject.getString("vote_average")
                            val date: String = movieObject.getString("release_date")
                            val backPoster: String = movieObject.getString("backdrop_path")
                            val description: String = movieObject.getString("overview")
                            val id: String = movieObject.getString("id")

                            val movie = Movie(originalTitle, posterPath, avgScore, date, backPoster, description, id)
                            popularMovieList.add(movie)

                            val movieAdapter =
                                PopularListViewAdapter(this@MainPageActivity, popularMovieList)
                            var popularList = findViewById<LinearLayout>(R.id.PopularList)
                            val movieView = movieAdapter.getView(i, null, popularList)
                            popularList.addView(movieView)
                        }
                    }
                }
            }
        })

        val top_rated_path = "top_rated?api_key="
        req = Request.Builder().url(host + top_rated_path + apiKey).build()

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
                        val resultsArray: JSONArray = jsonObject.getJSONArray("results")
                        val topRatedMovieList = mutableListOf<Movie>()

                        for (i in 0 until resultsArray.length()) {
                            val movieObject: JSONObject = resultsArray.getJSONObject(i)

                            val originalTitle: String = movieObject.getString("original_title")
                            val posterPath: String = movieObject.getString("poster_path")
                            val avgScore: String = movieObject.getString("vote_average")
                            val date: String = movieObject.getString("release_date")
                            val backPoster: String = movieObject.getString("backdrop_path")
                            val description: String = movieObject.getString("overview")
                            val id: String = movieObject.getString("id")

                            val movie = Movie(originalTitle, posterPath, avgScore, date, backPoster, description, id)
                            topRatedMovieList.add(movie)
                        }
                        val rateMovieAdapter =
                            TopRatedListAdapter(this@MainPageActivity, topRatedMovieList)
                        val topRatedList = findViewById<GridView>(R.id.TopRatedView)
                        topRatedList.adapter = rateMovieAdapter

                    }
                }
            }
        })
    }
}
