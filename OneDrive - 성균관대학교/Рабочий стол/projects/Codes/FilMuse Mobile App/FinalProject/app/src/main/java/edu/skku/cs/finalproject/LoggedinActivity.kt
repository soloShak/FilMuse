package edu.skku.cs.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoggedinActivity : AppCompatActivity() {

    val fireStore = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loggedin)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.AccountButton

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.SearchButton -> {
                    val intent = Intent(this, SearchActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out)
                    true
                }
                R.id.HomeButton -> {
                    val intent = Intent(this, MainPageActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right)
                    true
                }
                else -> false
            }
        }
        val email = intent.getStringExtra("email")
        val name = intent.getStringExtra("name")
        var imgUrl = intent.getStringExtra("imgUrl")
        val auth : FirebaseAuth = FirebaseAuth.getInstance()

        val addFavBtn = findViewById<Button>(R.id.AddFavListButton)
        val signOutBtn= findViewById<Button>(R.id.GoogleSignupButton)
        val welcomeView = findViewById<TextView>(R.id.WelcomeTextView)
        val emailView = findViewById<TextView>(R.id.EmailTextView)
        val nameView = findViewById<TextView>(R.id.NameTextView)
        val imageVIew = findViewById<ImageView>(R.id.UserImg)

        welcomeView.text = "Welcome,\n" + name.toString().capitalize() + "!"
        emailView.text = "Email:\n" + email
        nameView.text = "Name: " + name

        if (imgUrl.toString().length < 1) {
            imgUrl = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png"
        }
        Glide.with(this)
            .load(imgUrl)
            .into(imageVIew)

        // the following function checks
        // if the email address is in the database to prevent duplicate account
        // and creates the new account if there is none
        CoroutineScope(Dispatchers.Main).launch {
            readCheckCreateDb(email.toString(), name.toString(), imgUrl.toString())
        }

        signOutBtn.setOnClickListener{
            ifLoggedIn = false
            auth.signOut()
            startActivity(Intent(this , LoginActivity::class.java))
            finish()
        }

        addFavBtn.setOnClickListener{
            fireStore.collection("users")
                .get()
                .addOnSuccessListener {documentReference ->
                    for (document in documentReference) {
                        if (document.id == accountEmail){
                            val movies = document["favouriteMovie"].toString()
                            val intent = Intent(this, FavouriteMovieActivity::class.java)
                            intent.putExtra("movies", movies)
                            startActivity(intent)
                        }
                    }
                }
                .addOnFailureListener{
                    it.printStackTrace()
                    Log.w("Failure", "Error adding document")
                }
        }
    }

    fun readCheckCreateDb(email: String, name: String, imgUrl:String){
        var isNewAccount = true
        // reading the database to know if the account under email address exists
        fireStore.collection("users")
            .get()
            .addOnSuccessListener {documentReference ->
                for (document in documentReference) {
                    if (email == document.id){
                        isNewAccount = false
                        break
                    }
                }
                // if the email is new to DataBase it is added with initial values
                if (isNewAccount) {
                    val documentId = email
                    val user = hashMapOf(
                        "email" to email,
                        "name" to name,
                        "imgUrl" to imgUrl,
                        "favouriteMovie" to ""
                    )

                    val documentRef = fireStore.collection("users").document(documentId)
                    documentRef.set(user)
                        .addOnSuccessListener {
                            // Document successfully written
                        }
                        .addOnFailureListener { e ->
                            // Handle error
                        }
                }
            }
            .addOnFailureListener{
                it.printStackTrace()
                Log.w("Failure", "Error adding document")
            }
    }
}