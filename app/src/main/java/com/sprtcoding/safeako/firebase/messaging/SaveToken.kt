package com.sprtcoding.safeako.firebase.messaging

import android.content.Context
import android.view.LayoutInflater
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.sprtcoding.safeako.firebase.messaging.contract.IToken
import com.sprtcoding.safeako.utils.Utility

class SaveToken {
    object Token {
        fun saveToken(uid: String, iToken: IToken, context: Context, layoutInflater: LayoutInflater) {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task: Task<String?> ->
                    if (!task.isSuccessful) {
                        Utility.showAlertDialog(
                            context,
                            layoutInflater,
                            "FCM Error",
                            "Fetching FCM registration token failed ${task.exception}",
                            "Ok"
                        ){}
                        return@addOnCompleteListener
                    }
                    // Get new FCM registration token
                    val token = task.result
                    if (token != null) {
                        iToken.onTokenGenerated(token, uid)
                    }
                }
        }
    }
}