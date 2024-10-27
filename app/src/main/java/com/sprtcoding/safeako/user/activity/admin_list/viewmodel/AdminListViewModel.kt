package com.sprtcoding.safeako.user.activity.admin_list.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.activity.admin_list.IAdminListCallBack

class AdminListViewModel: ViewModel() {

    var liveUsers = MutableLiveData<ArrayList<Users>>()
    private var usersList = mutableListOf<Users>()

    fun getAdminUsers(callBack: IAdminListCallBack) {
        Utils.getAdminAccounts { success, data, message ->
            if(success) {
                usersList.clear()
                usersList.addAll(data!!)
                liveUsers.value = ArrayList(usersList)
                callBack.onSuccess()
            } else {
                if (message != null) {
                    callBack.onFailure(message)
                    callBack.onError(message)
                }
            }
        }
    }
}