package com.sprtcoding.safeako.authentication.forgot

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import com.sprtcoding.safeako.api.sms.OTPManager
import com.sprtcoding.safeako.authentication.forgot.viewmodel.ForgotPasswordViewModel
import com.sprtcoding.safeako.authentication.login.LoginActivity
import com.sprtcoding.safeako.authentication.signup.contract.VerifyOtpCallback
import com.sprtcoding.safeako.databinding.ActivityForgotPasswordBinding
import com.sprtcoding.safeako.model.StaffModel
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.utils.Constants
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.Utility.encryptPassword

class ForgotPassword : AppCompatActivity(), VerifyOtpCallback {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var ccp: CountryCodePicker
    private lateinit var etPhone: EditText
    private lateinit var newPasswordContainer: LinearLayout
    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnVerify: MaterialButton
    private lateinit var btnBack: ImageView
    private lateinit var loading: ProgressDialog
    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel
    private lateinit var otpManager: OTPManager
    private var isPhoneValid = false
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Initialize View Binding
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left + 60, systemBars.top + 60, systemBars.right + 60, systemBars.bottom + 60)
            insets
        }

        init()
        afterInit()
    }

    private fun init() {
        ccp = binding.ccp
        etPhone = binding.etPhoneNum
        newPasswordContainer = binding.newPassContainer
        etNewPassword = binding.etNewPassword
        etConfirmPassword = binding.etConfirmNewPassword
        btnVerify = binding.btnVerify
        btnBack = binding.btnBack

        loading = ProgressDialog(this)

        forgotPasswordViewModel = ViewModelProvider(this)[ForgotPasswordViewModel::class.java]
        otpManager = OTPManager()
    }

    private fun afterInit() {
        btnBack.setOnClickListener { finish() }

        btnVerify.isEnabled = false

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

                if(!isValid) {
                    etPhone.error = "phone number not valid!"
                }

                isPhoneValid = isValid
                btnVerify.isEnabled = isValid
            }

        })

        btnVerify.setOnClickListener {
            loading.show()
            if(isPhoneValid) {
                val countryCode = ccp.selectedCountryCodeWithPlus
                val phone = etPhone.text.toString()
                val phoneNumber = "$countryCode$phone"

                if(phone.isEmpty()) {
                    loading.dismiss()
                    Utility.showAlertDialogWithYesNo(
                        this,
                        layoutInflater,
                        "Please type your phone number",
                        "Ok",
                        "Cancel",
                        null
                    ) { etPhone.requestFocus() }
                } else {
                    forgotPasswordViewModel.getUsers(phoneNumber)
                }
            }
        }

        observer()
    }

    private fun observer() {
        forgotPasswordViewModel.user.observe(this) { result ->
            result.onSuccess { user ->
                if(user != null) {
                    when(user) {
                        is Users -> {
                            userId = user.userId!!

                            loading.dismiss()
                            Utility.showOTPDialog(
                                this,
                                layoutInflater,
                                user.fullName!!,
                                user.phone!!,
                                loading,
                                this@ForgotPassword,
                                otpManager,
                                forgotPasswordViewModel,
                                this
                            )
                        }
                        is StaffModel -> {
                            Utility.showAlertDialog(
                                this,
                                layoutInflater,
                                "User",
                                "To change your password. contact admin facility!",
                                "Ok"
                            ){}
                        }
                    }
                } else {
                    loading.dismiss()
                    Utility.showAlertDialogWithYesNo(
                        this,
                        layoutInflater,
                        "Phone number not found!",
                        "Ok",
                        "Cancel",
                        null
                    ) {  }
                }
            }
            result.onFailure { error ->
                loading.dismiss()
                Utility.showAlertDialogWithYesNo(
                    this,
                    layoutInflater,
                    "Error: ${error.message}",
                    "Ok",
                    "Cancel",
                    null
                ) {  }
            }
        }

        forgotPasswordViewModel.isPasswordUpdate.observe(this) { success ->
            if(success) {
                loading.dismiss()
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Change Password",
                    "Password change successfully!",
                    "Ok"
                ) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onOtpVerified(message: String) {
        loading.dismiss()
        newPasswordContainer.visibility = View.VISIBLE
        Constants.VerificationStatus.isVerifying = false
        Constants.VerificationStatus.isVerified = true

        btnVerify.text = "Save"
        btnVerify.setOnClickListener {
            loading.show()
            val password = etNewPassword.text.toString()
            val confirmPass = etConfirmPassword.text.toString()
            if(password.isNotEmpty() && confirmPass.isNotEmpty()) {
                val encryptedNewPassword = encryptPassword(password)
                if(confirmPass == password) {
                    forgotPasswordViewModel.updatePassword(userId!!, encryptedNewPassword)
                } else {
                    loading.dismiss()
                    Utility.showAlertDialog(
                        this,
                        layoutInflater,
                        "Change Password",
                        "Password not match! Try Again.",
                        "Ok"
                    ) {etConfirmPassword.requestFocus()}
                }
            } else {
                loading.dismiss()
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Change Password",
                    "Please add new password!",
                    "Ok"
                ) {etNewPassword.requestFocus()}
            }
        }
    }

    override fun onOtpFailed(failed: String) {
        loading.dismiss()
        Utility.showAlertDialog(
            this,
            layoutInflater,
            "OTP",
            failed,
            "Ok"
        ) {}
    }
}