package com.sprtcoding.safeako.user.activity.user_avatar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprtcoding.safeako.authentication.signup.contract.ISignUp
import com.sprtcoding.safeako.firebaseUtils.Utils
import com.sprtcoding.safeako.firebaseUtils.Utils.saveImageUrlToFireStore
import com.sprtcoding.safeako.model.StaffModel
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.activity.user_avatar.IDetailsCallBack

class AvatarViewModel: ViewModel() {
    private val _isAddSuccess = MutableLiveData<Result<Pair<Boolean, String>>>()
    val isAddSuccess: LiveData<Result<Pair<Boolean, String>>> get() = _isAddSuccess

    private val _isStaffAddSuccess = MutableLiveData<Result<Pair<String, String>>>()
    val isStaffAddSuccess: LiveData<Result<Pair<String, String>>> get() = _isStaffAddSuccess

    private val _isAvatarUpdated = MutableLiveData<Boolean>()
    val isAvatarUpdated: LiveData<Boolean> get() = _isAvatarUpdated

    private val _user = MutableLiveData<Any?>()
    val user: LiveData<Any?> get() = _user

    fun saveSelectedAvatar(selectedAvatar: String, municipality: String, userId: String) {
        saveImageUrlToFireStore(selectedAvatar, municipality, userId, object : IDetailsCallBack {
            override fun onSuccess(message: String) {
                _isAddSuccess.postValue(Result.success(Pair(true, message)))
            }

            override fun onFailed(failed: String) {
                _isAddSuccess.postValue(Result.success(Pair(false, failed)))
            }

            override fun onError(error: String) {
                _isAddSuccess.postValue(Result.success(Pair(false, error)))
            }
        })
    }

    fun setStaff(staff: StaffModel?) {
        Utils.setStaff(staff, object : ISignUp {
            override fun onDataSaveSuccess(success: String, userId: String) {
                _isStaffAddSuccess.postValue(Result.success(Pair(success, userId)))
            }

            override fun onDataSaveFailed(error: String) {
                _isStaffAddSuccess.postValue(Result.failure(Exception(error)))
            }
        })
    }

    fun updateAvatar(selectedAvatar: String, municipality: String, userId: String) {
        Utils.updateAvatar(selectedAvatar, municipality, userId) { isSuccess ->
            _isAvatarUpdated.postValue(isSuccess)
        }
    }

    fun getUser(userId: String) {
        Utils.getUser(userId) { user ->
            _user.postValue(user)
        }
    }
}