package com.sprtcoding.safeako.utils

import com.google.firebase.auth.PhoneAuthProvider

class Constants {

    companion object {
        const val MSG_TYPE_LEFT = 0
        const val MSG_TYPE_RIGHT = 1
        const val PERMISSIONS_REQUEST_READ_WRITE_STORAGE = 1
    }

    object VerificationStatus {
        var isVerifying = false
        var isVerified = false
    }

    object OTPCredentials {
        var code: String? = null
        var message: String? = null
        var isCodeSent = false
        var timeout: Long = 300L // Default timeout
    }
}