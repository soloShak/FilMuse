package edu.skku.cs.finalproject

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

// this variable is used to let other activities know if the user was signed in or not
var ifLoggedIn= false

var accountEmail = ""
var accountName = ""
var accountImg = ""

class LoginActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    lateinit var googleSignInClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
        val signUpView = findViewById<TextView>(R.id.SignUpText)
        val signupBtn = findViewById<Button>(R.id.SignInButton)
        auth = FirebaseAuth.getInstance()

        signUpView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        signupBtn.setOnClickListener {
            val email = findViewById<EditText>(R.id.EmailEditText).text
            val password = findViewById<EditText>(R.id.PasswordEditText).text

            if ((email.toString() != "") && (password.toString() != "")) {
                // if normal login was successful
                auth.signInWithEmailAndPassword(email.toString(), password.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        ifLoggedIn = true
                        accountEmail = email.toString()
                        accountImg = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png"
                        val intent = Intent(this, LoggedinActivity::class.java)
                        intent.putExtra("email", accountEmail)
                        intent.putExtra("name", accountName)
                        intent.putExtra("imgUrl", accountImg)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        Log.d("email is ", email.toString())
                    }
                }
            } else {
                Toast.makeText(this, "Please fill every field", Toast.LENGTH_SHORT).show()

            }
        }

        val googleSignOpt = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this , googleSignOpt)

        findViewById<Button>(R.id.GoogleSignupButton).setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }
    }

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful){
                val account : GoogleSignInAccount? = task.result
                if (account != null){
                    updateUI(account)
                }
            }else{
                Toast.makeText(this, task.exception.toString() , Toast.LENGTH_SHORT).show()
            }
        }
    }

    // if the login using google was successful
    fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken , null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                ifLoggedIn = true
                accountEmail = account.email.toString()
                accountName = account.displayName.toString()
                accountImg = account.photoUrl.toString()
                val intent = Intent(this , LoggedinActivity::class.java)
                intent.putExtra("email" , accountEmail)
                intent.putExtra("name" , accountName)
                intent.putExtra("imgUrl", accountImg)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this, it.exception.toString() , Toast.LENGTH_SHORT).show()
            }
        }
    }
}