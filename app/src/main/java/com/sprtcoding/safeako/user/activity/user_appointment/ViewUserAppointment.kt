package com.sprtcoding.safeako.user.activity.user_appointment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.model.StaffModel
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.activity.chat_activity.ChatActivity
import com.sprtcoding.safeako.utils.Utility
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ViewUserAppointment : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var tvUserName: TextView
    private lateinit var tvNote: TextView
    private lateinit var cardContact: LinearLayout
    private lateinit var tvDisplayName: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvFullName: TextView
    private lateinit var tvOccupation: TextView
    private lateinit var avatar: CircleImageView
    private lateinit var imgStatus: ImageView
    private lateinit var btnRequestUpdate: MaterialButton
    private var myID: String? = null
    private var adminID: String? = null
    private var appointmentNote: String? = null
    private var appointmentType: String? = null
    private var appointmentDate: String? = null
    private var appointmentTime: String? = null
    private var appointmentStatus: String? = null
    private var appointmentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_user_appointment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left + 60, systemBars.top + 60, systemBars.right + 60, systemBars.bottom + 60)
            insets
        }

        initViews()
        init()
        afterInit()
    }

    private fun initViews() {
        avatar = findViewById(R.id.avatar)
        btnBack = findViewById(R.id.btn_back)
        tvUserName = findViewById(R.id.username)
        tvNote = findViewById(R.id.message)
        cardContact = findViewById(R.id.card_contact)
        tvDisplayName = findViewById(R.id.tv_display_name)
        tvPhone = findViewById(R.id.tv_phone)
        tvEmail = findViewById(R.id.tv_email)
        tvFullName = findViewById(R.id.tv_full_name)
        tvOccupation = findViewById(R.id.tv_occupation)
        imgStatus = findViewById(R.id.img_status)
        btnRequestUpdate = findViewById(R.id.btn_request_update)
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        myID = intent.getStringExtra("myID")
        adminID = intent.getStringExtra("adminID")
        appointmentNote = intent.getStringExtra("appointmentNote")
        appointmentType = intent.getStringExtra("appointmentType")
        appointmentDate = intent.getStringExtra("appointmentDate")
        appointmentTime = intent.getStringExtra("appointmentTime")
        appointmentStatus = intent.getStringExtra("appointmentStatus")
        appointmentId = intent.getStringExtra("appointmentId")

        tvUserName.text = "Attention, $myID"
    }

    @SuppressLint("SetTextI18n")
    private fun afterInit() {

        Utils.getUser(adminID!!) { user ->
            when(user) {
                is StaffModel -> {
                    tvNote.text = "${user.displayName} (Staff) set you an $appointmentType on $appointmentDate at $appointmentTime" +
                            "\n\nNote: $appointmentNote"

                    tvDisplayName.text = "${user.displayName} (Staff)"
                    tvPhone.text = user.phone
                    tvEmail.visibility = View.GONE
                    tvFullName.text = user.fullName
                    tvOccupation.text = "Staff"
                }
                is Users -> {
                    tvNote.text = "${user.displayName} set you an $appointmentType on $appointmentDate at $appointmentTime" +
                            "\n\nNote: $appointmentNote"

                    tvDisplayName.text = user.displayName
                    tvPhone.text = user.phone
                    tvEmail.text = user.email
                    tvFullName.text = user.fullName
                    tvOccupation.text = user.occupation
                }
            }
        }

        Utility.getAvatar(adminID!!, avatar)

        btnBack.setOnClickListener {
            finish()
        }

        when(appointmentStatus) {
            "Positive" -> {
                imgStatus.visibility = View.VISIBLE
                cardContact.isEnabled = false
                btnRequestUpdate.visibility = View.GONE

                Picasso.get()
                    .load(R.drawable.cheking)
                    .into(imgStatus)
            }
            "Negative" -> {
                imgStatus.visibility = View.VISIBLE
                cardContact.isEnabled = false
                btnRequestUpdate.visibility = View.GONE

                Picasso.get()
                    .load(R.drawable.negative)
                    .into(imgStatus)
            }
            "Cancel" -> {
                imgStatus.visibility = View.VISIBLE
                cardContact.isEnabled = false
                btnRequestUpdate.visibility = View.GONE

                Picasso.get()
                    .load(R.drawable.cancelled)
                    .into(imgStatus)
            }
            "Done" -> {
                imgStatus.visibility = View.VISIBLE
                cardContact.isEnabled = false
                btnRequestUpdate.visibility = View.GONE

                Picasso.get()
                    .load(R.drawable.done_stamp)
                    .into(imgStatus)
            }
            "Not Active" -> {
                imgStatus.visibility = View.VISIBLE
                cardContact.isEnabled = false
                btnRequestUpdate.visibility = View.GONE

                Picasso.get()
                    .load(R.drawable.not_active_stamp)
                    .into(imgStatus)
            }
            else -> {
                imgStatus.visibility = View.GONE
                cardContact.isEnabled = true
                Utils.isRequestUpdate(myID!!) { isRequested ->
                    if(isRequested) {
                        btnRequestUpdate.visibility = View.GONE
                    } else {
                        btnRequestUpdate.visibility = View.VISIBLE
                    }
                }
            }
        }

        cardContact.setOnClickListener {
            Utils.getUser(adminID!!) { user ->
                when(user) {
                    is StaffModel -> {
                        startActivity(
                            Intent(this, ChatActivity::class.java)
                                .putExtra("receiverId", user.staffId)
                                .putExtra("receiverName", user.displayName)
                                .putExtra("userId", myID!!))
                    }
                    is Users -> {
                        startActivity(
                            Intent(this, ChatActivity::class.java)
                                .putExtra("receiverId", user.userId)
                                .putExtra("receiverName", user.displayName)
                                .putExtra("userId", myID))
                    }
                }
            }
        }

        btnRequestUpdate.setOnClickListener {
            val loading = ProgressDialog(this)
            loading.setTitle("Request Update")
            loading.setMessage("Please wait...")
            loading.show()

            Utils.getUser(myID!!) { user ->
                when(user) {
                    is Users -> {
                        val municipality = user.municipality
                        val genId = Utility.generateUpdateId()
                        val requestMap = mapOf(
                            Pair("id", genId),
                            Pair("userId", myID),
                            Pair("municipality", municipality),
                            Pair("appointmentId", appointmentId),
                            Pair("appointmentType", appointmentType),
                            Pair("msg", "${user.userId} requested to update schedule.")
                        )

                        Utils.setRequestUpdate(requestMap) { isSuccess ->
                            if(isSuccess) {
                                loading.dismiss()
                                sendNotifications(adminID!!, requestMap["msg"]!!)
                                btnRequestUpdate.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }

    }

    private fun sendNotifications(receiverId: String, msg: String) {
        Utils.sendNotification(
            receiverId,
            "Update Request",
            msg,
            "update",
            this
        )
    }
}