package com.sprtcoding.safeako.user.activity.user_avatar

interface IDetailsCallBack {
    fun onSuccess(message: String)
    fun onFailed(failed: String)
    fun onError(error: String)
}