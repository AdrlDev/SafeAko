package com.sprtcoding.safeako.user.activity.sti

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.user.activity.VideoPlayerActivity
import com.sprtcoding.safeako.user.activity.sti.adapter.STIAdapter

class StiActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var rvVideo: RecyclerView
    private lateinit var stiAdapter: STIAdapter
    private val videoList = listOf(
        R.raw.ph1_stds,
        R.raw.ph2_stds,
        R.raw.ph3_stds,
        R.raw.ph4_stds,
        R.raw.ph5_stds,
        R.raw.ph6_stds,
        R.raw.ph7_stds,
        R.raw.ph8_stds
    )

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
        stiAdapter = STIAdapter(videoList, this, { videoResId ->
            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra("VIDEO_RES_ID", videoResId)
            startActivity(intent)
        }, lifecycleScope)

        rvVideo.layoutManager = LinearLayoutManager(this)
        rvVideo.adapter = stiAdapter

        btnBack.setOnClickListener { finish() }
    }
}