package com.sprtcoding.safeako.authentication.signup

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.sprtcoding.safeako.model.Users

class CreateAccountViewModel: ViewModel() {
    private val db = Firebase.firestore

    fun addUserToFirestore(user: Users, callback: ICreateAccountCallBack) {
        // Check if the phone number already exists
        db.collection("users")
            .whereEqualTo("phone", user.phone)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Phone number already exists
                    callback.onDataSaveFailed("Phone number already registered")
                } else {
                    // Phone number does not exist, proceed to save user
                    user.userId?.let { userId ->
                        db.collection("users").document(userId)
                            .set(user)
                            .addOnSuccessListener {
                                // Handle success
                                callback.onDataSaveSuccess("User added successfully", userId)
                            }
                            .addOnFailureListener { e ->
                                // Handle failure
                                callback.onDataSaveFailed("Error adding user: ${e.message}")
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                // Handle failure in querying Firestore
                callback.onDataSaveFailed("Error checking phone number: ${e.message}")
            }
    }
}