package com.sprtcoding.safeako.user.activity.acknowledgement

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.model.AcknowledgementModel
import com.sprtcoding.safeako.user.activity.acknowledgement.adapter.AcknowledgementAdapter

class Acknowledgement : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var rvAcknowledgement: RecyclerView
    private lateinit var acknowledgementAdapter: AcknowledgementAdapter
    private var acknowledgementList: List<AcknowledgementModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_acknowledgement)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left + 60, systemBars.top + 60, systemBars.right + 60, systemBars.bottom + 60)
            insets
        }

        btnBack = findViewById(R.id.btn_back)
        btnBack.setOnClickListener { finish() }

        initViews()
        afterInit()
    }

    private fun afterInit() {
        val ll = LinearLayoutManager(this)
        rvAcknowledgement.layoutManager = ll

        val u1 = AcknowledgementModel(
            "Genesis A. Ysibido, RN, RM, MANc",
            "Instructor l - Midwifery Department",
            "Content Consultant and System Validator",
            R.drawable.u1_img)

        val u2 = AcknowledgementModel(
            "Francis Albert F. Galindo, RN",
            "Head Nurse RHU Magsaysay",
            "Content Consultant and System Validator",
            R.drawable.u2_img)

        val u3 = AcknowledgementModel(
            "Sonia N. Sy,",
            "Medical Technologies  ll - San Jose District Hospital",
            "Content Consultant and System Validator",
            R.drawable.avatar_man)

        val u4 = AcknowledgementModel(
            "Marites D. Escultor, MSIT",
            "Thesis Adviser",
            null,
            R.drawable.avatar_man)

        val u5 = AcknowledgementModel(
            "Marichris M. Usita, EdD",
            "Data Analyst",
            null,
            R.drawable.avatar_man)

        val u6 = AcknowledgementModel(
            "Pilita A. Amahan, PhD",
            "Critic Reader",
            null,
            R.drawable.avatar_man)

        acknowledgementList = listOf(
            u1,
            u2,
            u3,
            u4,
            u5,
            u6
        )

        acknowledgementAdapter = AcknowledgementAdapter(this, acknowledgementList)
        rvAcknowledgement.adapter = acknowledgementAdapter
    }

    private fun initViews() {
        rvAcknowledgement = findViewById(R.id.rv_acknowledgement)
    }
}