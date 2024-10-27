package com.sprtcoding.safeako.authentication.login.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import com.sprtcoding.safeako.authentication.login.ILoginCallBack
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.model.LoginResponse

class LoginViewModel(context: Context): ViewModel() {
    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _response = MutableLiveData<Result<LoginResponse>>()
    val response: LiveData<Result<LoginResponse>> get() = _response

    fun login(phone: String, password: String) {
        Utils.login(phone, password, sharedPrefs, object : ILoginCallBack {
            override fun onLoginSuccess(msg: String, userId: String, role: String) {
                _response.postValue(Result.success(LoginResponse(msg, userId, role)))
            }

            override fun onLoginFailed(msg: String) {
                _response.postValue(Result.success(LoginResponse(msg, null, null)))
            }

            override fun onLoginError(error: String) {
                _response.postValue(Result.success(LoginResponse(error, null, null)))
            }

        })
    }

    fun updateOnlineStatus(id: String, status: String) {
        Utils.updateOnlineStatus(id, status) { isUpdated ->
            if (isUpdated) {
                // Status updated successfully
                Log.d("STATUS", "Status updated successfully")
            } else {
                // Failed to update status
                Log.d("STATUS", "Failed to update status")
            }
        }
    }
}