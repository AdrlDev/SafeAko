package com.sprtcoding.safeako.firebaseUtils

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.sprtcoding.safeako.model.Message
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.activity.admin_list.IAdminListCallBack
import com.sprtcoding.safeako.user.activity.user_avatar.IDetailsCallBack
import com.sprtcoding.safeako.utils.Utility
import java.util.Date

object Utils {
    //check if users login
    fun checkLoginStatus(callback: ICheckLoginStatus, context: Context) {
        val db = Firebase.firestore
        val sharedPrefs: SharedPreferences =
            context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        val phone = sharedPrefs.getString("phone", null)
        val password = sharedPrefs.getString("password", null)

        if (phone != null && password != null) {
            db.collection("users")
                .whereEqualTo("phone", phone)
                .whereEqualTo("encryptedPass", password)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        callback.onCheckLoginStatusFailed("No user found with the provided credentials")
                    } else {
                        val userDocument = documents.first()
                        val storedToken = userDocument.getString("token")
                        val userId = userDocument.getString("userId")
                        val role = userDocument.getString("role")

                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val currentToken = task.result
                                if (storedToken == currentToken) {
                                    callback.onCheckLoginStatusSuccess(true, userId, role)
                                } else {
                                    callback.onCheckLoginStatusSuccess(false, userId, role)
                                }
                            } else {
                                callback.onCheckLoginStatusFailed("Error retrieving current token: ${task.exception?.message}")
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    callback.onCheckLoginStatusFailed("Error checking login status: ${e.message}")
                }
        } else {
            callback.onCheckLoginStatusFailed("No saved credentials found")
        }
    }

    //logout user
    fun logout(callback: (Boolean, String) -> Unit, context: Context) {
        val sharedPrefs: SharedPreferences =
            context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getString("userId", null)
        val db = Firebase.firestore

        if (userId != null) {
            db.collection("users").document(userId)
                .update("token", null)
                .addOnSuccessListener {
                    with(sharedPrefs.edit()) {
                        remove("userId")
                        remove("phone")
                        remove("password")
                        remove("role")
                        apply()
                    }
                    callback(true, "Logout successful")
                }
                .addOnFailureListener { e ->
                    callback(false, "Error during logout: ${e.message}")
                }
        } else {
            callback(false, "No user logged in")
        }
    }

    //remove account
    fun removeUserFromFirestore(user: Users, callback: (Boolean, String?) -> Unit) {
        val db = Firebase.firestore
        user.userId.let { userId ->
            if (userId != null) {
                db.collection("users").document(userId)
                    .delete()
                    .addOnSuccessListener {
                        // Handle success
                        callback(true, "User removed successfully")
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                        callback(true, e.message)
                    }
            }
        }
    }

    //retrieve users
    fun getUsers(userId: String, callback: (Boolean, Users?, String?) -> Unit) {
        val db = Firebase.firestore

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null && documents.exists()) {
                    val user = documents.toObject(Users::class.java)
                    callback(true, user, null)
                } else {
                    callback(false, null, "User not found")
                }
            }
            .addOnFailureListener { exception ->
                callback(false, null, exception.message)
            }
    }

    //retrieving message from firestore
    fun getMessages(receiverId: String, callback: (Boolean, List<Message>?, String?) -> Unit) {
        val db = Firebase.firestore
        db.collection("messages")
            .whereEqualTo("receiverId", receiverId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    callback(false, null, e.message)
                } else {
                    val messageMap = mutableMapOf<String, Message>()
                    for (document in snapshot!!) {
                        val message = document.toObject(Message::class.java)
                        val senderId = message.senderId
                        val receivedOn = message.receivedOn
                        if (senderId != null && receivedOn != null) {
                            val existingMessage = messageMap[senderId]
                            if (existingMessage == null || receivedOn.after(existingMessage.receivedOn)) {
                                messageMap[senderId] = message
                            }
                        }
                    }
                    val uniqueMessages = messageMap.values.toList()
                    callback(true, uniqueMessages, null)
                }
            }
    }

    //get admin account
    fun getAdminAccounts(callback: (Boolean, List<Users>?, String?) -> Unit) {
        val db = Firebase.firestore

        db.collection("users")
            .whereEqualTo("role", "Admin")
            .get()
            .addOnSuccessListener { documents ->
                val adminAccounts = mutableListOf<Users>()

                if(documents.isEmpty) {
                    callback(false, emptyList(), "No admin account found")
                } else {
                    for (document in documents) {
                        val adminAccount = document.toObject(Users::class.java)
                        adminAccounts.add(adminAccount)
                        callback(true, adminAccounts, null)
                    }
                }
            }
            .addOnFailureListener {
                callback(false, emptyList(), "Failed to retrieve admin accounts: ${it.message}")
            }
    }

    fun getMessagesToInbox(userId: String, callback: (Boolean, List<Message>?, String?) -> Unit) {
        val db = Firebase.firestore
        val messages = mutableListOf<Message>()

        // Query for messages where senderId is equal to userId
        val senderQuery = db.collection("messages")
            .whereEqualTo("senderId", userId)

        // Query for messages where receiverId is equal to userId
        val receiverQuery = db.collection("messages")
            .whereEqualTo("receiverId", userId)

        // Run the queries and combine the results
        senderQuery.get().addOnSuccessListener { senderSnapshot ->
            for (document in senderSnapshot) {
                val message = document.toObject(Message::class.java)
                messages.add(message)
            }

            receiverQuery.get().addOnSuccessListener { receiverSnapshot ->
                for (document in receiverSnapshot) {
                    val message = document.toObject(Message::class.java)
                    messages.add(message)
                }

                // Sort messages by receivedOn field
                val sortedMessages = messages.sortedBy { it.receivedOn }

                // Pass sorted messages to the callback
                callback(true, sortedMessages, null)
            }.addOnFailureListener { e ->
                callback(false, null, e.message)
            }

        }.addOnFailureListener { e ->
            callback(false, null, e.message)
        }
    }

    //add chat
    fun addMessage(userId: String,
                   receiverId: String,
                   senderName: String?,
                   receiverName: String,
                   message: String,
                   callback: (Boolean, String?) -> Unit) {
        val db = Firebase.firestore
        val messageId = Utility.generateMessageId();

        val dateNow = Utility.getCurrentDate()

        val data = hashMapOf(
            "messageId" to messageId,
            "senderId" to userId,
            "receiverId" to receiverId,
            "senderName" to senderName,
            "receiverName" to receiverName,
            "message" to message,
            "receivedOn" to dateNow
        )

        db.collection("messages")
            .document(messageId)
            .set(data)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Message sent successfully.")
                } else {
                    callback(false, it.exception?.message)
                }
            }

    }

    fun checkCredentials(userId: String, callback: (Boolean, Boolean) -> Unit) {
        val db = Firebase.firestore

        db.collection("users")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    callback(false, false)
                } else {
                    for(snapshot in documents) {
                        val user = snapshot.toObject(Users::class.java)
                        // Check if name is null or not
                        if (user.avatar != null) {
                            callback(true, true)
                        } else {
                            // Handle case where name is null
                            // You can decide what to do in this case, e.g., skip or log the user
                            callback(true, false)
                        }
                    }
                }
            }
    }

    fun checkIfAdmin(userId: String, callback: (Boolean, Boolean) -> Unit) {
        val db = Firebase.firestore

        db.collection("users")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    callback(false, false)
                } else {
                    for(snapshot in documents) {
                        val user = snapshot.toObject(Users::class.java)
                        // Check if name is null or not
                        if (user.role == "Admin") {
                            callback(true, true)
                        } else {
                            // Handle case where name is null
                            // You can decide what to do in this case, e.g., skip or log the user
                            callback(true, false)
                        }
                    }
                }
            }
    }

    //save avatar to firestore
    private fun saveImageUrlToFirestore(downloadUrl: String, userId: String, callback: IDetailsCallBack) {
        val firestore = Firebase.firestore

        val imageUrl = mapOf("avatar" to downloadUrl)

        firestore.collection("users")
            .document(userId)
            .update(imageUrl)
            .addOnSuccessListener {
                callback.onSuccess("Avatar save Successful.")
            }
            .addOnFailureListener { e ->
                callback.onFailed("Failed to save avatar: ${e.message}")
            }
    }

    //save avatar to firebase storage
    fun uploadImageToFirebaseStorage(uri: Uri, userId: String, callback: IDetailsCallBack) {
        val firebaseStorage = FirebaseStorage.getInstance()

        val storageRef = firebaseStorage.reference.child("avatar_images/${System.currentTimeMillis()}.jpg")
        val uploadTask = storageRef.putFile(uri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Get the download URL
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                saveImageUrlToFirestore(downloadUri.toString(), userId, callback)
            }
        }.addOnFailureListener { exception ->
            // Handle unsuccessful uploads
            callback.onError("Upload failed: ${exception.message}")
        }
    }
}