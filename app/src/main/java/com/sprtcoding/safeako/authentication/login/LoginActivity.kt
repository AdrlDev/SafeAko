package com.sprtcoding.safeako.authentication.login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.admin.AdminHomeDashboard
import com.sprtcoding.safeako.authentication.login.viewmodel.LoginViewModel
import com.sprtcoding.safeako.authentication.login.viewmodel.LoginViewModelFactory
import com.sprtcoding.safeako.user.activity.UserHomeDashboard
import com.sprtcoding.safeako.utils.Constants.LOG_IN_CREDENTIALS
import com.sprtcoding.safeako.utils.Constants.LOG_IN_TAG
import com.sprtcoding.safeako.utils.Constants.PASSWORD
import com.sprtcoding.safeako.utils.Constants.PHONE_NUMBER
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.Utility.encryptPassword
import com.sprtcoding.safeako.welcome_page.WelcomeLoginActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var btnBack : ImageView
    private lateinit var btnLogin: MaterialButton
    private lateinit var rememberMe: CheckBox
    private lateinit var etPhoneNumber: EditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loading: ProgressDialog
    private lateinit var db: FirebaseFirestore
    private lateinit var ccp: CountryCodePicker

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
        etPhoneNumber = findViewById(R.id.et_phone_num)
        etPassword = findViewById(R.id.et_password)
        rememberMe = findViewById(R.id.remember_cb)
        ccp = findViewById(R.id.ccp)
    }

    private fun init() {
        db = Firebase.firestore
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(this))[LoginViewModel::class.java]
        loading = ProgressDialog(this)
        loading.setMessage("Please wait...")

        if(getSharedPreferences(LOG_IN_CREDENTIALS, MODE_PRIVATE).contains(PHONE_NUMBER)) {
            val phoneNumber = getSharedPreferences(LOG_IN_CREDENTIALS, MODE_PRIVATE).getString(PHONE_NUMBER, "")
            val password = getSharedPreferences(LOG_IN_CREDENTIALS, MODE_PRIVATE).getString(PASSWORD, "")
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

            val ccpValue = ccp.selectedCountryCodeWithPlus
            val numberWithCountryCode = ccpValue + phoneNumber;

            loading.show()
            Log.d(LOG_IN_TAG, "Phone Number: $numberWithCountryCode")
            Log.d(LOG_IN_TAG, "Password: $password")

            if(rememberMe.isChecked) {
                val sharedPreferences = getSharedPreferences(LOG_IN_CREDENTIALS, MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(PHONE_NUMBER, numberWithCountryCode)
                editor.putString(PASSWORD, password)
                editor.apply()
            } else {
                // Clear saved credentials if "Remember Me" is unchecked
                val sharedPreferences = getSharedPreferences(LOG_IN_CREDENTIALS, MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.remove(PHONE_NUMBER)
                editor.remove(PASSWORD)
                editor.apply()
            }

            if (phoneNumber.isEmpty() || password.isEmpty()) {
                loading.dismiss()
                Toast.makeText(this, "Phone number and password cannot be empty", Toast.LENGTH_SHORT).show()
                etPhoneNumber.requestFocus()
            } else {
                loading.show()
                val encryptedPassword = encryptPassword(password)
                loginViewModel.login(numberWithCountryCode, encryptedPassword)
            }
        }

        loginViewModel.response.observe(this) { result ->
            result.onSuccess { response ->
                if(response.uid != null && response.role != null) {
                    loading.dismiss()
                    checkRole(response.role!!, response.uid!!)
                } else {
                    loading.dismiss()
                    Utility.showAlertDialogWithYesNo(
                        this,
                        layoutInflater,
                        response.message!!,
                        "Cancel",
                        "Ok",
                        null
                    ) {}
                }
            }
        }
    }

    private fun checkRole(role: String, uid: String) {
        if(role == "User") {
            val intent = Intent(this, UserHomeDashboard::class.java)
            intent.putExtra("userId", uid)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, AdminHomeDashboard::class.java)
            intent.putExtra("userId", uid)
            startActivity(intent)
            finish()
        }
    }

    private fun backToLoginWelcome() {
        startActivity(Intent(this, WelcomeLoginActivity::class.java))
        finish()
    }
}