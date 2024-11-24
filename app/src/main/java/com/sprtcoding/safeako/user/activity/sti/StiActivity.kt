package com.sprtcoding.safeako.user.activity.sti

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.api.google_docs_api.AuthTokenManager
import com.sprtcoding.safeako.user.activity.VideoPlayerActivity
import com.sprtcoding.safeako.user.activity.sti.adapter.STIAdapter
import com.sprtcoding.safeako.user.fragment.viewmodel.AssessmentViewModel
import com.sprtcoding.safeako.utils.Constants.definitionVideoId
import com.sprtcoding.safeako.utils.Utility.copyPdfFromAssets
import java.io.IOException

class StiActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var rvVideo: RecyclerView
    private lateinit var pdfContainer: LinearLayout
    private lateinit var stiAdapter: STIAdapter
    private lateinit var assessmentViewModel: AssessmentViewModel
    private lateinit var gestureDetector: GestureDetector
    private val matrix = Matrix()
    private var scaleFactor = 1.0f
    private val zoomInFactor = 1.5f
    private val zoomOutFactor = 0.75f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sti)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left + 50, systemBars.top + 50, systemBars.right + 50, systemBars.bottom)
            insets
        }

        initViews()
        init()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back)
        rvVideo = findViewById(R.id.rv_vids)
        pdfContainer = findViewById(R.id.pdf_container)
        gestureDetector = GestureDetector(this, GestureListener())
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        assessmentViewModel = ViewModelProvider(this)[AssessmentViewModel::class.java]

        assessmentViewModel.refreshToken(this)

        assessmentViewModel.token.observe(this) { result ->
            result.onSuccess { token ->
                AuthTokenManager.accessToken = token
            }
        }

        // Copy PDF from assets to a temporary file
        val pdfFile = copyPdfFromAssets(this, "introduction_to_STI_prevention_and_control.pdf")

        try {
            val pdfRenderer = PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY))

            // Loop through all pages and display them
            for (i in 0 until pdfRenderer.pageCount) {
                val page = pdfRenderer.openPage(i)
                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                val imageView = ImageView(this)
                imageView.setImageBitmap(bitmap)
                // Set the scale type to FIT_XY
                imageView.scaleType = ImageView.ScaleType.FIT_XY
                // Apply the scale matrix to the ImageView
                imageView.imageMatrix = matrix

                // Set up touch listener for double-tap zoom
                imageView.setOnTouchListener { v, event ->
                    gestureDetector.onTouchEvent(event)
                    v.performClick()
                }

                pdfContainer.addView(imageView) // Add each page image to layout
                page.close() // Close the page
            }

            pdfRenderer.close() // Close PdfRenderer when done

        } catch (e: IOException) {
            e.printStackTrace()
        }

        stiAdapter = STIAdapter(definitionVideoId, this, { videoResId, fileNames ->
            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra("VIDEO_RES_ID", videoResId)
            intent.putExtra("FILENAME", fileNames)
            startActivity(intent)
        }, lifecycleScope, assessmentViewModel)

        rvVideo.layoutManager = LinearLayoutManager(this)
        rvVideo.adapter = stiAdapter

        btnBack.setOnClickListener { finish() }
    }

    // GestureListener to handle double-tap zoom functionality
    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        // Handle double-tap gestures
        override fun onDoubleTap(e: MotionEvent): Boolean {
            // Toggle zoom in or zoom out on double-tap
            scaleFactor = if (scaleFactor < 3.0f) {
                scaleFactor * zoomInFactor  // Zoom in
            } else {
                scaleFactor * zoomOutFactor  // Zoom out
            }

            // Apply scale factor to the matrix
            matrix.setScale(scaleFactor, scaleFactor)

            // Apply the matrix transformation to each ImageView
            for (i in 0 until pdfContainer.childCount) {
                val imageView = pdfContainer.getChildAt(i) as ImageView
                imageView.imageMatrix = matrix
            }

            return true
        }
    }

    // Override onTouchEvent to forward events to GestureDetector
    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }
}