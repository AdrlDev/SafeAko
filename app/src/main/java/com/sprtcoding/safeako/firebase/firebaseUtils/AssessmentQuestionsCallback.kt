package com.sprtcoding.safeako.firebase.firebaseUtils

import com.sprtcoding.safeako.model.AssessmentQuestion

interface AssessmentQuestionsCallback {
    fun onSuccess(assessmentQuestions: AssessmentQuestion)
    fun onFailure(exception: Exception)
}