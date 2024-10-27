package com.sprtcoding.safeako.firebase.messaging.contract

interface IToken {
    fun onTokenGenerated(token: String, uid: String)
}