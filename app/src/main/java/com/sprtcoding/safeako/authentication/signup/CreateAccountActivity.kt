package com.sprtcoding.safeako.authentication.signup

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.sprtcoding.safeako.BuildConfig
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.api.OTPManager
import com.sprtcoding.safeako.authentication.login.LoginActivity
import com.sprtcoding.safeako.firebaseUtils.Utils
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.activity.user_avatar.AddAvatarActivity
import com.sprtcoding.safeako.utils.Constants
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.welcome_page.WelcomeLoginActivity

class CreateAccountActivity : AppCompatActivity(), ICreateAccountCallBack {
    private lateinit var btnBack: ImageView
    private lateinit var btnVerify: TextView
    private lateinit var otpSec: TextView
    private lateinit var btnRegister: MaterialButton
    private lateinit var etPhone: TextInputEditText
    private lateinit var etOtp: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etPassLayout: TextInputLayout
    private lateinit var etOtpLayout: LinearLayout
    private lateinit var cbTerms: MaterialCheckBox
    private var isPhoneValid = false
    private lateinit var auth: FirebaseAuth
    private lateinit var loading: ProgressDialog
    private lateinit var viewModel: CreateAccountViewModel
    private lateinit var otpManager: OTPManager
    private lateinit var API_KEY: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_account)
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
        btnVerify = findViewById(R.id.btn_verify)
        btnRegister = findViewById(R.id.btn_register)
        etPhone = findViewById(R.id.et_phone)
        etOtp = findViewById(R.id.et_otp)
        etPassword = findViewById(R.id.et_password)
        etPassLayout = findViewById(R.id.tl_password)
        etOtpLayout = findViewById(R.id.otp_ll)
        cbTerms = findViewById(R.id.term_cb)
        otpSec = findViewById(R.id.otp_sec)
    }

    private fun init() {
        auth = Firebase.auth
        loading = ProgressDialog(this)
        loading.setMessage("Loading...")
        loading.setCancelable(false)

        val factory = CreateAccountViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[CreateAccountViewModel::class.java]
        otpManager = OTPManager()

        API_KEY = BuildConfig.API_KEY
    }

    private fun afterInit() {
        btnBack.setOnClickListener {
            backToLoginWelcome()
        }

        etPhone.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val isValid = s.toString().matches(Regex("^\\+639\\d{9}$"))

                etPassword.isEnabled = !isValid
                btnVerify.isVisible = isValid
                btnRegister.isEnabled = !isValid
                isPhoneValid = isValid
            }

        })

        btnVerify.setOnClickListener {
            loading.show()
            if(isPhoneValid) {


                val phoneNumber = etPhone.text.toString()
                val message = "Your One Time Password is: {otp}. Please use it within 5 minutes."
                val senderName = "SafeAko"

                otpManager.sendOtp(API_KEY, phoneNumber, message, senderName, null,this, otpSec)

            }
        }
    }

    private fun backToLoginWelcome() {
        startActivity(Intent(this, WelcomeLoginActivity::class.java))
        finish()
    }

    override fun onOtpSent(success: String) {
        loading.dismiss()
        btnRegister.setText(R.string.submit)
        btnRegister.isEnabled = true
        etPassLayout.isVisible = false
        etOtpLayout.isVisible = true
        btnVerify.isEnabled = false
        btnVerify.isVisible = false

        Toast.makeText(this, ""+success, Toast.LENGTH_SHORT).show()

        Constants.VerificationStatus.isVerifying = true

        btnRegister.setOnClickListener {
            loading.show()
            //verify code here
            val code = Constants.OTPCredentials.code
            val myCode = etOtp.text.toString()

            if(myCode.isNotEmpty()) {
                Handler(Looper.getMainLooper()).postDelayed({
                    Utility.verifyOTP(code, myCode, this)
                }, 3000)
            }
        }
    }

    override fun onOtpSentFailed(error: String) {
        loading.dismiss()
        btnRegister.setText(R.string.register)
        Toast.makeText(this, ""+Constants.OTPCredentials.message, Toast.LENGTH_SHORT).show()
    }

    override fun onDataSaveSuccess(success: String, userId: String) {
        loading.dismiss()

        Utils.checkCredentials(userId) { success, hasAvatar ->
            if(success) {
                if(!hasAvatar) {
                    val intent = Intent(this, AddAvatarActivity::class.java)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, ""+success, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }

    override fun onDataSaveFailed(error: String) {
        loading.dismiss()
        Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show()
    }

    override fun onOtpSuccess(success: String) {
        loading.dismiss()
        btnRegister.setText(R.string.register)
        btnVerify.isVisible = false
        etPassword.isEnabled = true
        etPassLayout.isVisible = true
        etOtpLayout.isVisible = false

        Utility.cancelCountdownTimer()

        Constants.VerificationStatus.isVerifying = false
        Constants.VerificationStatus.isVerified = true
        Toast.makeText(this, ""+success, Toast.LENGTH_SHORT).show()

        btnRegister.setOnClickListener {
            val phone = etPhone.text.toString()
            val password = etPassword.text.toString()
            val encryptedPassword = Utility.encryptPassword(password)
            //val randomName = Utility.generateRandomName()

            val user = Users(
                userId = Utility.generateUserId(),
                isPhoneVerified = Constants.VerificationStatus.isVerified,
                phone = phone,
                encryptedPass = encryptedPassword,
                role = "User"
            )

            if(password.isNotEmpty()) {
                if(!cbTerms.isChecked) {
                    loading.dismiss()
                    Toast.makeText(this, "Please accept terms and conditions.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    loading.show()
                    btnRegister.isEnabled = false
                    viewModel.addUserToFirestore(user, this)
                }
            } else {
                loading.dismiss()
                Toast.makeText(this, "Password is required.", Toast.LENGTH_SHORT).show()
                etPassword.requestFocus()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onOtpFailed(error: String) {
        loading.dismiss()
        Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    override fun onOtpSentError(error: String) {
        loading.dismiss()
        Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    override fun onOtpExpired() {
        btnVerify.isEnabled = true
        btnVerify.isVisible = true
        btnVerify.text = "Resend OTP"

        btnRegister.setOnClickListener {
            loading.show()

            Toast.makeText(this, "OTP Expired", Toast.LENGTH_SHORT).show()
            loading.dismiss()
        }

        btnVerify.setOnClickListener {
            loading.show()
            val phoneNumber = etPhone.text.toString()
            val message = "Your One Time Password is: {otp}. Please use it within 5 minutes."
            val senderName = "CommuTech"

            otpManager.sendOtp(API_KEY, phoneNumber, message, senderName, null,this, otpSec)
        }
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
        backToLoginWelcome()
    }
}