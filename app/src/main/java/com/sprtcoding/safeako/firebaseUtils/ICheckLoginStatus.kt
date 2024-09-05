package com.sprtcoding.safeako.firebaseUtils

interface ICheckLoginStatus {
    fun onCheckLoginStatusSuccess(isLogin: Boolean, userId: String?, role: String?)
    fun onCheckLoginStatusFailed(message: String)
    fun onAlreadyLogout()
}