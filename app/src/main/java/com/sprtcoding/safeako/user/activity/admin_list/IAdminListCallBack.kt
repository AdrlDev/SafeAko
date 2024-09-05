package com.sprtcoding.safeako.user.activity.admin_list

import com.sprtcoding.safeako.model.Users

interface IAdminListCallBack {
    fun onSuccess()
    fun onFailure(message: String)
    fun onError(error: String)
    fun onClick(users: Users)
}