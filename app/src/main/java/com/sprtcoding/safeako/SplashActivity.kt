package com.sprtcoding.safeako

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sprtcoding.safeako.admin.AdminHomeDashboard
import com.sprtcoding.safeako.api.access.AccessManager
import com.sprtcoding.safeako.api.access.ResponseAccessCallback
import com.sprtcoding.safeako.firebase.firebaseUtils.ICheckLoginStatus
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils.setToken
import com.sprtcoding.safeako.firebase.messaging.SaveToken.Token.saveToken
import com.sprtcoding.safeako.firebase.messaging.contract.IToken
import com.sprtcoding.safeako.user.activity.UserHomeDashboard
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.welcome_page.WelcomeActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(), ICheckLoginStatus, ResponseAccessCallback, IToken {
    private lateinit var accessManager: AccessManager

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
    }

    private fun init() {
        accessManager = AccessManager()
        accessManager.getAccess(this)
    }

    override fun onCheckLoginStatusSuccess(isLogin: Boolean, userId: String?, role: String?) {
        if(isLogin) {
            saveToken(userId!!, this, this, layoutInflater)
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

    override fun onSuccess(response: String) {
        if(response == "true") {
            Handler(Looper.getMainLooper()).postDelayed({
                Utils.checkLoginStatus(this, this)
            }, 3000)
        } else {
            Toast.makeText(this, "Access Denied, BAYAD KA MUNA!!!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFailure(errorMessage: String) {
        Log.d("ACCESS_DENIED", errorMessage)
    }

    override fun onTokenGenerated(token: String, uid: String) {
        setToken(uid,token) { success ->
            if(success) {
                Log.d("Token", "Save token success")
            } else {
                Log.d("Token", "Save token failed")
            }
        }
    }
}