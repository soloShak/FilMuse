package edu.skku.cs.finalproject

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hide bottom navigation and upper status bars
        //hideNavBar()
        hideStatusBar()

        val videoView = findViewById<VideoView>(R.id.videoView)

        val videoPath = "android.resource://"  + packageName + "/" + R.raw.video

        val uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)
        videoView.start()

        // Set loop playback
        videoView.setOnCompletionListener { mediaPlayer ->
            mediaPlayer.start()
            mediaPlayer.isLooping = true
        }

        videoView.setOnClickListener{
            //hideNavBar()
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    fun hideNavBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            controller?.hide(WindowInsets.Type.navigationBars())
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
        }
    }

    fun hideStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            controller?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE
        }
    }
}