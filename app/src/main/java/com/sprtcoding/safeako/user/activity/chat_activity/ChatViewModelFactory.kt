package com.sprtcoding.safeako.user.activity.chat_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sprtcoding.safeako.user.activity.chat_activity.viewmodels.ChatViewModel

class ChatViewModelFactory(): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ChatViewModel::class.java)){
            return ChatViewModel() as T
        }
        throw IllegalArgumentException ("UnknownViewModel")
    }
}