package com.sprtcoding.safeako.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.CountDownTimer
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import com.sprtcoding.safeako.authentication.signup.ICreateAccountCallBack
import com.sprtcoding.safeako.utils.Constants.Companion.PERMISSIONS_REQUEST_READ_WRITE_STORAGE
import pub.devrel.easypermissions.EasyPermissions
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object Utility {
    private var countdownTimer: CountDownTimer? = null

    //generate user id
    fun generateUserId(): String {
        val randomCenter = (10000..99999).random()
        val randomSuffix = (10..99).random()
        return "SA-$randomCenter-$randomSuffix"
    }

    //generate user id
    fun generateMessageId(): String {
        val randomCenter = (10000..99999).random()
        val randomSuffix = (10..99).random()
        return "MSG-$randomCenter-$randomSuffix"
    }

    //encrypt password
    fun encryptPassword(password: String): String{
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(password.toByteArray())
        return bytes.joinToString("") { String.format("%02x", it) }
    }

    //start otp expiration countdown
    fun startCountdownTimer(timeout: Long, timerTextView: TextView, callBack: ICreateAccountCallBack) {
        countdownTimer = object : CountDownTimer(timeout * 1000, 1000) {

            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                timerTextView.text = "Your code will expired in $secondsRemaining seconds"
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                timerTextView.text = "Time's up! Please request a new code."
                callBack.onOtpExpired();
            }
        }.start()
    }

    //cancel timer
    fun cancelCountdownTimer() {
        countdownTimer?.cancel()
        countdownTimer = null
    }

    //verify otp
    fun verifyOTP(code: String?, yourOtpInput: String, callBack: ICreateAccountCallBack) {

        if(yourOtpInput == code) {
            callBack.onOtpSuccess("Phone Number is verified.")
        } else {
            callBack.onOtpFailed("Invalid OTP")
        }

    }

    fun getCurrentDate(): Date {
        return Calendar.getInstance().time
    }

    //date time formatter
    fun formatDateTime(date: Date): String {
        // Get current date and time
        val currentDate = Calendar.getInstance().time

        // Calculate the difference in milliseconds
        val diffMillis = currentDate.time - date.time

        // Calculate the difference in minutes
        val diffMinutes = diffMillis / (60 * 1000)

        // Display logic based on time difference
        val timeAgo = when {
            diffMinutes < 1 -> "Just now"
            diffMinutes < 60 -> "$diffMinutes minutes ago"
            diffMinutes < 24 * 60 -> {
                val hoursAgo = diffMinutes / 60
                "$hoursAgo hours ago"
            }
            else -> {
                // Format full date and time
                val dateFormat = SimpleDateFormat("MMMM d, yyyy . hh:mm a", Locale.getDefault())
                dateFormat.format(date)
            }
        }

        return timeAgo
    }

    fun generateRandomName(): String {
        val firstNames = listOf(
            "John", "Jane", "Alex", "Emily", "Michael", "Sarah", "David", "Laura", "Daniel", "Jessica",
            "James", "Amanda", "Robert", "Elizabeth", "Christopher", "Megan", "Andrew", "Ashley", "Joseph", "Rachel"
        )
        val lastNames = listOf(
            "Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor",
            "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson", "Garcia", "Martinez", "Robinson"
        )

        val firstName = firstNames[Random.nextInt(firstNames.size)]
        val lastName = lastNames[Random.nextInt(lastNames.size)]

        return "$firstName $lastName"
    }

    //open camera

    fun showImagePickerOptions(activity: Activity) {
        ImagePicker.with(activity)
            .crop()	    			//Crop image(Optional), Check Customization for more option
            .compress(1024)			//Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
            .start()
    }

}