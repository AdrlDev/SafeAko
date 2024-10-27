package com.sprtcoding.safeako.firebase.firebaseUtils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import com.sprtcoding.safeako.admin.appointment.contract.IAppointment
import com.sprtcoding.safeako.admin.staff.contract.IStaff
import com.sprtcoding.safeako.authentication.login.ILoginCallBack
import com.sprtcoding.safeako.authentication.signup.contract.ISignUp
import com.sprtcoding.safeako.firebase.messaging.FCMNotificationSender
import com.sprtcoding.safeako.model.AppointmentModel
import com.sprtcoding.safeako.model.AssessmentModel
import com.sprtcoding.safeako.model.Message
import com.sprtcoding.safeako.model.StaffModel
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.activity.user_avatar.IDetailsCallBack
import com.sprtcoding.safeako.user.fragment.contract.IAssessment
import com.sprtcoding.safeako.utils.Constants
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.Utility.generateAssessmentId
import java.util.Objects

object Utils {

    fun login(phone: String, password: String, sharedPrefs: SharedPreferences, callback: ILoginCallBack) {
        val db = Firebase.firestore

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

    fun checkPassword(id: String, password: String, callback: (Boolean) -> Unit) {
        val db = Firebase.firestore

        db.collection("users")
            .whereEqualTo("userId", id)
            .whereEqualTo("encryptedPass", password)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    //user found
                    callback(true)
                } else {
                    callback(false)
                }
            }
    }

    fun updatePassword(id: String, newPassword: String, callback: (Boolean) -> Unit) {
        val db = Firebase.firestore

        db.collection("users")
            .document(id)
            .update("encryptedPass", newPassword)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }

    }

    fun updatePhone(id: String, newPhone: String, callback: (Boolean) -> Unit) {
        val db = Firebase.firestore

        db.collection("users")
            .document(id)
            .update("phone", newPhone)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }

    }

    fun updateOnlineStatus(id: String, status: String, callback: (Boolean) -> Unit) {
        val db = Firebase.firestore

        db.collection("users")
            .document(id)
            .update("status", status)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
    }

    fun setToken(userId: String, token: String, callback: (Boolean) -> Unit) {
        val rtDB = Firebase.firestore
        val tokenRef = rtDB.collection("token").document(userId)
        val data = mapOf(
            "token" to token
        )
        tokenRef.set(data)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    callback(true)
                }
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    //check if users login
    fun checkLoginStatus(callback: ICheckLoginStatus, context: Context) {
        val db = Firebase.firestore
        val sharedPrefs: SharedPreferences =
            context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        val phone = sharedPrefs.getString("phone", null)
        val password = sharedPrefs.getString("password", null)

        Log.d("USER", "phone: $phone pass: $password")

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
                        val role = userDocument.getString("role")

                        val userId = if(role == "Staff") {
                            userDocument.getString("staffId")!!
                        } else {
                            userDocument.getString("userId")!!
                        }

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

    fun sendNotification(userId: String, title: String, body: String, fragment: String, context: Context) {
        val db = Firebase.firestore
        val ref = db.collection("token").document(userId)
        ref.get().addOnSuccessListener { result ->
            if(result.exists()) {
                val token = result.getString("token")
                Log.d("TOKEN_USER", token!!)
                FCMNotificationSender.sendNotification(title, body, fragment, token, context)
            }
        }
    }

    //logout user
    fun logout(callback: (Boolean, String) -> Unit, context: Context) {
        val sharedPrefs: SharedPreferences =
            context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getString("userId", null)
        val db = Firebase.firestore

        if (userId != null) {
            val collectionRef = db.collection("users").document(userId)
            collectionRef
                .get()
                .addOnSuccessListener { document ->
                    if(document.exists()) {
                        val role = document.getString("role")
                        if(role == "Staff") {
                            val dataToUpdate = mapOf(
                                "token" to null,
                                "status" to Constants.STATUS.OFFLINE
                            )
                            collectionRef
                                .update(dataToUpdate)
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
                            collectionRef
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
                        }
                    }
                }
        } else {
            callback(false, "No user logged in")
        }
    }

    fun setUser(user: Users, callback: ISignUp) {
        val db = Firebase.firestore
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

    fun setStaff(staff: StaffModel?, callback: ISignUp) {
        val db = Firebase.firestore
        db.collection("users")
            .whereEqualTo("phone", staff?.phone)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Phone number already exists
                    callback.onDataSaveFailed("Phone number already registered")
                } else {
                    // Phone number does not exist, proceed to save user
                    staff?.staffId.let { id ->
                        db.collection("users").document(id!!)
                            .set(staff!!)
                            .addOnSuccessListener {
                                // Handle success
                                callback.onDataSaveSuccess("Staff added successfully", id)
                            }
                            .addOnFailureListener { e ->
                                // Handle failure
                                callback.onDataSaveFailed("Error Staff user: ${e.message}")
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                // Handle failure in querying Firestore
                callback.onDataSaveFailed("Error checking phone number: ${e.message}")
            }
    }

    fun getStaff(adminId: String, iStaff: IStaff) {
        val db = Firebase.firestore
        db.collection("users")
            .whereEqualTo("adminId", adminId)
            .whereEqualTo("role", "Staff")
            .addSnapshotListener { value, error ->
                if(error == null && value != null) {
                    val staffList = mutableListOf<StaffModel>()

                    if(!value.isEmpty) {
                        for (staff in value) {
                            val staffs = staff.toObject(StaffModel::class.java)
                            Log.d("STAFF", staffs.fullName)
                            staffList.add(staffs)
                        }
                        iStaff.onGetStaff(staffList)
                    } else {
                        Log.d("STAFF", "EMPTY")
                        iStaff.onGetStaff(null)
                    }

                } else {
                    iStaff.onGetStaffFailed(error?.message!!)
                }
            }
    }

    //remove account
    fun removeUserFromFireStore(user: Users, callback: (Boolean, String?) -> Unit) {
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
    fun getUsers(userId: String, callback: (Boolean, Any?, String?) -> Unit) {
        val db = Firebase.firestore

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null && documents.exists()) {
                    val role = documents.getString("role")
                    val user: Any? = when (role) {
                        "Staff" -> documents.toObject(StaffModel::class.java)
                        else -> documents.toObject(Users::class.java)
                    }
                    if (user != null) {
                        callback(true, user, null)
                    } else {
                        callback(false, null, "Failed to parse user object")
                    }
                } else {
                    callback(false, null, "User not found")
                }
            }
            .addOnFailureListener { exception ->
                callback(false, null, exception.message)
            }
    }

    fun getMessagesSenderReceiver(uid: String, callback: (Boolean, List<Message>?, String?) -> Unit) {
        val db = Firebase.firestore

        // Query for messages where the user is the receiver
        val queryForReceiver = db.collection("messages").whereEqualTo("receiverId", uid)

        // Query for messages where the user is the sender
        val queryForSender = db.collection("messages").whereEqualTo("senderId", uid)

        val messageMap = mutableMapOf<String, Message>()

        // Listen to receiver messages in real-time
        queryForReceiver.addSnapshotListener { receiverSnapshot, receiverException ->
            if (receiverException != null) {
                callback(false, null, receiverException.message)
                return@addSnapshotListener
            }

            if (receiverSnapshot != null && !receiverSnapshot.isEmpty) {
                for (document in receiverSnapshot.documents) {
                    val message = document.toObject(Message::class.java)
                    val senderId = message?.senderId
                    val receiverId = message?.receiverId
                    val receivedOn = message?.receivedOn
                    if (senderId != null && receiverId != null && receivedOn != null) {
                        // Key messages by both sender and receiver IDs
                        val key = if (senderId == uid) receiverId else senderId
                        val existingMessage = messageMap[key]
                        if (existingMessage == null || receivedOn.after(existingMessage.receivedOn)) {
                            messageMap[key] = message
                        }
                    }
                }
                val uniqueMessages = messageMap.values.sortedByDescending { it.receivedOn }
                callback(true, uniqueMessages, null)
            }
        }

        // Listen to sender messages in real-time
        queryForSender.addSnapshotListener { senderSnapshot, senderException ->
            if (senderException != null) {
                callback(false, null, senderException.message)
                return@addSnapshotListener
            }

            if (senderSnapshot != null && !senderSnapshot.isEmpty) {
                for (document in senderSnapshot.documents) {
                    val message = document.toObject(Message::class.java)
                    val senderId = message?.senderId
                    val receiverId = message?.receiverId
                    val sentOn = message?.sentOn
                    if (senderId != null && receiverId != null && sentOn != null) {
                        // Key messages by both sender and receiver IDs
                        val key = if (receiverId == uid) senderId else receiverId
                        val existingMessage = messageMap[key]
                        if (existingMessage == null || sentOn.after(existingMessage.sentOn)) {
                            messageMap[key] = message
                        }
                    }
                }
                val uniqueMessages = messageMap.values.sortedByDescending { it.sentOn }
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

    //get admin account
    fun getContactFacility(displayName: String, callback: (Boolean, List<Any>?, String?) -> Unit) {
        val db = Firebase.firestore

        db.collection("users")
            .whereIn("role", listOf("Admin", "Staff"))
            .whereEqualTo("displayName", displayName)
            .get()
            .addOnSuccessListener { documents ->
                val adminAccounts = mutableListOf<Any>()

                if(documents.isEmpty) {
                    callback(false, emptyList(), "No contact facility found")
                } else {
                    for (document in documents) {
                        val role = document.getString("role")
                        val adminAccount: Any = when(role) {
                            "Staff" -> {
                                document.toObject(StaffModel::class.java)
                            }
                            else -> {
                                document.toObject(Users::class.java)
                            }
                        }
                        adminAccounts.add(adminAccount)
                        callback(true, adminAccounts, null)
                    }
                }
            }
            .addOnFailureListener {
                callback(false, emptyList(), "Failed to get contact facility: ${it.message}")
            }
    }

    fun getMessagesToInbox(userId: String, receiverId: String, callback: (Boolean, List<Message>?, String?) -> Unit) {
        val db = Firebase.firestore

        // Query for messages where senderId is equal to userId
        val senderQuery = db.collection("messages")
            .whereEqualTo("senderId", userId)
            .whereEqualTo("receiverId", receiverId)

        // Query for messages where receiverId is equal to userId
        val receiverQuery = db.collection("messages")
            .whereEqualTo("receiverId", userId)
            .whereEqualTo("senderId", receiverId)

        senderQuery.addSnapshotListener { senderSnapshot, senderError ->
            if (senderError != null) {
                callback(false, null, senderError.message)
            }

            receiverQuery.addSnapshotListener { receiverSnapshot, receiverError ->
                if (receiverError != null) {
                    callback(false, null, receiverError.message)
                }

                // Clear the messages list and add messages from both sender and receiver queries
                val messages = mutableListOf<Message>()

                senderSnapshot?.forEach { document ->
                    val message = document.toObject(Message::class.java)
                    messages.add(message)
                }

                receiverSnapshot?.forEach { document ->
                    val message = document.toObject(Message::class.java)
                    messages.add(message)
                }

                // Sort messages by receivedOn field
                val sortedMessages = messages.sortedBy { it.receivedOn }

                // Pass sorted messages to the callback
                callback(true, sortedMessages, null)
            }
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
        val messageId = Utility.generateMessageId()

        val dateNow = Utility.getCurrentDate()

        val data = hashMapOf(
            "messageId" to messageId,
            "senderId" to userId,
            "receiverId" to receiverId,
            "senderName" to senderName,
            "receiverName" to receiverName,
            "message" to message,
            "receivedOn" to dateNow,
            "sentOn" to dateNow
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
            .document(userId)
            .get()
            .addOnSuccessListener { documents ->
                if(documents.exists()) {
                    val user = documents.toObject(Users::class.java)
                    // Check if name is null or not
                    if (user?.role == "Admin" || user?.role == "Staff") {
                        callback(true, true)
                    } else {
                        // Handle case where name is null
                        // You can decide what to do in this case, e.g., skip or log the user
                        callback(true, false)
                    }
                }
            }
    }

    //save avatar to firestore
    fun saveImageUrlToFireStore(downloadUrl: String, municipality: String, userId: String, callback: IDetailsCallBack) {
        val firestore = Firebase.firestore

        val imageUrl = mapOf(
                "avatar" to downloadUrl,
                "municipality" to municipality
            )

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

    //update avatar
    fun updateAvatar(downloadUrl: String, name: String, userId: String, callback: (Boolean) -> Unit) {
        val firestore = Firebase.firestore

        val imageUrl = mapOf(
            "avatar" to downloadUrl,
            "fullName" to name
        )

        firestore.collection("users")
            .document(userId)
            .update(imageUrl)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                callback(false)
            }
    }

    //get avatar and name
    fun getUser(userId: String, callback: (Any?) -> Unit) {
        val firestore = Firebase.firestore

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { result ->
                if(result.exists()) {

                    val role = result.getString("role")

                    val user: Any? = when(role) {
                        "Staff" -> {
                            result.toObject(StaffModel::class.java)
                        } else -> {
                            result.toObject(Users::class.java)
                        }
                    }
                    callback(user)
                } else {
                    callback(null)
                }
            }
    }

    fun setRequestUpdate(updateMap: Map<String, String?>, callback: (Boolean) -> Unit) {
        val firestore = Firebase.firestore

        firestore.collection("update_request")
            .document(updateMap["id"]!!)
            .set(updateMap)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun isRequestUpdate(userId: String, callback: (Boolean) -> Unit) {
        val firestore = Firebase.firestore

        firestore.collection("update_request")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                if(!result.isEmpty || result.size() != 0) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
    }

    fun getUserAdmin(municipality: String, callback: (List<Any>?) -> Unit) {
        val firestore = Firebase.firestore

        firestore.collection("users")
            .whereEqualTo("displayName", "RHU $municipality")
            .get()
            .addOnSuccessListener { result ->
                if(!result.isEmpty) {
                    val adminList = mutableListOf<Any>()

                    for(i in result) {
                        val role = i.getString("role")
                        Log.d("ADMIN_ROLE", role!!)
                        val user: Any = when(role) {
                            "Staff" -> {
                                i.toObject(StaffModel::class.java)
                            } else -> {
                                i.toObject(Users::class.java)
                            }
                        }
                        adminList.add(user)
                    }

                    callback(adminList)
                } else {
                    callback(null)
                }
            }
    }

    //save assessment
    fun setAssessmentRequest(userId: String, docId: String, docName: String, municipality: String, iAssessment: IAssessment) {
        val db = Firebase.firestore

        val assessmentID = generateAssessmentId()

        val ref = db.collection("assessment").document(assessmentID)

        val dateNow = Utility.getCurrentDate()

        val data = mapOf(
            "id" to assessmentID,
            "userId" to userId,
            "docId" to docId,
            "docName" to docName,
            "submitOn" to dateNow,
            "status" to "pending",
            "municipality" to municipality
        )

        ref.set(data).addOnCompleteListener { res ->
            if(res.isSuccessful) {
                iAssessment.onAssessmentSubmit(true, "Your assessment is under review.")
            } else {
                iAssessment.onAssessmentSubmit(false, "Failed to submit.")
            }
        }.addOnFailureListener { e ->
            iAssessment.onAssessmentSubmit(false, e.message!!)
        }

    }

    fun updateAssessmentStatus(assessmentID: String, status: String, iAssessment: IAssessment.UpdateStatus) {
        val db = Firebase.firestore

        val ref = db.collection("assessment").document(assessmentID)

        val data = mapOf(
            "status" to status
        )

        ref.update(data).addOnCompleteListener { res ->
            if(res.isSuccessful) {
                iAssessment.updateStatus(true)
            } else {
                iAssessment.updateStatus(false)
            }
        }.addOnFailureListener {
            iAssessment.updateStatus(false)
        }

    }

    fun getAssessmentRequest(userId: String, iAssessment: IAssessment.Get) {
        val db = Firebase.firestore

        val ref = db.collection("assessment")

        ref.whereEqualTo("userId", userId).addSnapshotListener { value, error ->
            if(error == null && value != null) {
                if(!value.isEmpty) {
                    for (assessment in value) {
                        val uId = assessment.getString("userId")
                        val docId = assessment.getString("docId")
                        val docName = assessment.getString("docName")
                        val municipality = assessment.getString("municipality")
                        val submitOn = assessment.getDate("submitOn")
                        val status = assessment.getString("status")

                        iAssessment.assessment(AssessmentModel(
                            assessment.id,
                            uId,
                            docId,
                            municipality,
                            docName,
                            submitOn,
                            status!!
                        ))
                    }
                } else {
                    iAssessment.assessment(null)
                }
            }
        }
    }

    fun getAllAssessmentRequest(municipalities: String, iAssessment: IAssessment.GetAll) {
        val db = Firebase.firestore

        val ref = db.collection("assessment")
            .whereEqualTo("municipality", municipalities)

        ref.addSnapshotListener { value, error ->
            if(error == null && value != null) {
                if(!value.isEmpty) {
                    val assessmentList = ArrayList<AssessmentModel>()

                    for (assessment in value) {
                        val uId = assessment.getString("userId")
                        val docId = assessment.getString("docId")
                        val municipality = assessment.getString("municipality")
                        val docName = assessment.getString("docName")
                        val submitOn = assessment.getDate("submitOn")
                        val status = assessment.getString("status")

                        val list = AssessmentModel(
                            assessment.id,
                            uId,
                            docId,
                            municipality,
                            docName,
                            submitOn,
                            status!!
                        )

                        assessmentList.add(list)
                    }

                    iAssessment.assessment(assessmentList)
                } else {
                    iAssessment.assessment(null)
                }
            } else {
                iAssessment.onError(error?.message!!)
            }
        }
    }

    fun getAssessmentRequestCount(municipality: String, iAssessment: IAssessment.GetCount) {
        val db = Firebase.firestore

        val ref = db.collection("assessment")
            .whereEqualTo("municipality", municipality)

        ref.addSnapshotListener { value, error ->
            if(error == null && value != null) {
                if(!value.isEmpty) {
                    iAssessment.count(value.size())
                } else {
                    iAssessment.count(0)
                }
            }
        }
    }

    fun getAssessmentRequestCountStatus(iAssessment: IAssessment.GetCount) {
        val db = Firebase.firestore

        val ref = db.collection("assessment").whereEqualTo("status", "pending")

        ref.addSnapshotListener { value, error ->
            if(error == null && value != null) {
                if(!value.isEmpty) {
                    iAssessment.count(value.size())
                } else {
                    iAssessment.count(0)
                }
            }
        }
    }

    //set appointment
    fun setAppointment(appointmentModel: AppointmentModel, iAppointment: IAppointment) {
        val db = Firebase.firestore

        val ref = db.collection("appointment").document(appointmentModel.id!!)

        ref.set(appointmentModel).addOnCompleteListener { res ->
            if(res.isSuccessful) {
                iAppointment.onAddSuccess(true, "The appointment is confirmed.")
            } else {
                iAppointment.onAddSuccess(false, "Appointment set failed.")
            }
        }.addOnFailureListener { e ->
            iAppointment.onAddError(e.message!!)
        }
    }

    fun getAppointmentCountByType(uid: String, type: String, iAppointment: IAppointment.GetCount) {
        val db = Firebase.firestore

        val ref = db.collection("appointment")
            .whereEqualTo("senderId", uid)
            .whereEqualTo("type", type)

        ref.addSnapshotListener { value, error ->
            if(error == null && value != null) {
                if(!value.isEmpty) {
                    iAppointment.count(value.count())
                } else {
                    iAppointment.count(0)
                }
            } else {
                iAppointment.count(0)
            }
        }
    }

    fun getSingleAppointmentById(id: String, iAppointment: IAppointment.GetSingle) {
        val db = Firebase.firestore

        val ref = db.collection("appointment").document(id)

        ref.addSnapshotListener { value, error ->
            if(error == null && value != null) {
                if(value.exists()) {
                    val appointmentData = value.toObject(AppointmentModel::class.java)

                    iAppointment.appointment(true, appointmentData)
                } else {
                    iAppointment.appointment(true, null)
                }
            } else {
                iAppointment.onError(error?.message!!)
            }
        }
    }

    fun getAppointmentByType(uid: String, type: String, iAppointment: IAppointment.Get) {
        val db = Firebase.firestore

        val ref = db.collection("appointment")
            .whereEqualTo("senderId", uid)
            .whereEqualTo("type", type)

        ref.addSnapshotListener { value, error ->
            if(error == null && value != null) {
                if(!value.isEmpty) {
                    val listOfAppointment = ArrayList<AppointmentModel>()
                    for(appointment in value) {
                        val appointmentData = appointment.toObject(AppointmentModel::class.java)

                        listOfAppointment.add(appointmentData)
                    }
                    iAppointment.appointment(true, listOfAppointment)
                } else {
                    iAppointment.appointment(false, null)
                }
            } else {
                iAppointment.onError(error?.message!!)
            }
        }
    }

    fun updateAppointment(appointmentId: String, appointment: Map<String, Any>, iAppointment: IAppointment.Update) {
        val db = Firebase.firestore

        val ref = db.collection("appointment")

        ref.document(appointmentId).update(appointment)
            .addOnSuccessListener {
                iAppointment.onSuccess(true)
            }
            .addOnFailureListener { e ->
                iAppointment.onError(e.message!!)
            }
    }

    fun updateAppointmentRemark(appointmentId: String, remark: String, iAppointment: IAppointment.Update) {
        val db = Firebase.firestore

        val ref = db.collection("appointment")

        ref.document(appointmentId).update("status", remark)
            .addOnSuccessListener {
                iAppointment.onSuccess(true)
            }
            .addOnFailureListener { e ->
                iAppointment.onError(e.message!!)
            }
    }

    fun getUserAppointment(uid: String, iAppointment: IAppointment.Get) {
        val db = Firebase.firestore

        val ref = db.collection("appointment")
            .whereEqualTo("userId", uid)

        ref.addSnapshotListener { value, error ->
            if(error == null && value != null) {
                if(!value.isEmpty) {
                    val listOfAppointment = ArrayList<AppointmentModel>()
                    for(appointment in value) {
                        val appointmentData = appointment.toObject(AppointmentModel::class.java)

                        listOfAppointment.add(appointmentData)
                    }
                    iAppointment.appointment(true, listOfAppointment)
                } else {
                    iAppointment.appointment(false, null)
                }
            } else {
                iAppointment.appointment(false, null)
                iAppointment.onError(error?.message!!)
            }
        }
    }

    fun updateReadState(id: String, callback: (Boolean) -> Unit) {
        val db = Firebase.firestore

        db.collection("appointment")
            .document(id)
            .update("read", true)
            .addOnCompleteListener { res ->
                if(res.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
    }

    fun getUserAppointmentCount(uid: String, iAppointment: IAppointment.GetCount) {
        val db = Firebase.firestore

        val ref = db.collection("appointment")
            .whereEqualTo("userId", uid)
            .whereEqualTo("read", false)

        ref.addSnapshotListener { value, error ->
            if(error == null && value != null) {
                if(!value.isEmpty) {
                    iAppointment.count(value.count())
                } else {
                    iAppointment.count(0)
                }
            } else {
                iAppointment.count(0)
            }
        }
    }

    fun getUserAppointmentCountByType(uid: String, type: String, iAppointment: IAppointment.GetCount) {
        val db = Firebase.firestore

        val ref = db.collection("appointment")
            .whereEqualTo("userId", uid)
            .whereEqualTo("type", type)

        ref.addSnapshotListener { value, error ->
            if(error == null && value != null) {
                if(!value.isEmpty) {
                    iAppointment.count(value.count())
                } else {
                    iAppointment.count(0)
                }
            } else {
                iAppointment.count(0)
            }
        }
    }

    fun getUserAppointmentByType(uid: String, type: String, iAppointment: IAppointment.Get) {
        val db = Firebase.firestore

        val ref = db.collection("appointment")
            .whereEqualTo("userId", uid)
            .whereEqualTo("type", type)

        ref.addSnapshotListener { value, error ->
            if(error == null && value != null) {
                if(!value.isEmpty) {
                    val listOfAppointment = ArrayList<AppointmentModel>()
                    for(appointment in value) {
                        val appointmentData = appointment.toObject(AppointmentModel::class.java)

                        listOfAppointment.add(appointmentData)
                    }
                    iAppointment.appointment(true, listOfAppointment)
                } else {
                    iAppointment.appointment(false, null)
                }
            } else {
                iAppointment.onError(error?.message!!)
            }
        }
    }
}