package com.sprtcoding.safeako.firebase.firebaseUtils

interface ICheckLoginStatus {
    fun onCheckLoginStatusSuccess(isLogin: Boolean, userId: String?, role: String?)
    fun onCheckLoginStatusFailed(message: String)
    fun onAlreadyLogout()
}