package com.sprtcoding.safeako.user.activity

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
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
    private lateinit var fileName: TextView
    private lateinit var progressBar: ProgressBar

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
        progressBar = findViewById(R.id.progressBar)
        fileName = findViewById(R.id.file_name)
        val videoResId = intent.getStringExtra("VIDEO_RES_ID")
        val fileNames = intent.getStringExtra("FILENAME")

        fileName.text = fileNames

        // Initialize ExoPlayer instance
        exoPlayer = ExoPlayer.Builder(this).build()

        Log.d("VIDEO_ID", videoResId!!)
        val videoUri = Uri.parse("https://drive.google.com/uc?export=download&id=$videoResId")
        initializePlayer(videoUri, exoPlayer, playerView)
        
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                // Log detailed error information
                Log.e("VideoPlayerError", "Playback failed: ${error.errorCode}, Cause: ${error.cause}")
                Utility.showToast(this@VideoPlayerActivity, "Error: ${error.message}")
                when (error.errorCode) {
                    PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS -> {
                        Utility.showToast(this@VideoPlayerActivity, "Network issue or file inaccessible")
                    }
                    PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> {
                        Utility.showToast(this@VideoPlayerActivity, "Video file not found")
                    }
                    PlaybackException.ERROR_CODE_DECODING_FAILED -> {
                        Utility.showToast(this@VideoPlayerActivity, "Decoding failed. Unsupported video format.")
                    }
                    else -> {
                        Utility.showToast(this@VideoPlayerActivity, "Playback failed: ${error.message}")
                    }
                }
                super.onPlayerError(error)
            }
            override fun onIsLoadingChanged(isLoading: Boolean) {
                // Show or hide the ProgressBar based on loading state
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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