package com.sprtcoding.safeako.user.activity

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.Utility.initializePlayer

class VideoPlayerActivity : AppCompatActivity() {
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var backBtn: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_video_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        backBtn = findViewById(R.id.btn_back)
        playerView = findViewById(R.id.videoView)
        val videoResId = intent.getIntExtra("VIDEO_RES_ID", -1)

        // Initialize ExoPlayer instance
        exoPlayer = ExoPlayer.Builder(this).build()

        if (videoResId != -1) {
            val videoUri = Uri.parse("android.resource://${packageName}/$videoResId")
            initializePlayer(videoUri, exoPlayer, playerView)
        }
        
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Utility.showToast(this@VideoPlayerActivity, "Error: ${error.message}")
                super.onPlayerError(error)
            }
        })

        backBtn.setOnClickListener { finish() }
    }

    override fun onStart() {
        super.onStart()
        exoPlayer.playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
        exoPlayer.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release() // Release the player when done
    }
}