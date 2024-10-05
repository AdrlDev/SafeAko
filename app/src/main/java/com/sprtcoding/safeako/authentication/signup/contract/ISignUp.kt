package com.sprtcoding.safeako.authentication.signup.contract

interface ISignUp {
    fun onDataSaveSuccess(success: String, userId: String)
    fun onDataSaveFailed(error: String)
}