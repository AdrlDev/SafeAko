package com.sprtcoding.safeako.utils.profile_settings

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import com.sprtcoding.safeako.BuildConfig
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.api.sms.OTPManager
import com.sprtcoding.safeako.authentication.signup.contract.VerifyOtpCallback
import com.sprtcoding.safeako.utils.Constants
import com.sprtcoding.safeako.utils.Constants.MESSAGE
import com.sprtcoding.safeako.utils.Constants.SENDER_NAME
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.profile_settings.viewmodel.ProfileSettingsViewModel

class ChangePhone : AppCompatActivity(), VerifyOtpCallback {
    private lateinit var btnBack: ImageButton
    private lateinit var etPassword: TextInputEditText
    private lateinit var etNewPhone: TextInputEditText
    private lateinit var otpLL: LinearLayout
    private lateinit var tvOtpSec: TextView
    private lateinit var etOtp: TextInputEditText
    private lateinit var saveBtn: MaterialButton
    private lateinit var profileSettingsViewModel: ProfileSettingsViewModel
    private lateinit var loading: ProgressDialog
    private lateinit var otpManager: OTPManager
    private var myId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_phone)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left + 60, systemBars.top + 60, systemBars.right + 60, systemBars.bottom + 60)
            insets
        }

        initViews()
        init()
        afterInit()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back)
        etPassword = findViewById(R.id.et_password)
        etNewPhone = findViewById(R.id.et_phone)
        otpLL = findViewById(R.id.otp_ll)
        tvOtpSec = findViewById(R.id.otp_sec)
        etOtp = findViewById(R.id.et_otp)
        saveBtn = findViewById(R.id.btn_save)
    }

    private fun init() {
        myId = intent.getStringExtra("UID")

        loading = ProgressDialog(this)
        loading.setTitle("Change Phone Number")
        loading.setMessage("Please wait...")

        profileSettingsViewModel = ViewModelProvider(this)[ProfileSettingsViewModel::class.java]
        otpManager = OTPManager()

        etNewPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    return
                }

                if (s.toString().startsWith("+63")) {
                    // Country code is already present, do nothing
                    return
                }

                try {
                    val phoneNumberUtil = PhoneNumberUtil.getInstance()
                    val number = phoneNumberUtil.parse(s.toString(), "PH") // PH for Philippines

                    if (phoneNumberUtil.isValidNumber(number)) {
                        val formattedNumber = phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
                        etNewPhone.setText(formattedNumber)
                        etNewPhone.setSelection(formattedNumber.length)
                    } else {
                        etNewPhone.error = "Invalid phone number!"
                    }
                } catch (e: NumberParseException) {
                    Log.d("TAG", "Error parsing phone number: $e")
                }
            }
        })
    }

    private fun afterInit() {
        btnBack.setOnClickListener {
            finish()
        }

        saveBtn.setOnClickListener {
            loading.show()
            val password = etPassword.text.toString()
            val phone = etNewPhone.text.toString()
            val encryptedPass = Utility.encryptPassword(password)

            if(password.isEmpty()) {
                showCustomDialog("Please enter your password to continue.")
                loading.dismiss()
            } else if(phone.isEmpty()) {
                showCustomDialog("Please enter your new phone number to continue.")
                loading.dismiss()
            } else {
                profileSettingsViewModel.checkPassword(myId!!, encryptedPass)
            }
        }

        observer()
    }

    @SuppressLint("SetTextI18n")
    private fun observer() {
        profileSettingsViewModel.isPasswordCorrect.observe(this) { isPasswordCorrect ->
            loading.dismiss()
            if(isPasswordCorrect) {
                val phone = etNewPhone.text.toString()

                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Verify Phone Number",
                    "Please verify phone number",
                    "Verify"
                ) {
                    loading.show()
                    profileSettingsViewModel.sendOtp(otpManager, BuildConfig.API_KEY, phone, MESSAGE, SENDER_NAME, null, tvOtpSec)
                }

            } else {
                showCustomDialog("Password is incorrect. Cannot change your phone number!")
            }
        }

        profileSettingsViewModel.onOtpSent.observe(this) { result ->
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
                        otpLL.isVisible = true
                        Constants.VerificationStatus.isVerifying = true
                        saveBtn.text = "Submit OTP"
                    }

                    saveBtn.setOnClickListener {
                        loading.show()
                        //verify code here
                        val code = response.code
                        val myCode = etOtp.text.toString()

                        if(myCode.isNotEmpty()) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                Utility.verifyOTP(code, myCode, this)
                            }, 3000)
                        } else {
                            loading.dismiss()
                            Utility.showAlertDialog(
                                this,
                                layoutInflater,
                                "OTP",
                                "Please enter OTP.",
                                "Ok",
                            ) {
                                etOtp.requestFocus()
                            }
                        }
                    }
                }
            }
        }

        profileSettingsViewModel.onOtpSentFailed.observe(this) { result ->
            result.onSuccess { response ->
                loading.dismiss()
                if(!response.isCodeSent) {
                    Utility.showAlertDialog(
                        this,
                        layoutInflater,
                        "OTP",
                        response.message!!,
                        "Ok"
                    ) {
                        saveBtn.text = "Send Again"
                    }
                }
            }
        }

        profileSettingsViewModel.onOtpSentError.observe(this) { result->
            result.onSuccess { err ->
                loading.dismiss()
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "OTP",
                    err,
                    "Ok"
                ) {

                }
            }
        }

        profileSettingsViewModel.onOtpExpired.observe(this) { result ->
            result.onSuccess { isExpired ->
                if(isExpired) {
                    loading.show()
                    Utility.showAlertDialogWithYesNo(
                        this,
                        layoutInflater,
                        "OTP Expired. do you want to resend it?",
                        "No",
                        "Resend",
                        null
                    ) {
                        loading.show()
                        profileSettingsViewModel.sendOtp(otpManager, BuildConfig.API_KEY, etNewPhone.text.toString(), MESSAGE, SENDER_NAME, null, tvOtpSec)
                    }
                }
            }
        }

        profileSettingsViewModel.isPhoneUpdate.observe(this) { isPhoneUpdated ->
            if (isPhoneUpdated) {
                loading.dismiss()
                showCustomDialog("Phone number updated successfully!")
            } else {
                loading.dismiss()
                showCustomDialog("Failed to update phone number!")
            }
        }
    }

    private fun showCustomDialog(message: String) {
        if(message == "Phone number updated successfully!") {
            Utility.showAlertDialog(
                this,
                layoutInflater,
                "Change Phone",
                message,
                "Ok"
            ) {finish()}
        } else {
            Utility.showAlertDialog(
                this,
                layoutInflater,
                "Change Phone",
                message,
                "Ok"
            ) {}
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onOtpVerified(message: String) {
        loading.dismiss()
        Utility.showAlertDialog(
            this,
            layoutInflater,
            "Phone Verified",
            "Phone number verified successfully.",
            "Save Phone"
        ) {
            loading.show()
            profileSettingsViewModel.updatePhone(myId!!, etNewPhone.text.toString())
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