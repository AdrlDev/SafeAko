package com.sprtcoding.safeako.api.sms

import com.sprtcoding.safeako.api.sms.model.SemaphoreOTPResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ISemaphoreService {
    @FormUrlEncoded
    @POST("/api/v4/otp")
    fun sendOTP(
        @Field("apikey") apiKey: String,
        @Field("number") phoneNumber: String,
        @Field("message") message: String,
        @Field("sendername") senderName: String,
        @Field("code") otpCode: String? = null
    ): Call<List<SemaphoreOTPResponse>>
}