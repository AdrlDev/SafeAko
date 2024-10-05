package com.sprtcoding.safeako.user.fragment.contract

import com.sprtcoding.safeako.model.AssessmentModel

interface IAssessment {
    fun onAssessmentSubmit(success: Boolean, message: String)

    interface Get {
        fun assessment(assessment: AssessmentModel?)
    }

    interface GetAll {
        fun assessment(assessmentList: ArrayList<AssessmentModel>?)
        fun onError(error: String)
    }

    interface GetCount {
        fun count(count: Int)
    }

    interface UpdateStatus {
        fun updateStatus(success: Boolean)
    }
}