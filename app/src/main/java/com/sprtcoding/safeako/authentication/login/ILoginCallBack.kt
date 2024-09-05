package com.sprtcoding.safeako.authentication.login

interface ILoginCallBack {
    fun onLoginSuccess(msg: String, userId: String, role: String)
    fun onLoginFailed(msg: String)
    fun onLoginError(error: String)
}