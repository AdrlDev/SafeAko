package com.sprtcoding.safeako.api.model

data class SemaphoreOTPResponse(
    val message_id: Long,
    val user_id: Long,
    val user: String,
    val account_id: Long,
    val account: String,
    val recipient: String,
    val message: String,
    val code: String?,
    val sender_name: String,
    val network: String,
    val status: String,
    val type: String,
    val source: String,
    val created_at: String,
    val updated_at: String
)
