package com.sprtcoding.safeako.authentication.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sprtcoding.safeako.authentication.signup.CreateAccountViewModel

class LoginViewModelFactory(): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return CreateAccountViewModel() as T
        }
        throw IllegalArgumentException ("UnknownViewModel")
    }
}