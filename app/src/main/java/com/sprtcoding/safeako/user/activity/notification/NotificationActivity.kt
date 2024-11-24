package com.sprtcoding.safeako.user.activity.notification

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.user.activity.notification.adapter.NotificationAdapter
import com.sprtcoding.safeako.user.activity.notification.viewmodel.UserAppointmentViewModel
import com.sprtcoding.safeako.utils.Utility

class NotificationActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var noData: ImageView
    private lateinit var rvNotification: RecyclerView
    private lateinit var userAppointmentViewModel: UserAppointmentViewModel
    private lateinit var notificationAdapter: NotificationAdapter
    private var myId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notification)
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
        btnBack = findViewById(R.id.btn_back)
        noData = findViewById(R.id.no_data)
        rvNotification = findViewById(R.id.rv_notification)
    }

    private fun init() {
        myId = intent.getStringExtra("UID")

        userAppointmentViewModel = ViewModelProvider(this)[UserAppointmentViewModel::class.java]

        val viewManager = LinearLayoutManager(this)
        rvNotification.layoutManager = viewManager
        notificationAdapter = NotificationAdapter(arrayListOf(), userAppointmentViewModel, this)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun afterInit() {
        userAppointmentViewModel.getUserAppointment(myId!!)

        userAppointmentViewModel.appointment.observe(this) { result ->
            result.onSuccess { appointment ->
                if(appointment != null) {
                    notificationAdapter.arrayList.clear()
                    notificationAdapter.arrayList.addAll(appointment)
                    notificationAdapter.notifyDataSetChanged()
                    // Scroll to the top when a new message is added
                    rvNotification.scrollToPosition(0)
                    rvNotification.adapter = notificationAdapter
                    noData.visibility = View.GONE
                    rvNotification.visibility = View.VISIBLE
                } else {
                    noData.visibility = View.VISIBLE
                    rvNotification.visibility = View.GONE
                }
            }
            result.onFailure { err ->
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Notification",
                    err.message!!,
                    "Ok"
                ) {}
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}