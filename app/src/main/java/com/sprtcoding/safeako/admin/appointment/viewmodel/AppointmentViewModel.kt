package com.sprtcoding.safeako.admin.appointment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprtcoding.safeako.admin.appointment.contract.IAppointment
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.model.AppointmentModel
import java.util.Objects

class AppointmentViewModel: ViewModel() {
    private val _appointment = MutableLiveData<Result<Pair<Boolean, String>>>()
    val isAppointmentSuccess: LiveData<Result<Pair<Boolean, String>>> get() = _appointment

    private val _appointmentList = MutableLiveData<Result<List<AppointmentModel>?>>()
    val appointmentList: LiveData<Result<List<AppointmentModel>?>> get() = _appointmentList

    private val _isAppointmentUpdated = MutableLiveData<Result<Boolean>>()
    val isAppointmentUpdated: LiveData<Result<Boolean>> get() = _isAppointmentUpdated

    private val _isAppointmentRemarkUpdated = MutableLiveData<Result<Boolean>>()
    val isAppointmentRemarkUpdated: LiveData<Result<Boolean>> get() = _isAppointmentRemarkUpdated

    private val _appointmentSingle = MutableLiveData<Result<AppointmentModel?>>()
    val appointmentSingle: LiveData<Result<AppointmentModel?>> get() = _appointmentSingle

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

    fun getSingleAppointment(appointmentId: String) {
        Utils.getSingleAppointmentById(appointmentId, object : IAppointment.GetSingle {
            override fun appointment(success: Boolean, appointment: AppointmentModel?) {
                if(success) {
                    _appointmentSingle.postValue(Result.success(appointment))
                } else {
                    _appointmentSingle.postValue(Result.success(null))
                }
            }

            override fun onError(error: String) {
                _appointmentSingle.postValue(Result.failure(Exception(error)))
            }
        })
    }

    fun updateAppointment(appointmentId: String, appointment: Map<String, Any>) {
        Utils.updateAppointment(appointmentId, appointment, object : IAppointment.Update {
            override fun onSuccess(success: Boolean) {
                _isAppointmentUpdated.postValue(Result.success(success))
            }

            override fun onError(error: String) {
                _isAppointmentUpdated.postValue(Result.failure(Exception(error)))
            }
        })
    }

    fun updateAppointmentRemark(appointmentId: String, remark: String) {
        Utils.updateAppointmentRemark(appointmentId, remark, object : IAppointment.Update {
            override fun onSuccess(success: Boolean) {
                _isAppointmentRemarkUpdated.postValue(Result.success(success))
            }

            override fun onError(error: String) {
                _isAppointmentRemarkUpdated.postValue(Result.failure(Exception(error)))
            }
        })
    }
}