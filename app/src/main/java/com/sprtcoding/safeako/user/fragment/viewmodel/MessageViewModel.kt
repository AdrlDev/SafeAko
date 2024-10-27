package com.sprtcoding.safeako.user.fragment.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.model.Message
import com.sprtcoding.safeako.model.Users

class MessageViewModel: ViewModel() {
    var liveMessage = MutableLiveData<ArrayList<Message>>()
    private var messageList = arrayListOf<Message>()

    private var liveUserList = MutableLiveData<ArrayList<Users>>()
    private var userList = arrayListOf<Users>()

    init {
        liveMessage.value = messageList
    }

    init {
        liveUserList.value = userList
    }

    fun remove(message: Message){
        messageList.remove(message)
        liveMessage.value = ArrayList(messageList)
    }

    fun fetchMessages(uid: String, callback: (Boolean, String?) -> Unit) {
        Utils.getMessagesSenderReceiver(uid) { success, messages, error ->
            if (success) {
                messageList.clear()
                messageList.addAll(messages!!)
                liveMessage.value = ArrayList(messageList)
                callback(true, null)
            } else {
                callback(false, error)
            }
        }
    }
}
