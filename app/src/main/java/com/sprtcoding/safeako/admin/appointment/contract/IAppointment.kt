package com.sprtcoding.safeako.admin.appointment.contract

import com.sprtcoding.safeako.model.AppointmentModel

interface IAppointment {
    fun onAddSuccess(success: Boolean, message: String)
    fun onAddError(error: String)

    interface GetCount {
        fun count(count: Int)
    }

    interface Get {
        fun appointment(success: Boolean, appointment: List<AppointmentModel>?)
        fun onError(error: String)
    }

    interface GetSingle {
        fun appointment(success: Boolean, appointment: AppointmentModel?)
        fun onError(error: String)
    }

    interface GetType {
        fun appointment(success: Boolean, appointment: List<AppointmentModel>?)
        fun onError(error: String)
    }
}