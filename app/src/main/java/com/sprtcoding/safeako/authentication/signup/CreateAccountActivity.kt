package com.sprtcoding.safeako.authentication.signup

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import com.sprtcoding.safeako.BuildConfig
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.api.sms.OTPManager
import com.sprtcoding.safeako.authentication.login.LoginActivity
import com.sprtcoding.safeako.authentication.signup.contract.VerifyOtpCallback
import com.sprtcoding.safeako.authentication.signup.viewmodel.CreateAccountViewModel
import com.sprtcoding.safeako.firebaseUtils.Utils
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.activity.user_avatar.AddAvatarActivity
import com.sprtcoding.safeako.utils.Constants
import com.sprtcoding.safeako.utils.Constants.MESSAGE
import com.sprtcoding.safeako.utils.Constants.SENDER_NAME
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.welcome_page.WelcomeLoginActivity

class CreateAccountActivity : AppCompatActivity(), VerifyOtpCallback {
    private lateinit var btnBack: ImageView
    private lateinit var btnVerify: TextView
    private lateinit var otpSec: TextView
    private lateinit var btnRegister: MaterialButton
    private lateinit var etPhone: EditText
    private lateinit var etOtp: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etPassLayout: TextInputLayout
    private lateinit var etOtpLayout: LinearLayout
    private lateinit var cbTerms: MaterialCheckBox
    private var isPhoneValid = false
    private lateinit var loading: ProgressDialog
    private lateinit var viewModel: CreateAccountViewModel
    private lateinit var otpManager: OTPManager
    private lateinit var ccp: CountryCodePicker

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
        etPhone = findViewById(R.id.et_phone_num)
        etOtp = findViewById(R.id.et_otp)
        etPassword = findViewById(R.id.et_password)
        etPassLayout = findViewById(R.id.tl_password)
        etOtpLayout = findViewById(R.id.otp_ll)
        cbTerms = findViewById(R.id.term_cb)
        otpSec = findViewById(R.id.otp_sec)
        ccp = findViewById(R.id.ccp)
    }

    private fun init() {
        loading = ProgressDialog(this)
        loading.setMessage("Loading...")
        loading.setCancelable(false)

        viewModel = ViewModelProvider(this)[CreateAccountViewModel::class.java]
        otpManager = OTPManager()
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
                val phoneNumber = s.toString()

                // Remove leading '0' if it exists
                if (phoneNumber.startsWith("0")) {
                    phoneNumber.substring(1)
                }

                val isValid = s.toString().matches(Regex("^\\d{10}$"))

                etPassword.isEnabled = !isValid
                btnVerify.isVisible = isValid
                btnRegister.isEnabled = !isValid
                isPhoneValid = isValid
            }

        })

        btnVerify.setOnClickListener {
            loading.show()
            if(isPhoneValid) {
                viewModel.sendOtp(otpManager, BuildConfig.API_KEY, getPhoneWithCC(), MESSAGE, SENDER_NAME, null, otpSec)
            }
        }

        observer()
    }

    private fun getPhoneWithCC(): String {
        val ccpValue = ccp.selectedCountryCodeWithPlus
        val phoneNumber = etPhone.text.toString()
        if(phoneNumber.isNotEmpty()) {
            return ccpValue + phoneNumber
        }
        return ""
    }

    private fun backToLoginWelcome() {
        startActivity(Intent(this, WelcomeLoginActivity::class.java))
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun observer() {
        viewModel.signUpResponse.observe(this) { result ->
            result.onSuccess { response ->
                loading.dismiss()

                Utils.checkCredentials(response.uid!!) { successMessage, hasAvatar ->
                    if(successMessage) {
                        if(!hasAvatar) {
                            val intent = Intent(this, AddAvatarActivity::class.java)
                            intent.putExtra("userId", response.uid!!)
                            startActivity(intent)
                            finish()
                        } else {
                            Utility.showAlertDialog(
                                this,
                                layoutInflater,
                                "Avatar",
                                response.message!!,
                                "Ok",
                            ) {
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                        }
                    }
                }
            }
            result.onFailure { err ->
                loading.dismiss()
                err.message?.let { error ->
                    Utility.showAlertDialog(
                        this,
                        layoutInflater,
                        "Sign Up Failed",
                        error,
                        "Ok",
                    ) {
                    }
                }
            }
        }

        viewModel.onOtpSent.observe(this) { result ->
            result.onSuccess { response ->
                if(response.isCodeSent) {
                    Utility.showAlertDialog(
                        this,
                        layoutInflater,
                        "OTP",
                        "OTP sent successfully.",
                        "Ok",
                    ) {
                        loading.dismiss()
                        btnRegister.setText(R.string.submit)
                        btnRegister.isEnabled = true
                        etPassLayout.isVisible = false
                        etOtpLayout.isVisible = true
                        btnVerify.isEnabled = false
                        btnVerify.isVisible = false
                        Constants.VerificationStatus.isVerifying = true
                    }

                    btnRegister.setOnClickListener {
                        loading.show()
                        //verify code here
                        val code = response.code
                        val myCode = etOtp.text.toString()

                        if(myCode.isNotEmpty()) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                Utility.verifyOTP(code, myCode, this)
                            }, 3000)
                        }
                    }
                }
            }
        }

        viewModel.onOtpSentFailed.observe(this) { result ->
            result.onSuccess { response ->
                if(!response.isCodeSent) {
                    Utility.showAlertDialog(
                        this,
                        layoutInflater,
                        "OTP",
                        response.message!!,
                        "Ok"
                    ) {
                        loading.dismiss()
                        btnRegister.setText(R.string.register)
                    }
                }
            }
        }

        viewModel.onOtpSentError.observe(this) { result->
            result.onSuccess { err ->
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "OTP",
                    err,
                    "Ok"
                ) {
                    loading.dismiss()
                }
            }
        }

        viewModel.onOtpExpired.observe(this) { result ->
            result.onSuccess { isExpired ->
                if(isExpired) {
                    btnVerify.isEnabled = true
                    btnVerify.isVisible = true
                    btnVerify.text = "Resend OTP"

                    btnRegister.setOnClickListener {
                        loading.show()

                        Utility.showAlertDialog(
                            this,
                            layoutInflater,
                            "OTP",
                            "OTP Expired",
                            "Ok"
                        ) {
                            loading.dismiss()
                        }
                    }

                    btnVerify.setOnClickListener {
                        loading.show()
                        viewModel.sendOtp(otpManager, BuildConfig.API_KEY, getPhoneWithCC(), MESSAGE, SENDER_NAME, null, otpSec)
                    }
                }
            }
        }
    }

    override fun onOtpVerified(message: String) {
        Utility.showAlertDialog(
            this,
            layoutInflater,
            "OTP",
            message,
            "Ok"
        ) {
            loading.dismiss()
            btnRegister.setText(R.string.register)
            btnVerify.isVisible = false
            etPassword.isEnabled = true
            etPassLayout.isVisible = true
            etOtpLayout.isVisible = false

            Utility.cancelCountdownTimer()

            Constants.VerificationStatus.isVerifying = false
            Constants.VerificationStatus.isVerified = true
        }

        btnRegister.setOnClickListener {
            val password = etPassword.text.toString()
            val encryptedPassword = Utility.encryptPassword(password)

            val user = Users(
                userId = Utility.generateUserId(),
                isPhoneVerified = Constants.VerificationStatus.isVerified,
                phone = getPhoneWithCC(),
                encryptedPass = encryptedPassword,
                role = "User"
            )

            if(password.isNotEmpty()) {
                if(!cbTerms.isChecked) {
                    Utility.showAlertDialogWithYesNo(
                        this,
                        layoutInflater,
                        "Please accept terms and conditions.",
                        "Cancel",
                        "Accept",
                        null
                    ) {
                        cbTerms.isChecked = true
                        loading.dismiss()
                    }
                    return@setOnClickListener
                } else {
                    loading.show()
                    btnRegister.isEnabled = false
                    viewModel.addUserToFireStore(user)
                }
            } else {
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Warning",
                    "Password is required.",
                    "Ok"
                ) {
                    loading.dismiss()
                    etPassword.requestFocus()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onOtpFailed(failed: String) {
        Utility.showAlertDialog(
            this,
            layoutInflater,
            "OTP",
            failed,
            "Ok"
        ) {
            loading.dismiss()
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