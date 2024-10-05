package com.sprtcoding.safeako.model

data class OTPResponse(
    var isCodeSent: Boolean,
    var code: String?,
    var message: String?,

)
