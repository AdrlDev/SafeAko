package com.sprtcoding.safeako.api.access

interface ResponseAccessCallback {
    fun onSuccess(response: String)
    fun onFailure(errorMessage: String)
}