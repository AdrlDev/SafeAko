package com.sprtcoding.safeako.model

data class StaffModel(
    val staffId: String = "",
    val adminId: String = "",
    val avatar: String = "",
    val displayName: String = "",
    val fullName: String = "",
    val jobDesc: String = "",
    val phone: String = "",
    val encryptedPass: String = "",
    val role: String = "",
    val status: String = ""
)
