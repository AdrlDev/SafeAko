package com.sprtcoding.safeako.model

import java.util.Date

data class AssessmentModel(
    val id: String?,
    val userId: String?,
    val docId: String?,
    val docName: String?,
    val submitOn: Date?,
    val status: String
)
