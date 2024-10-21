package com.sprtcoding.safeako.model

data class Users(
    var userId: String? = null,
    val fullName: String? = null,
    val displayName: String? = null,
    val occupation: String? = null,
    val email: String? = null,
    val avatar: String? = null,
    val municipality: String? = null,
    val isPhoneVerified: Boolean? = null,
    val phone: String? = null,
    val encryptedPass: String? = null,
    val role: String? = null
) {
    constructor() : this(null, null, null,null, null, null, null, null, null, null)
}
