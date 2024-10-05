package com.sprtcoding.safeako.api.access

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccessManager {
    private val accessService: ISemaphoreService = AccessClient.instance.create(
        ISemaphoreService::class.java)

    fun getAccess(callback: ResponseAccessCallback) {
        val call = accessService.getAccess()
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful) {
                    val responseBody = response.body()
                    // Handle the response body
                    println("Response Body: $responseBody")
                    callback.onSuccess(responseBody!!)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("AccessManager", "Error: ${t.message}")
                callback.onFailure(t.message!!)
            }
        })
    }
}