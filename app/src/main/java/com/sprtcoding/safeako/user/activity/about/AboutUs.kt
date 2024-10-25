package com.sprtcoding.safeako.user.activity.about

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.model.Developers
import com.sprtcoding.safeako.user.activity.about.adapter.AboutAdapter

class AboutUs : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var rvDevelopers: RecyclerView
    private lateinit var aboutAdapter: AboutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_about_us)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnBack = findViewById(R.id.btn_back)
        btnBack.setOnClickListener { finish() }

        rvDevelopers = findViewById(R.id.rv_developers)

        val benjie = Developers(
            "Benjie B. Santos",
            "sbenjie962@gmail.com",
            R.drawable.benjie
        )

        val charity = Developers(
            "Charity D. Bayaoa",
            "charity.bayaoa16@gmail.com",
            R.drawable.charity
        )

        val renante = Developers(
            "Renante H. Oliveros",
            "renanteoliveros262003@gmail.com",
            R.drawable.renante
        )

        val christian = Developers(
            "Christian P. Bartolome",
            "christianbartolome911@gmail.com",
            R.drawable.christian
        )

        val nexon = Developers(
            "Nexon P. Minorca",
            "minorcanexon33@gmail.com",
            R.drawable.nexon
        )

        val devs = listOf(
            benjie,
            charity,
            renante,
            christian,
            nexon
        )

        val ll = LinearLayoutManager(this)
        rvDevelopers.layoutManager = ll
        aboutAdapter = AboutAdapter(this, devs)
        rvDevelopers.adapter = aboutAdapter

    }
}