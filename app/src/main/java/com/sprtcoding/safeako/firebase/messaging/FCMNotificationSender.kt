package com.sprtcoding.safeako.firebase.messaging

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.sprtcoding.safeako.utils.Constants.FCM_URL
import org.json.JSONException
import org.json.JSONObject

object FCMNotificationSender {

    fun sendNotification(title: String, body: String, fragment: String, userToken: String, context: Context) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        val mainObject = JSONObject()

        try {
            val messageObject = JSONObject()
            val notificationObject = JSONObject()
            val dataObject = JSONObject()

            notificationObject.put("title", title)
            notificationObject.put("body", body)

            // Adding fragment to data object
            dataObject.put("fragment", fragment)

            messageObject.put("token", userToken)
            messageObject.put("notification", notificationObject)
            messageObject.put("data", dataObject)

            mainObject.put("message", messageObject)
            val request = object : JsonObjectRequest(
                Method.POST,
                FCM_URL,
                mainObject,
                { response ->
                    // Handle the response
                    Log.d("Response", response.toString())
                    // Additional logic to handle the successful response
                },
                { error ->
                    // Handle the error
                    Log.e("Error", error.toString())
                    // Additional logic to handle the error response
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val accessToken = AccessToken()
                    val accessKey = accessToken.getAccessToken()

                    val headers = HashMap<String, String>()
                    headers["content-type"] = "application/json"
                    headers["authorization"] = "Bearer $accessKey" // Replace with your token or other headers
                    return headers
                }
            }

            requestQueue.add(request)

        } catch (ex: JSONException) {
            ex.message?.let { Log.d("FCMSender Error", it) }
        }
    }

}