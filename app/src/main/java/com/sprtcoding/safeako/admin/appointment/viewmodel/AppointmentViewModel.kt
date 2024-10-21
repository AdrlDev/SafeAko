package com.sprtcoding.safeako.admin.appointment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprtcoding.safeako.admin.appointment.contract.IAppointment
import com.sprtcoding.safeako.firebaseUtils.Utils
import com.sprtcoding.safeako.model.AppointmentModel

class AppointmentViewModel: ViewModel() {
    private val _appointment = MutableLiveData<Result<Pair<Boolean, String>>>()
    val isAppointmentSuccess: LiveData<Result<Pair<Boolean, String>>> get() = _appointment

    private val _appointmentList = MutableLiveData<Result<List<AppointmentModel>?>>()
    val appointmentList: LiveData<Result<List<AppointmentModel>?>> get() = _appointmentList

    fun setAppointment(appointment: AppointmentModel) {
        Utils.setAppointment(appointment, object : IAppointment {
            override fun onAddSuccess(success: Boolean, message: String) {
                if(success) {
                    _appointment.postValue(Result.success(Pair(true, message)))
                } else {
                    _appointment.postValue(Result.success(Pair(false, message)))
                }
            }

            override fun onAddError(error: String) {
                _appointment.postValue(Result.failure(Exception(error)))
            }
        })
    }

    fun getAppointmentByType(uid: String, type: String) {
        Utils.getAppointmentByType(uid, type, object : IAppointment.Get {
            override fun appointment(success: Boolean, appointment: List<AppointmentModel>?) {
                if(appointment != null && success) {
                    _appointmentList.postValue(Result.success(appointment))
                } else {
                    _appointmentList.postValue(Result.success(null))
                }
            }

            override fun onError(error: String) {
                _appointment.postValue(Result.failure(Exception(error)))
            }
        })
    }
}