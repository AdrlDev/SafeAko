package com.sprtcoding.safeako.admin.assessment.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprtcoding.safeako.admin.appointment.contract.IAppointment
import com.sprtcoding.safeako.firebaseUtils.Utils
import com.sprtcoding.safeako.model.AppointmentModel
import com.sprtcoding.safeako.user.fragment.contract.IAssessment

class AdminAssessmentViewModel: ViewModel() {
    fun updateAssessmentStatus(assessmentId: String, status: String) {
        Utils.updateAssessmentStatus(assessmentId, status, object : IAssessment.UpdateStatus {
            override fun updateStatus(success: Boolean) {
                if(success) {
                    Log.d("AdminAssessmentViewModel", "Assessment status updated successfully")
                }
            }
        })
    }
}