package com.sprtcoding.safeako.user.activity.user_avatar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprtcoding.safeako.firebaseUtils.Utils.saveImageUrlToFireStore
import com.sprtcoding.safeako.user.activity.user_avatar.IDetailsCallBack

class AvatarViewModel: ViewModel() {
    private val _isAddSuccess = MutableLiveData<Result<Pair<Boolean, String>>>()
    val isAddSuccess: LiveData<Result<Pair<Boolean, String>>> get() = _isAddSuccess

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
}