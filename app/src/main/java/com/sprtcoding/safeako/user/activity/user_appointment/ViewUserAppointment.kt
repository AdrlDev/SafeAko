package com.sprtcoding.safeako.user.activity.user_appointment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.firebaseUtils.Utils
import com.sprtcoding.safeako.model.StaffModel
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.activity.chat_activity.ChatActivity
import com.sprtcoding.safeako.utils.Utility
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
    private var myID: String? = null
    private var adminID: String? = null
    private var appointmentNote: String? = null
    private var appointmentType: String? = null
    private var appointmentDate: String? = null
    private var appointmentTime: String? = null
    private var appointmentStatus: String? = null

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

    }
}