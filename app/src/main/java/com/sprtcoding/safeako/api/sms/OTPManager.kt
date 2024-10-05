package com.sprtcoding.safeako.api.sms

import android.util.Log
import android.widget.TextView
import com.sprtcoding.safeako.api.sms.model.SemaphoreOTPResponse
import com.sprtcoding.safeako.authentication.signup.contract.IOtpCallback
import com.sprtcoding.safeako.model.OTPResponse
import com.sprtcoding.safeako.utils.Constants
import com.sprtcoding.safeako.utils.Utility.startCountdownTimer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OTPManager {
    private val semaphoreService: ISemaphoreService = RetrofitClient.instance.create(
        ISemaphoreService::class.java)

    fun sendOtp(apiKey: String, phoneNumber: String, message: String, senderName: String, otpCode: String? = null, timerTextView: TextView, callback: IOtpCallback) {
        val call = semaphoreService.sendOTP(apiKey, phoneNumber, message, senderName, otpCode)
        call.enqueue(object : Callback<List<SemaphoreOTPResponse>> {
            override fun onResponse(call: Call<List<SemaphoreOTPResponse>>, response: Response<List<SemaphoreOTPResponse>>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        for (otpResponse in responseBody) {
                            callback.onOtpSent(OTPResponse(true, otpResponse.code, otpResponse.message))
                            // Start the countdown timer
                            startCountdownTimer(Constants.OTPCredentials.timeout, timerTextView, callback)
                        }

                    }
                } else {
                    Log.e("OTPManager", "Failed to send OTP: ${response.errorBody()?.string()}")
                    callback.onOtpSentFailed(OTPResponse(false, null, response.errorBody()?.string()))
                }
            }

            override fun onFailure(call: Call<List<SemaphoreOTPResponse>>, t: Throwable) {
                Log.e("OTPManager", "Error: ${t.message}")
                callback.onOtpSentError("Error: ${t.message}")
            }
        })
    }
}