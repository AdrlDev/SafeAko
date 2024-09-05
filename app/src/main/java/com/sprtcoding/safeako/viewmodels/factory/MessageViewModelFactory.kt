package com.sprtcoding.safeako.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sprtcoding.safeako.viewmodels.MessageViewModel

class MessageViewModelFactory(): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MessageViewModel::class.java)){
            return MessageViewModel() as T
        }
        throw IllegalArgumentException ("UnknownViewModel")
    }
}