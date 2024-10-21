package com.sprtcoding.safeako.admin.appointment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.admin.appointment.adapter.AppointmentAdapter
import com.sprtcoding.safeako.admin.appointment.viewmodel.AppointmentViewModel
import com.sprtcoding.safeako.utils.Utility

class ViewAppointment : AppCompatActivity() {
    private lateinit var appointmentAdapter: AppointmentAdapter
    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var btnBack: ImageButton
    private lateinit var rvAppointmentRequest: RecyclerView
    private lateinit var noData: ImageView
    private lateinit var tvTitle: TextView
    private var type: String? = null
    private var myId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_appointment)
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
        rvAppointmentRequest = findViewById(R.id.rv_appointment_request)
        tvTitle = findViewById(R.id.tv_title)
        noData = findViewById(R.id.no_data)
    }

    private fun init() {
        type = intent.getStringExtra("TYPE")
        myId = intent.getStringExtra("UID")
        tvTitle.text = type

        appointmentViewModel = ViewModelProvider(this)[AppointmentViewModel::class.java]

        val viewManager = LinearLayoutManager(this)
        rvAppointmentRequest.layoutManager = viewManager
        appointmentAdapter = AppointmentAdapter(arrayListOf(), this)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun afterInit() {
        appointmentViewModel.getAppointmentByType(myId!!, type!!)

        appointmentViewModel.appointmentList.observe(this) { result ->
            result.onSuccess { appointmentList ->
                if(appointmentList != null) {
                    appointmentAdapter.arrayList.clear()
                    appointmentAdapter.arrayList.addAll(appointmentList)
                    appointmentAdapter.notifyDataSetChanged()
                    // Scroll to the top when a new message is added
                    rvAppointmentRequest.scrollToPosition(0)
                    rvAppointmentRequest.adapter = appointmentAdapter
                    noData.visibility = View.GONE
                    rvAppointmentRequest.visibility = View.VISIBLE
                } else {
                    noData.visibility = View.VISIBLE
                    rvAppointmentRequest.visibility = View.GONE
                }
            }
            result.onFailure { e ->
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Appointment",
                    e.message!!,
                    "Ok"
                ) {
                    noData.visibility = View.VISIBLE
                    rvAppointmentRequest.visibility = View.GONE
                }
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}