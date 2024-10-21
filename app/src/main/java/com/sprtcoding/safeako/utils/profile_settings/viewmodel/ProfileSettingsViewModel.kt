package com.sprtcoding.safeako.utils.profile_settings.viewmodel

import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprtcoding.safeako.api.sms.OTPManager
import com.sprtcoding.safeako.authentication.signup.contract.IOtpCallback
import com.sprtcoding.safeako.firebaseUtils.Utils
import com.sprtcoding.safeako.model.OTPResponse

class ProfileSettingsViewModel: ViewModel() {
    private val _isPasswordUpdate = MutableLiveData<Boolean>()
    val isPasswordUpdate: LiveData<Boolean> get() = _isPasswordUpdate

    private val _isPhoneUpdate = MutableLiveData<Boolean>()
    val isPhoneUpdate: LiveData<Boolean> get() = _isPhoneUpdate

    private val _isPasswordCorrect = MutableLiveData<Boolean>()
    val isPasswordCorrect: LiveData<Boolean> get() = _isPasswordCorrect

    private val _onOtpSent = MutableLiveData<Result<OTPResponse>>()
    val onOtpSent: LiveData<Result<OTPResponse>> get() = _onOtpSent

    private val _onOtpSentFailed = MutableLiveData<Result<OTPResponse>>()
    val onOtpSentFailed: LiveData<Result<OTPResponse>> get() = _onOtpSentFailed

    private val _onOtpSentError = MutableLiveData<Result<String>>()
    val onOtpSentError: LiveData<Result<String>> get() = _onOtpSentError

    private val _onOtpExpired = MutableLiveData<Result<Boolean>>()
    val onOtpExpired: LiveData<Result<Boolean>> get() = _onOtpExpired

    fun updatePassword(id: String, newPassword: String) {
        Utils.updatePassword(id, newPassword) { isSuccess ->
            _isPasswordUpdate.postValue(isSuccess)
        }
    }

    fun updatePhone(id: String, newPhone: String) {
        Utils.updatePhone(id, newPhone) { isSuccess ->
            _isPhoneUpdate.postValue(isSuccess)
        }
    }

    fun checkPassword(id: String, password: String) {
        Utils.checkPassword(id, password) { isSuccess ->
            _isPasswordCorrect.postValue(isSuccess)
        }
    }

    fun sendOtp(otpManager: OTPManager, apiKey: String, phoneNumber: String, message: String, senderName: String, otpCode: String? = null, timerTextView: TextView) {
        otpManager.sendOtp(apiKey, phoneNumber, message, senderName, otpCode, timerTextView, object :
            IOtpCallback {
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
}