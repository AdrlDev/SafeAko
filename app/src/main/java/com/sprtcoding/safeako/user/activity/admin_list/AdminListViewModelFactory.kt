package com.sprtcoding.safeako.user.activity.admin_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AdminListViewModelFactory(): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AdminListViewModel::class.java)){
            return AdminListViewModel() as T
        }
        throw IllegalArgumentException ("UnknownViewModel")
    }
}