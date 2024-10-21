package com.sprtcoding.safeako.admin.staff.contract

import com.sprtcoding.safeako.model.StaffModel

interface IStaff {
    fun onGetStaff(staffList: List<StaffModel>?)
    fun onGetStaffFailed(error: String)
}