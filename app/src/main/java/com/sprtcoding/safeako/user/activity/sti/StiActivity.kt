package com.sprtcoding.safeako.user.activity.sti

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
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

class StiActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var rvVideo: RecyclerView
    private lateinit var stiAdapter: STIAdapter
    private lateinit var assessmentViewModel: AssessmentViewModel

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
    }

    private fun init() {
        assessmentViewModel = ViewModelProvider(this)[AssessmentViewModel::class.java]

        assessmentViewModel.refreshToken(this)

        assessmentViewModel.token.observe(this) { result ->
            result.onSuccess { token ->
                AuthTokenManager.accessToken = token
            }
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
}