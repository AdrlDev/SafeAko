package com.sprtcoding.safeako.admin.assessment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.admin.appointment.Appointment
import com.sprtcoding.safeako.admin.assessment.viewmodel.AdminAssessmentViewModel
import com.sprtcoding.safeako.utils.Constants
import com.sprtcoding.safeako.utils.Utility

class ViewAssessment : AppCompatActivity() {
    private lateinit var tvFileName: TextView
    private lateinit var btnViewDoc: MaterialButton
    private lateinit var btnSetAppointment: MaterialButton
    private lateinit var btnBack: ImageButton
    private lateinit var adminAssessmentViewModel: AdminAssessmentViewModel
    private var documentUrl: String? = null
    private var userId: String? = null
    private var senderId: String? = null
    private var assessmentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_assessment)
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
        tvFileName = findViewById(R.id.tv_file_name)
        btnViewDoc = findViewById(R.id.btn_view_doc)
        btnSetAppointment = findViewById(R.id.btn_set_appointment)
        btnBack = findViewById(R.id.btn_back)
    }

    private fun init() {
        documentUrl = intent.getStringExtra("DOCUMENT_URL")
        userId = intent.getStringExtra("USER_ID")
        senderId = intent.getStringExtra("SENDER_ID")
        assessmentId = intent.getStringExtra("ASSESSMENT_ID")

        adminAssessmentViewModel = ViewModelProvider(this)[AdminAssessmentViewModel::class.java]
    }

    @SuppressLint("SetTextI18n")
    private fun afterInit() {
        tvFileName.text = "$userId - Assessment"

        btnViewDoc.setOnClickListener {
            documentUrl?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
                adminAssessmentViewModel.updateAssessmentStatus(assessmentId!!, "Completed")
            }
        }

        btnSetAppointment.setOnClickListener {
            Utility.showAppointmentDialog(this, layoutInflater,
                {
                //testing btn click
                    val intent = Intent(this, Appointment::class.java)
                    intent.putExtra("USER_ID", userId)
                    intent.putExtra("APPOINTMENT_TYPE", Constants.TESTING_TAG)
                    intent.putExtra("SENDER_ID", senderId)
                    .putExtra("status", Constants.STATUS.SET)
                    startActivity(intent)
            }, {
                //counseling btn click
                    val intent = Intent(this, Appointment::class.java)
                    intent.putExtra("USER_ID", userId)
                    intent.putExtra("APPOINTMENT_TYPE", Constants.COUNSELING_TAG)
                    intent.putExtra("SENDER_ID", senderId)
                    .putExtra("status", Constants.STATUS.SET)
                    startActivity(intent)
            })
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}