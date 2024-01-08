package edu.skku.cs.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

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

        auth = FirebaseAuth.getInstance()
        val registerBtn = findViewById<Button>(R.id.RegisterButton)


        registerBtn.setOnClickListener {
            val email = findViewById<EditText>(R.id.SignUpEmailEditText).text
            val password = findViewById<EditText>(R.id.SignUpPasswordEditText).text
            val repeatPass = findViewById<EditText>(R.id.SignUpRepeatPassEditText).text
            val name = findViewById<EditText>(R.id.SignUpNameEditText).text

            if ((email.toString() != "") && (password.toString() != "")
                && (repeatPass.toString() != "") && (name.toString() != "")) {
                if (password.toString() == repeatPass.toString()) {
                    auth.createUserWithEmailAndPassword(email.toString(), password.toString()).addOnCompleteListener {
                        if (it.isSuccessful) {
                            // name is initialized only here, other parameters are in LoginActivity
                            accountName = name.toString()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Passwords are not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill every field", Toast.LENGTH_SHORT).show()

            }
        }
    }
}