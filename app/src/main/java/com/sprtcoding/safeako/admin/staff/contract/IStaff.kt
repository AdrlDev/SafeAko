package com.sprtcoding.safeako.admin.staff.contract

import com.sprtcoding.safeako.model.StaffModel

interface IStaff {
    fun onGetStaff(staffList: List<StaffModel>?)
    fun onGetStaffFailed(error: String)

    interface Remove {
        fun onRemoveClick(uid: String)
    }

    interface Staff {
        fun onSuccess(staff: StaffModel)
        fun onError(error: Exception)
    }

    interface DeleteStaff {
        fun onSuccess()
        fun onError(error: Exception)
    }

    interface UpdateStaff {
        fun onSuccess()
        fun onError(error: Exception)
    }
}