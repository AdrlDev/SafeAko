package com.sprtcoding.safeako.admin.appointment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.admin.appointment.contract.IAppointment
import com.sprtcoding.safeako.admin.appointment.viewmodel.AppointmentViewModel
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.model.AppointmentModel
import com.sprtcoding.safeako.model.StaffModel
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.utils.Constants
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.Utility.generateAppointmentId
import com.sprtcoding.safeako.utils.Utility.getAvatar
import de.hdodenhof.circleimageview.CircleImageView

class Appointment : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var tvUserId: TextView
    private lateinit var tvTitle: TextView
    private lateinit var imgAvatar: CircleImageView
    private lateinit var selectDate: MaterialButton
    private lateinit var selectTime: MaterialButton
    private lateinit var btnConfirm: MaterialButton
    private lateinit var etNote: TextInputEditText
    private var userId: String? = null
    private var senderId: String? = null
    private var appointmentType: String? = null
    private var appointmentId: String? = null
    private var status: String? = null
    private var appointmentIdToUpdate: String? = null
    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var loadingDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_testing_appointment)
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
        tvUserId = findViewById(R.id.tv_user_id)
        imgAvatar = findViewById(R.id.avatar)
        selectDate = findViewById(R.id.btn_select_date)
        selectTime = findViewById(R.id.btn_select_time)
        tvTitle = findViewById(R.id.title)
        btnConfirm = findViewById(R.id.btn_confirm)
        etNote = findViewById(R.id.et_note)
    }

    private fun init() {
        userId = intent.getStringExtra("USER_ID")
        senderId = intent.getStringExtra("SENDER_ID")
        appointmentType = intent.getStringExtra("APPOINTMENT_TYPE")
        status = intent.getStringExtra("status")
        appointmentIdToUpdate = intent.getStringExtra("appointmentId")

        loadingDialog = ProgressDialog(this)
        loadingDialog.setMessage("Loading...")
        loadingDialog.setCancelable(false)

        appointmentId = generateAppointmentId()

        appointmentViewModel = ViewModelProvider(this)[AppointmentViewModel::class.java]
    }

    @SuppressLint("SetTextI18n")
    private fun afterInit() {
        tvUserId.text = userId

        if(appointmentType == Constants.COUNSELING_TAG) {
            tvTitle.text = "Set Counseling Date"
        } else {
            tvTitle.text = "Set Testing Date"
        }

        btnBack.setOnClickListener {
            finish()
        }

        getAvatar(userId!!, imgAvatar)

        selectDate.setOnClickListener {
            Utility.showDatePicker(this) { selectedDate ->
                // Handle the selected date
                selectDate.text = selectedDate
            }
        }

        selectTime.setOnClickListener {
            Utility.showTimePicker(this) { selectedTime ->
                // Handle the selected time
                selectTime.text = selectedTime
            }
        }

        if(status == Constants.STATUS.UPDATE) {
            Utils.getSingleAppointmentById(appointmentIdToUpdate!!, object : IAppointment.GetSingle {
                @SuppressLint("SetTextI18n")
                override fun appointment(success: Boolean, appointment: AppointmentModel?) {
                    if(success) {
                        val formattedDate = appointment?.dateOfAppointment?.let { Utility.formatDateToDateString(it) }.toString()
                        val formattedTime = appointment?.timeOfAppointment?.let { Utility.formatDateTo12Hour(it) }.toString()
                        selectDate.text = formattedDate
                        selectTime.text = formattedTime

                        etNote.setText(appointment?.note)

                        btnConfirm.text = "Update"

                        btnConfirm.setOnClickListener {
                            loadingDialog.show()
                            val date = selectDate.text.toString()
                            val time = selectTime.text.toString()
                            val note = etNote.text.toString()

                            if(date.isEmpty() || date == "Select Date" || time.isEmpty() || time == "Select Time" || note.isEmpty()) {
                                loadingDialog.dismiss()
                                Utility.showAlertDialog(
                                    this@Appointment,
                                    layoutInflater,
                                    "Warning",
                                    "Please fill in all fields",
                                    "Ok"
                                ) {}
                            } else {
                                val data = mapOf<String, Any>(
                                    "id" to appointmentIdToUpdate!!,
                                    "userId" to userId!!,
                                    "senderId" to senderId!!,
                                    "type" to appointmentType!!,
                                    "dateOfAppointment" to Utility.parseDateStringToDate(date)!!,
                                    "timeOfAppointment" to Utility.parseTimeToDate(time)!!,
                                    "status" to "Send",
                                    "note" to note,
                                    "read" to false,
                                    "createdAt" to Utility.getCurrentDate()
                                )

                                appointmentViewModel.updateAppointment(appointmentIdToUpdate!!, data)
                            }
                        }
                    }
                }

                override fun onError(error: String) {
                    Log.d("", error)
                }
            })
        } else {
            btnConfirm.setOnClickListener {
                loadingDialog.show()
                val date = selectDate.text.toString()
                val time = selectTime.text.toString()
                val note = etNote.text.toString()

                if(date.isEmpty() || date == "Select Date" || time.isEmpty() || time == "Select Time" || note.isEmpty()) {
                    loadingDialog.dismiss()
                    Utility.showAlertDialog(
                        this,
                        layoutInflater,
                        "Warning",
                        "Please fill in all fields",
                        "Ok"
                    ) {}
                } else {
                    val data = AppointmentModel(
                        appointmentId,
                        userId,
                        senderId,
                        appointmentType,
                        Utility.parseDateStringToDate(date),
                        Utility.parseTimeToDate(time),
                        "Send",
                        note,
                        false,
                        Utility.getCurrentDate()
                    )

                    Utils.getUser(senderId!!) { sender ->
                        when(sender) {
                            is StaffModel -> {
                                Utils.sendNotification(
                                    userId!!,
                                    "Appointment",
                                    "${sender.displayName} (Staff) scheduled you an ${data.type} appointment.",
                                    "appointment",
                                    this
                                )
                            }
                            is Users -> {
                                Utils.sendNotification(
                                    userId!!,
                                    "Appointment",
                                    "${sender.displayName} scheduled you an ${data.type} appointment.",
                                    "appointment",
                                    this
                                )
                            }
                        }
                    }

                    appointmentViewModel.setAppointment(data)
                }
            }
        }

        observer()

    }

    private fun observer() {
        appointmentViewModel.isAppointmentSuccess.observe(this) { result ->
            result.onSuccess { pair ->
                val isSuccess = pair.first
                val message = pair.second

                if(isSuccess) {
                    loadingDialog.dismiss()
                    Utility.showAlertDialog(
                        this,
                        layoutInflater,
                        "Success",
                        message,
                        "Ok"
                    ) {finish()}
                } else {
                    loadingDialog.dismiss()
                    Utility.showAlertDialog(
                        this,
                        layoutInflater,
                        "Failed",
                        message,
                        "Ok"
                    ) {}
                }
            }
            result.onFailure { e ->
                loadingDialog.dismiss()
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Error",
                    e.message!!,
                    "Ok"
                ) {}
            }
        }

        appointmentViewModel.isAppointmentUpdated.observe(this) { result ->
            result.onSuccess { isSuccess ->
                if(isSuccess) {
                    loadingDialog.dismiss()

                    Utils.getUser(senderId!!) { sender ->
                        when(sender) {
                            is StaffModel -> {
                                Utils.sendNotification(
                                    userId!!,
                                    "Appointment",
                                    "${sender.displayName} (Staff) updated your schedule.",
                                    "appointment",
                                    this@Appointment
                                )
                            }
                            is Users -> {
                                Utils.sendNotification(
                                    userId!!,
                                    "Appointment",
                                    "${sender.displayName} updated your schedule",
                                    "appointment",
                                    this@Appointment
                                )
                            }
                        }
                    }

                    Utility.showAlertDialog(
                        this,
                        layoutInflater,
                        "Success",
                        "Schedule updated successfully.",
                        "Ok"
                    ) {finish()}
                } else {
                    loadingDialog.dismiss()
                    Utility.showAlertDialog(
                        this,
                        layoutInflater,
                        "Failed",
                        "Failed to update schedule.",
                        "Ok"
                    ) {}
                }
            }
            result.onFailure { e ->
                loadingDialog.dismiss()
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Error",
                    e.message!!,
                    "Ok"
                ) {}
            }
        }
    }
}