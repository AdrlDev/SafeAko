package com.sprtcoding.safeako

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sprtcoding.safeako.admin.AdminHomeDashboard
import com.sprtcoding.safeako.firebaseUtils.ICheckLoginStatus
import com.sprtcoding.safeako.firebaseUtils.Utils
import com.sprtcoding.safeako.user.activity.UserHomeDashboard
import com.sprtcoding.safeako.welcome_page.WelcomeActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(), ICheckLoginStatus {
    private lateinit var tvTitle : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()
        initView()
        afterInit()
    }

    private fun init() {
        Handler(Looper.getMainLooper()).postDelayed({
            Utils.checkLoginStatus(this, this)
        }, 3000)
    }

    private fun initView() {
        tvTitle = findViewById(R.id.title)
    }

    private fun afterInit() {
        // Set gradient colors
        val gradient = LinearGradient(
            0f, 0f, 0f, tvTitle.textSize,
            intArrayOf(
                getColor(R.color.g1),  // Replace with your start color
                getColor(R.color.g1),    // Replace with your end color
                getColor(R.color.g3)
            ),
            null,
            Shader.TileMode.CLAMP
        )

        // Apply the shader to the TextView
        tvTitle.paint.shader = gradient
    }

    override fun onCheckLoginStatusSuccess(isLogin: Boolean, userId: String?, role: String?) {
        if(isLogin) {
            if(role == "User") {
                val intent = Intent(this, UserHomeDashboard::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, AdminHomeDashboard::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
                finish()
            }
        } else {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }

    override fun onCheckLoginStatusFailed(message: String) {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }

    override fun onAlreadyLogout() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }
}