package com.sprtcoding.safeako.authentication.login

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import com.sprtcoding.safeako.firebaseUtils.Utils
import com.sprtcoding.safeako.model.Users

class LoginViewModel(context: Context): ViewModel() {
    private val db = Firebase.firestore
    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun login(phone: String, password: String, callback: ILoginCallBack) {
        db.collection("users")
            .whereEqualTo("phone", phone)
            .whereEqualTo("encryptedPass", password)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // No user found
                    callback.onLoginFailed("Invalid phone number or password")
                } else {
                    // User found
                    val userDocument = documents.first()
                    val userId = userDocument.id
                    val userRole = userDocument.getString("role") ?: "User"

                    // Generate or retrieve the token
                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val token = task.result

                            // Save the token to Firestore
                            db.collection("users").document(userId)
                                .update("token", token)
                                .addOnSuccessListener {
                                    // Save credentials to SharedPreferences
                                    with(sharedPrefs.edit()) {
                                        putString("userId", userId)
                                        putString("phone", phone)
                                        putString("password", password)
                                        putString("role", userRole)
                                        apply()
                                    }
                                    callback.onLoginSuccess("Login successful", userId, userRole)
                                }
                                .addOnFailureListener { e ->
                                    callback.onLoginFailed("Error saving token: ${e.message}")
                                }
                        } else {
                            callback.onLoginFailed("Error generating token: ${task.exception?.message}")
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                // Handle error
                callback.onLoginError("Error checking user: ${e.message}")
            }
    }
}