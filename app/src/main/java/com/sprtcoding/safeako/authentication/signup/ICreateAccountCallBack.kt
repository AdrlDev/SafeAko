package com.sprtcoding.safeako.authentication.signup

interface ICreateAccountCallBack {
    fun onOtpSent(success: String)
    fun onOtpSentFailed(error: String)
    fun onDataSaveSuccess(success: String, userId: String)
    fun onDataSaveFailed(error: String)
    fun onOtpSuccess(success: String)
    fun onOtpFailed(error: String)
    fun onOtpSentError(error: String)
    fun onOtpExpired();
}