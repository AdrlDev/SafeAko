package com.sprtcoding.safeako.user.activity.contact.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.model.Users

class ContactViewModel: ViewModel() {
    private val _municipality = MutableLiveData<String>()
    val municipality: LiveData<String> get() = _municipality

    private val _contactFacility = MutableLiveData<Result<List<Any>?>>()
    val contactFacility: LiveData<Result<List<Any>?>> get() = _contactFacility

    fun getMunicipality(id: String) {
        Utils.getUser(id) { user ->
            when(user) {
                is Users -> {
                    _municipality.postValue(user.municipality)
                }
            }
        }
    }

    fun getContactFacility(displayName: String) {
        Utils.getContactFacility(displayName) { success, data, message ->
            if(success) {
                _contactFacility.postValue(Result.success(data))
            } else {
                _contactFacility.postValue(Result.failure(Exception(message)))
            }
        }
    }
}