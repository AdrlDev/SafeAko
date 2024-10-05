package com.sprtcoding.safeako.admin.appointment

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.admin.appointment.contract.IAppointment
import com.sprtcoding.safeako.firebaseUtils.Utils
import com.sprtcoding.safeako.model.AppointmentModel
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.Utility.getAvatar
import de.hdodenhof.circleimageview.CircleImageView

class ViewSchedule : AppCompatActivity(), IAppointment.GetSingle {
    private lateinit var btnBack: ImageButton
    private lateinit var tvType: TextView
    private lateinit var tvUserId: TextView
    private lateinit var tvScheduleDate: TextView
    private lateinit var userAvatar: CircleImageView
    private lateinit var btnSetRemark: MaterialButton
    private lateinit var btnUpdateSchedule: MaterialButton
    private lateinit var btnDone: MaterialButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var btnNotActive: MaterialButton
    private lateinit var cardRemark: CardView
    private var appointmentId: String? = null
    private var userId: String? = null
    private var senderId: String? = null
    private var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_schedule)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left + 50, systemBars.top + 50, systemBars.right + 50, systemBars.bottom + 50)
            insets
        }

        initViews()
        init()
        afterInit()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back)
        tvType = findViewById(R.id.tv_type)
        tvUserId = findViewById(R.id.tv_user_id)
        tvScheduleDate = findViewById(R.id.tv_sched)
        userAvatar = findViewById(R.id.avatar)
        btnSetRemark = findViewById(R.id.btn_set_remark)
        btnUpdateSchedule = findViewById(R.id.btn_update)
        cardRemark = findViewById(R.id.card_remark)
        btnDone = findViewById(R.id.btn_done)
        btnCancel = findViewById(R.id.btn_cancel)
        btnNotActive = findViewById(R.id.btn_not_active)
    }

    private fun init() {
        appointmentId = intent.getStringExtra("APPOINTMENT_ID")
        userId = intent.getStringExtra("USER_ID")
        senderId = intent.getStringExtra("SENDER_ID")
        type = intent.getStringExtra("TYPE")

        tvType.text = type
        getAvatar(userId!!, userAvatar)
        tvUserId.text = userId

        Utils.getSingleAppointmentById(appointmentId!!, this)
    }

    private fun afterInit() {
        btnBack.setOnClickListener { finish() }

        btnSetRemark.setOnClickListener {
            Utility.animateCardView(true, cardRemark)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun appointment(success: Boolean, appointment: AppointmentModel?) {
        if(success && appointment != null) {
            val formattedDate = Utility.formatDateToDateString(appointment.dateOfAppointment!!)
            val formattedTime = Utility.formatDateTo12Hour(appointment.timeOfAppointment!!)
            tvScheduleDate.text = "$formattedDate at $formattedTime"
        }
    }

    override fun onError(error: String) {
        Utility.showAlertDialog(
            this,
            layoutInflater,
            "Schedule",
            error,
            "Ok"
        ){}
    }
}