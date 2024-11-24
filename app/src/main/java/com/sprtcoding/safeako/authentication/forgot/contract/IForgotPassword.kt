package com.sprtcoding.safeako.authentication.forgot.contract

import com.sprtcoding.safeako.model.Users

interface IForgotPassword {
    interface OTPSend {
        fun onSuccess()
        fun onError(error: Exception)
    }

    interface User {
        fun onSuccess(user: Any?)
        fun onError(error: Exception)
    }
}