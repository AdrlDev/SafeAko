package com.sprtcoding.safeako.admin.staff.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprtcoding.safeako.admin.staff.contract.IStaff
import com.sprtcoding.safeako.firebaseUtils.Utils
import com.sprtcoding.safeako.model.StaffModel

class StaffViewModel: ViewModel() {
    private val _getStaff = MutableLiveData<Result<List<StaffModel>?>>()
    val getStaff: LiveData<Result<List<StaffModel>?>> get() = _getStaff

    fun retrieveStaff(adminUid: String) {
        Utils.getStaff(adminUid, object: IStaff {
            override fun onGetStaff(staffList: List<StaffModel>?) {
                if(staffList != null) {
                    _getStaff.postValue(Result.success(staffList))
                } else {
                    _getStaff.postValue(Result.success(null))
                }
            }

            override fun onGetStaffFailed(error: String) {
                _getStaff.postValue(Result.failure(Exception(error)))
            }
        })
    }
}