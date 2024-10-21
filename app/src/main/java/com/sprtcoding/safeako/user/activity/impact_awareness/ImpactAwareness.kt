package com.sprtcoding.safeako.user.activity.impact_awareness

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.facebook.shimmer.ShimmerFrameLayout
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.api.google_docs_api.AuthTokenManager
import com.sprtcoding.safeako.api.google_docs_api.MetadataCallback
import com.sprtcoding.safeako.user.activity.VideoPlayerActivity
import com.sprtcoding.safeako.user.fragment.viewmodel.AssessmentViewModel
import com.sprtcoding.safeako.utils.Constants
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class ImpactAwareness : AppCompatActivity() {
    private lateinit var backBtn: ImageButton
    private lateinit var thumbnail1: ImageView
    private lateinit var thumbnail2: ImageView
    private lateinit var videoTitle1: TextView
    private lateinit var videoTitle2: TextView
    private lateinit var video1Card: CardView
    private lateinit var video2Card: CardView
    private lateinit var shimmerV1: ShimmerFrameLayout
    private lateinit var shimmerV2: ShimmerFrameLayout
    private lateinit var assessmentViewModel: AssessmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_impact_awareness)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left + 50,
                systemBars.top + 50,
                systemBars.right + 50,
                systemBars.bottom + 50
            )
            insets
        }

        initViews()
        init()
        afterInit()
    }

    private fun initViews() {
        backBtn = findViewById(R.id.back_button)
        thumbnail1 = findViewById(R.id.thumbnail_1)
        thumbnail2 = findViewById(R.id.thumbnail_2)
        videoTitle1 = findViewById(R.id.video_title_1)
        videoTitle2 = findViewById(R.id.video_title_2)
        video1Card = findViewById(R.id.video_1_card)
        video2Card = findViewById(R.id.video_2_card)
        shimmerV1 = findViewById(R.id.shimmer_video_1)
        shimmerV2 = findViewById(R.id.shimmer_video_2)
    }

    private fun init() {
        assessmentViewModel = ViewModelProvider(this)[AssessmentViewModel::class.java]

        assessmentViewModel.refreshToken(this)

        assessmentViewModel.token.observe(this) { result ->
            result.onSuccess { token ->
                AuthTokenManager.accessToken = token
            }
        }
    }

    private fun afterInit() {
        backBtn.setOnClickListener { finish() }

        lifecycleScope.launch {
            for (i in 6..7) {
                val thumbnailLink = Constants.allVideoId[i - 1]
                val videoTitle = Constants.videoTitle[i - 1]

                assessmentViewModel.getDriveFileMetadata(
                    this@ImpactAwareness,
                    thumbnailLink,
                    object :
                        MetadataCallback {
                        override fun onSuccess(thumbnailLink: String?, fileName: String?) {
                            // Check if thumbnailLink is not null before proceeding
                            if (!thumbnailLink.isNullOrEmpty()) {
                                // Switch to the main thread for UI operations
                                lifecycleScope.launch {
                                    shimmerV1.startShimmer()
                                    shimmerV2.startShimmer()
                                    thumbnail1.visibility = View.INVISIBLE
                                    thumbnail2.visibility = View.INVISIBLE
                                    thumbnail1.scaleType = ImageView.ScaleType.CENTER_INSIDE
                                    thumbnail2.scaleType = ImageView.ScaleType.CENTER_INSIDE

                                    if (i == 6) {
                                        videoTitle1.text = videoTitle
                                        Picasso.get()
                                            .load(thumbnailLink)
                                            .placeholder(R.drawable.reel)
                                            .into(thumbnail1, object : Callback {
                                                override fun onSuccess() {
                                                    // Stop shimmer effect when the thumbnail is loaded
                                                    shimmerV1.stopShimmer()
                                                    shimmerV1.setShimmer(null)
                                                    thumbnail1.visibility = View.VISIBLE
                                                    thumbnail1.scaleType =
                                                        ImageView.ScaleType.FIT_XY
                                                }

                                                override fun onError(e: Exception?) {
                                                    // Handle the error case, stop shimmer and show default image
                                                    shimmerV1.stopShimmer()
                                                    shimmerV1.setShimmer(null)
                                                    thumbnail1.visibility = View.VISIBLE
                                                    thumbnail1.setImageResource(R.drawable.reel)
                                                    thumbnail1.scaleType =
                                                        ImageView.ScaleType.CENTER_INSIDE
                                                }
                                            })
                                    } else {
                                        videoTitle2.text = videoTitle
                                        Picasso.get()
                                            .load(thumbnailLink)
                                            .placeholder(R.drawable.reel)
                                            .into(thumbnail2, object : Callback {
                                                override fun onSuccess() {
                                                    // Stop shimmer effect when the thumbnail is loaded
                                                    shimmerV2.stopShimmer()
                                                    shimmerV2.setShimmer(null)
                                                    thumbnail2.visibility = View.VISIBLE
                                                    thumbnail2.scaleType =
                                                        ImageView.ScaleType.FIT_XY
                                                }

                                                override fun onError(e: Exception?) {
                                                    // Handle the error case, stop shimmer and show default image
                                                    shimmerV2.stopShimmer()
                                                    shimmerV1.setShimmer(null)
                                                    thumbnail2.visibility = View.VISIBLE
                                                    thumbnail2.setImageResource(R.drawable.reel)
                                                    thumbnail2.scaleType =
                                                        ImageView.ScaleType.CENTER_INSIDE
                                                }
                                            })
                                    }
                                }
                            } else {
                                // Handle the case where the thumbnail link is null or empty
                                println("Failed to retrieve a valid thumbnail link for video: ${Constants.allVideoId[0]}")
                                // Optionally set a default image or handle it accordingly
                                // Optionally set a default image or handle it accordingly
                                lifecycleScope.launch {
                                    if (i == 1) {
                                        Picasso.get()
                                            .load(thumbnailLink)
                                            .placeholder(R.drawable.reel)
                                            .fit()
                                            .into(thumbnail1, object : Callback {
                                                override fun onSuccess() {
                                                    // Stop shimmer effect when the thumbnail is loaded
                                                    shimmerV1.stopShimmer()
                                                    thumbnail1.scaleType =
                                                        ImageView.ScaleType.FIT_XY
                                                }

                                                override fun onError(e: Exception?) {
                                                    // Handle the error case, stop shimmer and show default image
                                                    shimmerV1.stopShimmer()
                                                    thumbnail1.setImageResource(R.drawable.reel)
                                                    thumbnail1.scaleType =
                                                        ImageView.ScaleType.CENTER_INSIDE
                                                }
                                            })
                                    } else {
                                        Picasso.get()
                                            .load(thumbnailLink)
                                            .placeholder(R.drawable.reel)
                                            .fit()
                                            .into(thumbnail2, object : Callback {
                                                override fun onSuccess() {
                                                    // Stop shimmer effect when the thumbnail is loaded
                                                    shimmerV2.stopShimmer()
                                                    thumbnail2.scaleType =
                                                        ImageView.ScaleType.FIT_XY
                                                }

                                                override fun onError(e: Exception?) {
                                                    // Handle the error case, stop shimmer and show default image
                                                    shimmerV2.stopShimmer()
                                                    thumbnail2.setImageResource(R.drawable.reel)
                                                    thumbnail2.scaleType =
                                                        ImageView.ScaleType.CENTER_INSIDE
                                                }
                                            })
                                    }
                                }
                            }
                        }

                        override fun onError(errorMessage: String) {
                            // Handle the case where the thumbnail link is null or empty
                            println(errorMessage)
                        }
                    })
            }
        }

        video1Card.setOnClickListener {
            val intent = Intent(this@ImpactAwareness, VideoPlayerActivity::class.java)
            intent.putExtra("VIDEO_RES_ID", Constants.allVideoId[5])
            intent.putExtra("FILENAME", Constants.videoTitle[5])
            startActivity(intent)
        }

        video2Card.setOnClickListener {
            val intent = Intent(this@ImpactAwareness, VideoPlayerActivity::class.java)
            intent.putExtra("VIDEO_RES_ID", Constants.allVideoId[6])
            intent.putExtra("FILENAME", Constants.videoTitle[6])
            startActivity(intent)
        }
    }

}