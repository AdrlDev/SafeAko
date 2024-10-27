package com.sprtcoding.safeako.user.activity.chat_activity.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.model.Message

class ChatViewModel: ViewModel() {
    //private var messageList = mutableListOf<Message>()

    private val _liveMessage = MutableLiveData<ArrayList<Message>>()
    val liveMessage: LiveData<ArrayList<Message>> get() = _liveMessage

    fun retrieveChats(userId: String, receiverId: String, callback: (Boolean, String?) -> Unit) {
        Utils.getMessagesToInbox(userId, receiverId) { success, messages, error ->
            if (success) {
                _liveMessage.postValue(ArrayList(messages!!))
                callback(true, null)
            } else {
                callback(false, error)
            }
        }
    }

    fun addChats(userId: String,
                 receiverId: String,
                 senderName: String?,
                 receiverName: String,
                 message: String,
                 callback: (Boolean, String?) -> Unit) {
        Utils.addMessage(userId, receiverId, senderName, receiverName, message) { success, error ->
            if (success) {
                callback(true, error)
            } else {
                callback(false, error)
            }
        }
    }
}