package com.sprtcoding.safeako.admin.appointment

import android.annotation.SuppressLint
import android.app.ProgressDialog
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
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.admin.appointment.contract.IAppointment
import com.sprtcoding.safeako.admin.appointment.viewmodel.AppointmentViewModel
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.model.AppointmentModel
import com.sprtcoding.safeako.model.StaffModel
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.utils.Constants
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
    private lateinit var buttonContainer: LinearLayout
    private lateinit var tvRemarks: TextView
    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var loading: ProgressDialog
    private var appointmentId: String? = null
    private var appointmentType: String? = null
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
        buttonContainer = findViewById(R.id.button_container)
        tvRemarks = findViewById(R.id.tv_remarks)
    }

    private fun init() {
        appointmentId = intent.getStringExtra("APPOINTMENT_ID")
        appointmentType = intent.getStringExtra("APPOINTMENT_TYPE")
        userId = intent.getStringExtra("USER_ID")
        senderId = intent.getStringExtra("SENDER_ID")
        type = intent.getStringExtra("TYPE")

        loading = ProgressDialog(this)

        tvType.text = type
        getAvatar(userId!!, userAvatar)
        tvUserId.text = userId

        Utils.getSingleAppointmentById(appointmentId!!, this)

        appointmentViewModel = ViewModelProvider(this)[AppointmentViewModel::class.java]

        appointmentViewModel.getSingleAppointment(appointmentId!!)
    }

    @SuppressLint("SetTextI18n")
    private fun afterInit() {
        btnBack.setOnClickListener { finish() }

        btnSetRemark.setOnClickListener {
            Utility.animateCardView(true, cardRemark)
        }

        appointmentViewModel.appointmentSingle.observe(this) { result ->
            result.onSuccess { appointment ->
                when(appointment?.status) {
                    "Send" -> {
                        tvRemarks.visibility = View.GONE
                        buttonContainer.visibility = View.VISIBLE
                    }
                    "Done", "Cancel", "Not Active" -> {
                        cardRemark.visibility = View.GONE
                        tvRemarks.visibility = View.VISIBLE
                        buttonContainer.visibility = View.GONE

                        tvRemarks.text = "Remarks: ${appointment.type} mark as ${appointment.status}"
                    }
                    "Positive", "Negative" -> {
                        cardRemark.visibility = View.GONE
                        tvRemarks.visibility = View.VISIBLE
                        buttonContainer.visibility = View.GONE

                        tvRemarks.text = "Remarks: ${appointment.type} mark as ${appointment.status}"
                    }
                }
            }
        }

        btnUpdateSchedule.setOnClickListener {
            startActivity(Intent(this, Appointment::class.java)
                .putExtra("status", Constants.STATUS.UPDATE)
                .putExtra("appointmentId", appointmentId)
                .putExtra("USER_ID", userId)
                .putExtra("SENDER_ID", senderId)
                .putExtra("APPOINTMENT_TYPE", appointmentType))
        }

        if(appointmentType == Constants.TESTING_TAG) {
            setMark("Positive", "Negative")
        } else if(appointmentType == Constants.COUNSELING_TAG) {
            setMark("Done", "Cancel")
        }

        observer()
    }

    private fun setMark(mark1: String, mark2: String) {
        btnDone.text = mark1
        btnCancel.text = mark2

        btnDone.setOnClickListener {
            val done = btnDone.text.toString()
            loading.setTitle("Set Remark")
            loading.setMessage("Please wait...")
            loading.show()

            sendNotification(done)

            appointmentViewModel.updateAppointmentRemark(appointmentId!!, done)
        }

        btnCancel.setOnClickListener {
            val cancel = btnCancel.text.toString()
            loading.setTitle("Set Remark")
            loading.setMessage("Please wait...")
            loading.show()

            sendNotification(cancel)

            appointmentViewModel.updateAppointmentRemark(appointmentId!!, cancel)
        }

        btnNotActive.setOnClickListener {
            val notActive = btnNotActive.text.toString()
            loading.setTitle("Set Remark")
            loading.setMessage("Please wait...")
            loading.show()

            sendNotification(notActive)

            appointmentViewModel.updateAppointmentRemark(appointmentId!!, notActive)
        }
    }

    private fun sendNotification(remark: String) {
        Utils.getUser(senderId!!) { sender ->
            when(sender) {
                is StaffModel -> {
                    Utils.sendNotification(
                        userId!!,
                        "Remarks",
                        "${sender.displayName} (Staff) set your appointment to ${remark}.",
                        "appointment",
                        this
                    )
                }
                is Users -> {
                    Utils.sendNotification(
                        userId!!,
                        "Remarks",
                        "${sender.displayName} set your appointment to ${remark}.",
                        "appointment",
                        this
                    )
                }
            }
        }
    }

    private fun observer() {
        appointmentViewModel.isAppointmentRemarkUpdated.observe(this) { result ->
            result.onSuccess { success ->
                if(success) {
                    loading.dismiss()
                    Utility.showAlertDialog(
                        this,
                        layoutInflater,
                        "Remark",
                        "Scheduled Appointment set remark successfully.",
                        "Ok"
                    ){}
                } else {
                    loading.dismiss()
                    Utility.showAlertDialog(
                        this,
                        layoutInflater,
                        "Remark",
                        "Scheduled Appointment set remark failed.",
                        "Ok"
                    ){}
                }
            }
            result.onFailure { e ->
                loading.dismiss()
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Remark",
                    e.message!!,
                    "Ok"
                ){}
            }
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