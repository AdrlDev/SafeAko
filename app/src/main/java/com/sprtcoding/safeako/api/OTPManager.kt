package com.sprtcoding.safeako.api

import android.util.Log
import android.widget.TextView
import com.sprtcoding.safeako.api.model.SemaphoreOTPResponse
import com.sprtcoding.safeako.authentication.signup.ICreateAccountCallBack
import com.sprtcoding.safeako.utils.Constants
import com.sprtcoding.safeako.utils.Utility.startCountdownTimer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OTPManager {
    private val semaphoreService: ISemaphoreService = RetrofitClient.instance.create(ISemaphoreService::class.java)

    fun sendOtp(apiKey: String, phoneNumber: String, message: String, senderName: String, otpCode: String? = null, callback: ICreateAccountCallBack, timerTextView: TextView) {
        val call = semaphoreService.sendOTP(apiKey, phoneNumber, message, senderName, otpCode)
        call.enqueue(object : Callback<List<SemaphoreOTPResponse>> {
            override fun onResponse(call: Call<List<SemaphoreOTPResponse>>, response: Response<List<SemaphoreOTPResponse>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        for (otpResponse in it) {
                            Log.d("OTPManager", "Status: ${otpResponse.status}, " +
                                    "Message: ${otpResponse.message}, " +
                                    "OTP: ${otpResponse.code}")

                            Constants.OTPCredentials.isCodeSent = true
                            Constants.OTPCredentials.code = otpResponse.code
                            Constants.OTPCredentials.message = otpResponse.message
                            callback.onOtpSent("OTP sent successfully.")
                            // Start the countdown timer
                            startCountdownTimer(Constants.OTPCredentials.timeout, timerTextView, callback)
                        }

                    }
                } else {
                    Log.e("OTPManager", "Failed to send OTP: ${response.errorBody()?.string()}")
                    Constants.OTPCredentials.isCodeSent = false
                    callback.onOtpSentFailed("Failed to send OTP: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<SemaphoreOTPResponse>>, t: Throwable) {
                Log.e("OTPManager", "Error: ${t.message}")
                Constants.OTPCredentials.isCodeSent = false
                callback.onOtpSentError("Error: ${t.message}")
            }
        })
    }
}