package com.sprtcoding.safeako.user.activity.notification.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprtcoding.safeako.admin.appointment.contract.IAppointment
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.model.AppointmentModel

class UserAppointmentViewModel: ViewModel() {
    private val _appointment = MutableLiveData<Result<List<AppointmentModel>?>>()
    val appointment: LiveData<Result<List<AppointmentModel>?>> get() = _appointment

    private val _appointmentByType = MutableLiveData<Result<List<AppointmentModel>?>>()
    val appointmentByType: LiveData<Result<List<AppointmentModel>?>> get() = _appointmentByType

    private val _appointmentCount = MutableLiveData<Int>()
    val appointmentCount: LiveData<Int> get() = _appointmentCount

    private val _appointmentCountByType = MutableLiveData<Int>()
    val appointmentCountByType: LiveData<Int> get() = _appointmentCountByType

    fun getUserAppointment(uid: String) {
        Utils.getUserAppointment(uid, object : IAppointment.Get {
            override fun appointment(success: Boolean, appointment: List<AppointmentModel>?) {
                if(success) {
                    _appointment.postValue(Result.success(appointment))
                } else {
                    _appointment.postValue(Result.success(null))
                }
            }

            override fun onError(error: String) {
                _appointment.postValue(Result.failure(Exception(error)))
            }
        })
    }

    fun updateReadState(id: String) {
        Utils.updateReadState(id) { isUpdate ->
            Log.e("READ_UPDATE", "$isUpdate")
        }
    }

    fun getUserAppointmentByType(uid: String, type: String) {
        Utils.getUserAppointmentByType(uid, type, object : IAppointment.Get {
            override fun appointment(success: Boolean, appointment: List<AppointmentModel>?) {
                if(success) {
                    _appointmentByType.postValue(Result.success(appointment))
                }
            }

            override fun onError(error: String) {
                _appointmentByType.postValue(Result.failure(Exception(error)))
            }
        })
    }

    fun getUserAppointmentCount(uid: String) {
        Utils.getUserAppointmentCount(uid, object : IAppointment.GetCount {
            override fun count(count: Int) {
                _appointmentCount.postValue(count)
            }
        })
    }

    fun getUserAppointmentCountByType(uid: String, type: String) {
        Utils.getUserAppointmentCountByType(uid, type, object : IAppointment.GetCount {
            override fun count(count: Int) {
                _appointmentCountByType.postValue(count)
            }
        })
    }
}