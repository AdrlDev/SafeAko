package com.sprtcoding.safeako.authentication.login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.admin.AdminHomeDashboard
import com.sprtcoding.safeako.user.activity.UserHomeDashboard
import com.sprtcoding.safeako.utils.Utility.encryptPassword
import com.sprtcoding.safeako.welcome_page.WelcomeLoginActivity

class LoginActivity : AppCompatActivity(), ILoginCallBack {
    private lateinit var btnBack : ImageView
    private lateinit var btnLogin: MaterialButton
    private lateinit var rememberMe: CheckBox
    private lateinit var etPhoneNumber: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loading: ProgressDialog
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        init()
        afterInit()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back)
        btnLogin = findViewById(R.id.btn_login)
        etPhoneNumber = findViewById(R.id.et_phone)
        etPassword = findViewById(R.id.et_password)
        rememberMe = findViewById(R.id.remember_cb)
    }

    private fun init() {
        db = Firebase.firestore
        loginViewModel = LoginViewModel(this)
        loading = ProgressDialog(this)
        loading.setMessage("Please wait...")

        if(getSharedPreferences("user_login_credentials", MODE_PRIVATE).contains("phoneNumber")) {
            val phoneNumber = getSharedPreferences("user_login_credentials", MODE_PRIVATE).getString("phoneNumber", "")
            val password = getSharedPreferences("user_login_credentials", MODE_PRIVATE).getString("password", "")
            etPhoneNumber.setText(phoneNumber)
            etPassword.setText(password)
            rememberMe.isChecked = true
        } else {
            rememberMe.isChecked = false
        }

        btnBack.setOnClickListener {
            backToLoginWelcome()
        }
    }

    private fun afterInit() {
        btnLogin.setOnClickListener {
            val phoneNumber = etPhoneNumber.text.toString().trim()
            val password = etPassword.text.toString().trim()

            loading.show()
            Log.d("LoginActivity", "Phone Number: $phoneNumber")
            Log.d("LoginActivity", "Password: $password")

            if(rememberMe.isChecked) {
                val sharedPreferences = getSharedPreferences("user_login_credentials", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("phoneNumber", phoneNumber)
                editor.putString("password", password)
                editor.apply()
            } else {
                // Clear saved credentials if "Remember Me" is unchecked
                val sharedPreferences = getSharedPreferences("user_login_credentials", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.remove("phoneNumber")
                editor.remove("password")
                editor.apply()
            }

            if (phoneNumber.isEmpty() || password.isEmpty()) {
                loading.dismiss()
                Toast.makeText(this, "Phone number and password cannot be empty", Toast.LENGTH_SHORT).show()
                etPhoneNumber.requestFocus()
            } else {
                loading.show()
                val encryptedPassword = encryptPassword(password)
                loginViewModel.login(phoneNumber, encryptedPassword, this)
            }
        }
    }

    private fun backToLoginWelcome() {
        startActivity(Intent(this, WelcomeLoginActivity::class.java))
        finish()
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
        backToLoginWelcome()
    }

    override fun onLoginSuccess(msg: String, userId: String, role: String) {
        loading.dismiss()
        Log.d("Login", "$msg $userId")
        Toast.makeText(this, "$msg $userId", Toast.LENGTH_SHORT).show()

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

    }

    override fun onLoginFailed(msg: String) {
        loading.dismiss()
        Log.d("LoginFailed", msg)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onLoginError(error: String) {
        loading.dismiss()
        Log.d("LoginError", error)
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }
}