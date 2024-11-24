package com.sprtcoding.safeako.authentication.forgot.viewmodel

import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprtcoding.safeako.api.sms.OTPManager
import com.sprtcoding.safeako.authentication.forgot.contract.IForgotPassword
import com.sprtcoding.safeako.authentication.signup.contract.IOtpCallback
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.model.OTPResponse

class ForgotPasswordViewModel: ViewModel() {
    private val _user = MutableLiveData<Result<Any?>>()
    val user: LiveData<Result<Any?>> get() = _user

    private val _onOtpSent = MutableLiveData<Result<OTPResponse>>()
    val onOtpSent: LiveData<Result<OTPResponse>> get() = _onOtpSent

    private val _onOtpSentFailed = MutableLiveData<Result<OTPResponse>>()
    val onOtpSentFailed: LiveData<Result<OTPResponse>> get() = _onOtpSentFailed

    private val _onOtpSentError = MutableLiveData<Result<String>>()
    val onOtpSentError: LiveData<Result<String>> get() = _onOtpSentError

    private val _onOtpExpired = MutableLiveData<Result<Boolean>>()
    val onOtpExpired: LiveData<Result<Boolean>> get() = _onOtpExpired

    private val _isPasswordUpdate = MutableLiveData<Boolean>()
    val isPasswordUpdate: LiveData<Boolean> get() = _isPasswordUpdate

    fun getUsers(phone: String) {
        Utils.getUserPhone(phone, object : IForgotPassword.User {
            override fun onSuccess(user: Any?) {
                _user.value = Result.success(user)
            }

            override fun onError(error: Exception) {
                _user.value = Result.failure(error)
            }
        })
    }

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

    fun updatePassword(id: String, newPassword: String) {
        Utils.updatePassword(id, newPassword) { isSuccess ->
            _isPasswordUpdate.postValue(isSuccess)
        }
    }
}