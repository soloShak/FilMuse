package edu.skku.cs.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
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

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchText = findViewById<EditText>(R.id.SearchTextView).text
        val searchBtn = findViewById<Button>(R.id.SearchEnterButton)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.SearchButton

        val editText = findViewById<EditText>(R.id.SearchTextView)

        // to search for movies use this
        // https://api.themoviedb.org/3/search/movie?api_key=6e91a39015831c5e762559276cf43f53&query={Movie Name}

        // Clear focus from the EditText
        editText.clearFocus()

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.HomeButton -> {
                    val intent = Intent(this, MainPageActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right)
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

        searchBtn.setOnClickListener{

            val client = OkHttpClient()
            val host = "https://api.themoviedb.org/3/search/movie?api_key="
            val searchQuery = "&query=" + searchText
            var req = Request.Builder().url(host + apiKey + searchQuery).build()

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
                            val searchMovieList = mutableListOf<Movie>()

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
                                searchMovieList.add(movie)
                            }

                            if (searchMovieList.size > 1){
                                val rateMovieAdapter =
                                    SearchListAdapter(this@SearchActivity, searchMovieList, false)
                                val searchListView = findViewById<ListView>(R.id.SearchListView)
                                searchListView.adapter = rateMovieAdapter
                            }
                            else
                                Toast.makeText(
                                    this@SearchActivity,
                                    "There is no such movie",
                                    Toast.LENGTH_LONG).
                                show()
                        }
                    }
                }
            })


        }
    }
}