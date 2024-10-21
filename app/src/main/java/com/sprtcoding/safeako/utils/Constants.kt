package com.sprtcoding.safeako.utils

import com.sprtcoding.safeako.R

object Constants {

    const val MSG_TYPE_LEFT = 0
    const val MSG_TYPE_RIGHT = 1

    const val TESTING_TAG = "Testing"
    const val COUNSELING_TAG = "Counseling"

    const val SENDER_NAME = "SafeAko"
    const val MESSAGE = "Your One Time Password is: {otp}. Please use it within 5 minutes."

    const val LOG_IN_CREDENTIALS = "user_login_credentials"
    const val PHONE_NUMBER = "phoneNumber"
    const val PASSWORD = "password"

    const val LOG_IN_TAG = "LoginActivity"

    const val COPY_DOC_ID = "1YRVvJvy3PPcx9JVF0atfgkHT6GF9C8i-nQXhWYUBDvc"
    const val FOLDER_ID = "1gEvZk3K-Ki6BDUlIe4gz4DXZmxIVRnMP"

    const val CHECK_MARK = "✓"  // Symbol for checked box
    const val EMPTY_BOX = "☐"   // Symbol for unchecked box

    val definitionVideoId = listOf(
        "1Rj_Vm2V-Qi-SYr6mlPgtGKh95loMFGhK",
        "1VIZF-IHJMiN8Nu6mEkn6lQAii1owWshE",
        "1xbVCuW18pWTFT6b8FyFfAXvjUC5NUo6Y"
    )

    val allVideoId = listOf(
        "1Rj_Vm2V-Qi-SYr6mlPgtGKh95loMFGhK",
        "1VIZF-IHJMiN8Nu6mEkn6lQAii1owWshE",
        "1xbVCuW18pWTFT6b8FyFfAXvjUC5NUo6Y",
        "17P12urG5bmzDEr4RfEb0erzQKQPoZc7M",
        "1iS8bGbTHXLzu5vBaQXEuSp4hiy21SUUA",
        "1r4C8AvS8x_H_99AOFn6WEAju6vFHYxEX",
        "1H05nVg0y9dK3PDa84DgytP87itu2lJv5",
        "1cGyVrfwgO8ZsDMtMkCRVTDyWorANE35u"
    )

    val videoTitle = listOf(
        "Introduction",
        "Type of STDs",
        "Symptoms",
        "Testing Method",
        "Prevention",
        "Awareness",
        "Impact",
        "Global Perspective"
    )

    val cat1QArray = arrayOf(
        "I am starting or ending a relationship",
        "I may not have exposed to an STI",
        "I have a symptom(or my partner has a symptom)",
        "I have questions or concerns"
    )

    val cat2QArray = arrayOf(
        "No steady partner",
        "Multiple partners",
        "Steady partner",
        "Others"
    )

    val cat3QArray = arrayOf(
        "No",
        "Yes; the most recent time was within the past 12 months",
        "Yes; the most recent time was more than 12 months ago"
    )

    val avatarMap = mapOf(
        "avatar1" to R.drawable.avatar_1,
        "avatar2" to R.drawable.avatar_2,
        "avatar3" to R.drawable.avatar_3,
        "avatar4" to R.drawable.avatar_4,
        "avatar5" to R.drawable.avatar_5,
        "avatar6" to R.drawable.avatar_6,
        "avatar7" to R.drawable.avatar_7,
        "avatar8" to R.drawable.avatar_8,
        "avatar9" to R.drawable.avatar_9,
        "avatar10" to R.drawable.avatar_10,
        "avatar11" to R.drawable.avatar_11,
        "avatar12" to R.drawable.avatar_12,
        "default_avatar" to R.drawable.avatar_man
    )

    val avatarList = listOf(
        R.drawable.avatar_1,
        R.drawable.avatar_2,
        R.drawable.avatar_3,
        R.drawable.avatar_4,
        R.drawable.avatar_5,
        R.drawable.avatar_6,
        R.drawable.avatar_7,
        R.drawable.avatar_8,
        R.drawable.avatar_9,
        R.drawable.avatar_10,
        R.drawable.avatar_11,
        R.drawable.avatar_12
    )

    object VerificationStatus {
        var isVerifying = false
        var isVerified = false
    }

    object OTPCredentials {
        var timeout: Long = 300L // Default timeout
    }

    object STATUS {
        const val ONLINE = "online"
        const val OFFLINE = "offline"
    }
}