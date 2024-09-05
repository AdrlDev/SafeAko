package com.sprtcoding.safeako.welcome_page

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.authentication.login.LoginActivity
import com.sprtcoding.safeako.authentication.signup.CreateAccountActivity

class WelcomeLoginActivity : AppCompatActivity() {
    private lateinit var loginBtn : MaterialButton
    private lateinit var createAccountBtn : MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        init()
    }

    private fun initViews() {
        loginBtn = findViewById(R.id.btn_login)
        createAccountBtn = findViewById(R.id.btn_create)
    }

    private fun init() {
        loginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
            finish()
        }

        createAccountBtn.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
            finish()
        }
    }
}