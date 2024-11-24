package com.sprtcoding.safeako.admin.staff.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprtcoding.safeako.admin.staff.contract.IStaff
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.model.StaffModel

class StaffViewModel: ViewModel() {
    private val _getStaff = MutableLiveData<Result<List<StaffModel>?>>()
    val getStaff: LiveData<Result<List<StaffModel>?>> get() = _getStaff

    private val _isPasswordUpdate = MutableLiveData<Boolean>()
    val isPasswordUpdate: LiveData<Boolean> get() = _isPasswordUpdate

    private val _isUpdate = MutableLiveData<Boolean>()
    val isUpdate: LiveData<Boolean> get() = _isUpdate

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

    fun getStaff(staffId: String, callback: IStaff.Staff) {
        Utils.getStaff(staffId, callback)
    }

    fun deleteStaff(staffId: String, callback: IStaff.DeleteStaff) {
        Utils.deleteStaff(staffId, callback)
    }

    fun updateStaff(id: String, staff: Map<String, String>) {
        Utils.updateStaff(id, staff) { isSuccess ->
            _isUpdate.postValue(isSuccess)
        }
    }

    fun updatePassword(id: String, newPassword: String) {
        Utils.updatePassword(id, newPassword) { isSuccess ->
            _isPasswordUpdate.postValue(isSuccess)
        }
    }
}