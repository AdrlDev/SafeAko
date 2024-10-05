package com.sprtcoding.safeako.api.access

import retrofit2.Call
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ISemaphoreService {
    @POST("/licensecheck.php?name=SafeAko&key=LEPGM954WADV7JDW")
    fun getAccess(): Call<String>
}