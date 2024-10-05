package com.sprtcoding.safeako.model

import java.util.Date

data class AppointmentModel(
    val id: String? = null,
    val userId: String? = null,
    val senderId: String? = null,
    val type: String? = null,
    val dateOfAppointment: Date? = null,
    val timeOfAppointment: Date? = null,
    val status: String? = null,
    val createdAt: Date? = null
)
