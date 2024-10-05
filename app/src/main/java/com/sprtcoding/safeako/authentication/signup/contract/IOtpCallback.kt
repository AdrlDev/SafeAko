package com.sprtcoding.safeako.authentication.signup.contract

import com.sprtcoding.safeako.model.OTPResponse

interface IOtpCallback {
    fun onOtpSent(response: OTPResponse)
    fun onOtpSentFailed(response: OTPResponse)
    fun onOtpSentError(error: String)
    fun onOtpExpired();
}