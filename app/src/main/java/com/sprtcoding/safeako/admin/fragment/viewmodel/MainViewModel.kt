package com.sprtcoding.safeako.admin.fragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.BarEntry
import com.sprtcoding.safeako.admin.appointment.contract.IAppointment
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.model.StaffModel
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.fragment.contract.IAssessment
import com.sprtcoding.safeako.utils.Constants

class MainViewModel: ViewModel() {
    private val _getAssessmentCount = MutableLiveData<Pair<Int, BarEntry>>()
    val getAssessmentCount: LiveData<Pair<Int, BarEntry>> get() = _getAssessmentCount

    private val _getCounselingCount = MutableLiveData<Pair<Int, BarEntry>>()
    val getCounselingCount: LiveData<Pair<Int, BarEntry>> get() = _getCounselingCount

    private val _getTestingCount = MutableLiveData<Pair<Int, BarEntry>>()
    val getTestingCount: LiveData<Pair<Int, BarEntry>> get() = _getTestingCount

    fun getAssessment(uid: String) {
        Utils.getUser(uid) { user ->
            when(user) {
                is StaffModel -> {
                    val municipality = user.displayName.substringAfter("RHU ").trim()
                    Utils.getAssessmentRequestCount(municipality, object : IAssessment.GetCount {
                        override fun count(count: Int) {
                            _getAssessmentCount.postValue(Pair(count, BarEntry(0f, count.toFloat())))
                        }
                    })
                }
                is Users -> {
                    val municipality = user.displayName?.substringAfter("RHU ")?.trim()
                    Utils.getAssessmentRequestCount(municipality!!, object : IAssessment.GetCount {
                        override fun count(count: Int) {
                            _getAssessmentCount.postValue(Pair(count, BarEntry(0f, count.toFloat())))
                        }
                    })
                }
            }
        }
    }

    fun getCounseling(uid: String) {
        Utils.getAppointmentCountByType(uid, Constants.COUNSELING_TAG, object : IAppointment.GetCount {
            override fun count(count: Int) {
                _getCounselingCount.postValue(Pair(count, BarEntry(1f, count.toFloat())))
            }
        })
    }

    fun getTesting(uid: String) {
        Utils.getAppointmentCountByType(uid, Constants.TESTING_TAG, object : IAppointment.GetCount {
            override fun count(count: Int) {
                _getTestingCount.postValue(Pair(count, BarEntry(2f, count.toFloat())))
            }
        })
    }
}