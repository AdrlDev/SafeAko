package com.sprtcoding.safeako.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.badge.BadgeDrawable
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.docs.v1.DocsScopes
import com.google.api.services.drive.DriveScopes
import com.google.auth.oauth2.GoogleCredentials.fromStream
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.authentication.signup.contract.IOtpCallback
import com.sprtcoding.safeako.authentication.signup.contract.VerifyOtpCallback
import com.sprtcoding.safeako.firebaseUtils.Utils
import com.sprtcoding.safeako.admin.assessment.ViewAssessment
import com.sprtcoding.safeako.user.activity.user_avatar.adapter.AvatarAdapter
import com.sprtcoding.safeako.utils.Constants.avatarMap
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Random

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

    fun generateAssessmentId(): String {
        val randomCenter = (10000..99999).random()
        val randomSuffix = (10..99).random()
        return "ASSESSMENT-$randomCenter-$randomSuffix"
    }

    fun generateAppointmentId(): String {
        val randomCenter = (10000..99999).random()
        val randomSuffix = (10..99).random()
        return "APPOINTMENT-$randomCenter-$randomSuffix"
    }

    //encrypt password
    fun encryptPassword(password: String): String{
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(password.toByteArray())
        return bytes.joinToString("") { String.format("%02x", it) }
    }

    //start otp expiration countdown
    fun startCountdownTimer(timeout: Long, timerTextView: TextView, callBack: IOtpCallback) {
        countdownTimer = object : CountDownTimer(timeout * 1000, 1000) {

            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                timerTextView.text = "Your code will expired in $secondsRemaining seconds"
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                timerTextView.text = "Time's up! Please request a new code."
                callBack.onOtpExpired()
            }
        }.start()
    }

    //cancel timer
    fun cancelCountdownTimer() {
        countdownTimer?.cancel()
        countdownTimer = null
    }

    //verify otp
    fun verifyOTP(code: String?, yourOtpInput: String, callBack: VerifyOtpCallback) {
        if(yourOtpInput == code) {
            callBack.onOtpVerified("Phone Number is verified.")
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

    fun showAvatarPickerDialog(context: Context, avatarList: List<Int>, selectedAvatarImageView: CircleImageView, callback: (Int) -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.avatar_dialog, null)
        val avatarRecyclerView = dialogView.findViewById<RecyclerView>(R.id.avatarRecyclerView)

        // Show the dialog
        val dialog = AlertDialog.Builder(context, R.style.TransparentAlertDialog)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        avatarRecyclerView.layoutManager = GridLayoutManager(context, 4) // 3 columns
        val adapter = AvatarAdapter(avatarList) { selectedAvatar ->
            // Use the selected avatar in your app (e.g., set to an ImageView or save the selection)
            callback(selectedAvatar)
            Picasso.get()
                .load(selectedAvatar)
                .placeholder(R.drawable.avatar_man)
                .into(selectedAvatarImageView)
            // Dismiss the dialog after selection
            dialog.dismiss()
        }

        avatarRecyclerView.adapter = adapter

        dialog.show()

        // Apply margin to the dialog (reduce width by 32dp for 16dp margins on both sides)
        val marginInPixels = (16 * context.resources.displayMetrics.density).toInt() // 16dp margin
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,  // Full width
            ViewGroup.LayoutParams.WRAP_CONTENT  // Adjust height to wrap content
        )

        // Set the dialog window with margins
        dialog.window?.decorView?.setPadding(marginInPixels, 0, marginInPixels, 0)
    }

    fun initializePlayer(videoUri: Uri, exoPlayer: ExoPlayer, playerView: PlayerView) {
        // Attach the player to the PlayerView
        playerView.player = exoPlayer

        // Prepare the media source
        val mediaItem = MediaItem.fromUri(videoUri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()

        // Start playback when ready
        exoPlayer.playWhenReady = true
    }

    fun generateExoPlayerThumbnail(videoUri: Uri, context: Context): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)
        val thumbnail = retriever.frameAtTime // You can provide a specific timestamp here
        retriever.release()
        return thumbnail
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("InflateParams")
    fun showAlertDialogWithYesNo(context: Context, layoutInflater: LayoutInflater, message: String, btnNo: String, btnYes: String, btnNoAction: (() -> Unit?)?, btnYesAction: () -> Unit) {
        val view = layoutInflater.inflate(R.layout.dialog_yes_no, null)
        val alertDialog = AlertDialog.Builder(context, R.style.TransparentAlertDialog)
            .setView(view)
            .setCancelable(false)
            .create()

        val tvMessage = view.findViewById<TextView>(R.id.message)
        tvMessage.text = message

        val btnNoDialog = view.findViewById<TextView>(R.id.tv_no)
        btnNoDialog.text = btnNo

        val btnYesDialog = view.findViewById<TextView>(R.id.tv_yes)
        btnYesDialog.text = btnYes

        btnNoDialog.setOnClickListener {
            if (btnNoAction != null) {
                btnNoAction()
            }
            alertDialog.dismiss()
        }

        btnYesDialog.setOnClickListener {
            btnYesAction()
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    @SuppressLint("InflateParams")
    fun showAlertDialog(context: Context, layoutInflater: LayoutInflater, title: String, message: String, btnYes: String, btnYesAction: () -> Unit) {
        val view = layoutInflater.inflate(R.layout.dialog_one_button, null)
        val alertDialog = AlertDialog.Builder(context, R.style.TransparentAlertDialog)
            .setView(view)
            .setCancelable(false)
            .create()

        val tvTitle = view.findViewById<TextView>(R.id.title)
        tvTitle.text = title

        val tvMessage = view.findViewById<TextView>(R.id.message)
        tvMessage.text = message

        val btnYesDialog = view.findViewById<TextView>(R.id.tv_yes)
        btnYesDialog.text = btnYes

        btnYesDialog.setOnClickListener {
            btnYesAction()
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    @SuppressLint("InflateParams", "MissingInflatedId")
    fun showAppointmentDialog(context: Context, layoutInflater: LayoutInflater, btnTesting: () -> Unit , btnCounseling: () -> Unit) {
        val view = layoutInflater.inflate(R.layout.appointment_dialog, null)
        val alertDialog = AlertDialog.Builder(context, R.style.TransparentAlertDialog)
            .setView(view)
            .setCancelable(false)
            .create()

        val btnTestingBtn = view.findViewById<TextView>(R.id.tv_testing)
        val btnCounselingBtn = view.findViewById<TextView>(R.id.tv_counseling)

        btnTestingBtn.setOnClickListener {
            btnTesting()
            alertDialog.dismiss()
        }

        btnCounselingBtn.setOnClickListener {
            btnCounseling()
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    fun getAvatar(id: String, imgUserPic: CircleImageView) {
        Utils.getUsers(id) { success, user, _ ->
            if(success) {
                if (user != null) {
                    val avatarImg = user.avatar

                    if(avatarImg != null) {
                        if(avatarMap.containsKey(avatarImg)) {
                            Picasso.get()
                                .load(avatarMap[avatarImg]!!)
                                .placeholder(R.drawable.avatar_man)
                                .fit()
                                .into(imgUserPic)
                        } else {
                            setDefaultAvatar(user.role!!, imgUserPic)
                        }
                    } else {
                        setDefaultAvatar(user.role!!, imgUserPic)
                    }
                }
            }
        }
    }

    private fun setDefaultAvatar(role: String, imgUserPic: CircleImageView) {
        if(role == "Admin") {
            Picasso.get()
                .load(R.drawable.avatar)
                .placeholder(R.drawable.avatar_man)
                .fit()
                .into(imgUserPic)
        } else {
            Picasso.get()
                .load(R.drawable.avatar_man)
                .placeholder(R.drawable.avatar_man)
                .fit()
                .into(imgUserPic)
        }
    }

    @Throws(IOException::class, GeneralSecurityException::class)
    suspend fun getAccessToken(context: Context): String? = withContext(Dispatchers.IO) {
        val jsonFactory = GsonFactory.getDefaultInstance()

        // Load client secrets from the assets folder.
        val credentialsStream: InputStream = context.assets.open("credentials.json")
        val googleCredentials = fromStream(credentialsStream)
            .createScoped(listOf(
                DocsScopes.DOCUMENTS_READONLY,
                DocsScopes.DOCUMENTS,  // Google Docs scope
                DriveScopes.DRIVE,     // Google Drive scope
                DriveScopes.DRIVE_FILE
            ))

        // Fetch the access token.
        googleCredentials.refreshAccessToken()?.tokenValue
    }

    fun generateRandomDocumentName(): String {
        val prefix = "Document_"
        val timestamp = System.currentTimeMillis()
        val randomSuffix = Random().nextInt(1000) // Generates a random number between 0 and 999
        return "$prefix${timestamp}_$randomSuffix"
    }

    fun loadDocument(documentId: String, userId: String, assessmentId: String, senderId: String, context: Context) {
        // Construct the URL for the Google Docs document
        val documentUrl = "https://docs.google.com/document/d/$documentId/view"

        // Launch the WebViewActivity to display the document
        val intent = Intent(context, ViewAssessment::class.java).apply {
            putExtra("DOCUMENT_URL", documentUrl)
            putExtra("USER_ID", userId)
            putExtra("SENDER_ID", senderId)
            putExtra("ASSESSMENT_ID", assessmentId)
        }
        context.startActivity(intent)
    }

    fun showNotificationDot(hasNotifications: Boolean, count: Long, badgeDrawable: BadgeDrawable) {
        if (hasNotifications && count.toInt() > 0) {
            badgeDrawable.isVisible = true
            badgeDrawable.number = count.toInt() // You can set the number of notifications here
        } else {
            badgeDrawable.isVisible = false
            badgeDrawable.clearNumber()
        }
    }

    fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
        // Get the current date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Show DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Handle the date selection
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear" // Format the date
                onDateSelected(formattedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {
        // Get the current time
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Show TimePickerDialog
        val timePickerDialog = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                // Format and handle the selected time
                val formattedTime = formatTimeTo12Hour(selectedHour, selectedMinute)
                onTimeSelected(formattedTime)
            },
            hour, minute, false // The last parameter is for 24-hour format (set to false for AM/PM format)
        )

        timePickerDialog.show()
    }

    // Function to format time to 12-hour format with AM/PM
    @SuppressLint("DefaultLocale")
    private fun formatTimeTo12Hour(hour: Int, minute: Int): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val hourIn12Format = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        return String.format("%02d:%02d %s", hourIn12Format, minute, amPm)
    }

    fun parseTimeToDate(time: String): Date? {
        return try {
            // Define the input format (12-hour with AM/PM)
            val inputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            // Parse the time string into a Date object
            inputFormat.parse(time)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun formatDateTo12Hour(date: Date): String {
        // Define the output format as 12-hour with AM/PM
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return outputFormat.format(date) // Format the Date object to 12-hour format
    }

    fun parseDateStringToDate(dateString: String): Date? {
        return try {
            // Define the input format for the date string
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            // Parse the date string into a Date object
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun formatDateToDateString(date: Date): String {
        // Define the output format as 12-hour with AM/PM
        val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        return outputFormat.format(date) // Format the Date object to 12-hour format
    }

    fun animateCardView(show: Boolean, cardView: CardView) {
        if (show) {
            cardView.visibility = View.VISIBLE
            val fadeIn = AlphaAnimation(0f, 1f).apply {
                duration = 500 // Animation duration in milliseconds
                fillAfter = true
            }
            cardView.startAnimation(fadeIn)
        } else {
            val fadeOut = AlphaAnimation(1f, 0f).apply {
                duration = 500
                fillAfter = true
            }
            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    cardView.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
            cardView.startAnimation(fadeOut)
        }
    }

}