package com.sprtcoding.safeako.utils.profile_settings

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sprtcoding.safeako.R

class ProfileSettings : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var btnChangeAvatar: LinearLayout
    private lateinit var btnChangePassword: LinearLayout
    private lateinit var btnChangePhone: LinearLayout
    private var myID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left + 60, systemBars.top + 60, systemBars.right + 60, systemBars.bottom + 60)
            insets
        }

        btnBack = findViewById(R.id.btn_back)
        btnChangeAvatar = findViewById(R.id.btn_change_avatar)
        btnChangePassword = findViewById(R.id.btn_change_password)
        btnChangePhone = findViewById(R.id.btn_change_phone)

        myID = intent.getStringExtra("UID")

        btnBack.setOnClickListener { finish() }
        btnChangeAvatar.setOnClickListener {
            startActivity(Intent(this, ChangeAvatar::class.java)
                .putExtra("UID", myID))
        }

        btnChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePassword::class.java)
                .putExtra("UID", myID))
        }

        btnChangePhone.setOnClickListener {
            startActivity(Intent(this, ChangePhone::class.java)
                .putExtra("UID", myID))
        }

    }
}