package com.sprtcoding.safeako.authentication.signup.viewmodel

import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprtcoding.safeako.api.sms.OTPManager
import com.sprtcoding.safeako.authentication.signup.contract.IOtpCallback
import com.sprtcoding.safeako.authentication.signup.contract.ISignUp
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils.setUser
import com.sprtcoding.safeako.model.OTPResponse
import com.sprtcoding.safeako.model.SignUpResponse
import com.sprtcoding.safeako.model.Users

class CreateAccountViewModel: ViewModel() {

    private val _signUpResponse = MutableLiveData<Result<SignUpResponse>>()
    val signUpResponse: LiveData<Result<SignUpResponse>> get() = _signUpResponse

    private val _onOtpSent = MutableLiveData<Result<OTPResponse>>()
    val onOtpSent: LiveData<Result<OTPResponse>> get() = _onOtpSent

    private val _onOtpSentFailed = MutableLiveData<Result<OTPResponse>>()
    val onOtpSentFailed: LiveData<Result<OTPResponse>> get() = _onOtpSentFailed

    private val _onOtpSentError = MutableLiveData<Result<String>>()
    val onOtpSentError: LiveData<Result<String>> get() = _onOtpSentError

    private val _onOtpExpired = MutableLiveData<Result<Boolean>>()
    val onOtpExpired: LiveData<Result<Boolean>> get() = _onOtpExpired

    fun sendOtp(otpManager: OTPManager, apiKey: String, phoneNumber: String, message: String, senderName: String, otpCode: String? = null, timerTextView: TextView) {
        otpManager.sendOtp(apiKey, phoneNumber, message, senderName, otpCode, timerTextView, object : IOtpCallback {
            override fun onOtpSent(response: OTPResponse) {
                _onOtpSent.postValue(Result.success(response))
            }

            override fun onOtpSentFailed(response: OTPResponse) {
                _onOtpSentFailed.postValue(Result.success(response))
            }

            override fun onOtpSentError(error: String) {
                _onOtpSentError.postValue(Result.success(error))
            }

            override fun onOtpExpired() {
                _onOtpExpired.postValue(Result.success(true))
            }
        })
    }

    fun addUserToFireStore(user: Users) {
        // Check if the phone number already exists
        setUser(user, object : ISignUp {

            override fun onDataSaveSuccess(success: String, userId: String) {
                _signUpResponse.postValue(Result.success(SignUpResponse(success, userId)))
            }

            override fun onDataSaveFailed(error: String) {
                _signUpResponse.postValue(Result.failure(Exception(error)))
            }

        })
    }
}