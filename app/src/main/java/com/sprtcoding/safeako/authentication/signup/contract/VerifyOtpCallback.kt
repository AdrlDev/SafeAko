package com.sprtcoding.safeako.authentication.signup.contract

interface VerifyOtpCallback {
    fun onOtpVerified(message: String)
    fun onOtpFailed(failed: String)
}