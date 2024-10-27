package com.sprtcoding.safeako.admin.staff

import android.os.Bundle
import android.util.Log
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
import com.sprtcoding.safeako.admin.staff.adapter.StaffAdapter
import com.sprtcoding.safeako.admin.staff.viewmodel.StaffViewModel

class MyStaff : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var noDataImg: ImageView
    private lateinit var rvStaff: RecyclerView
    private lateinit var staffViewModel: StaffViewModel
    private var myId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_staff)
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
        noDataImg = findViewById(R.id.no_data)
        rvStaff = findViewById(R.id.rv_staff)
    }

    private fun init() {
        myId = intent.getStringExtra("UID")

        rvStaff.layoutManager = LinearLayoutManager(this)

        staffViewModel = ViewModelProvider(this)[StaffViewModel::class.java]

        staffViewModel.retrieveStaff(myId!!)
    }

    private fun afterInit() {
        staffViewModel.getStaff.observe(this) { result ->
            result.onSuccess { staffList ->
                if(staffList?.isNotEmpty() == true || staffList != null) {
                    Log.d("STAFF", "NOT_EMPTY")
                    noDataImg.visibility = View.GONE
                    rvStaff.visibility = View.VISIBLE

                    val staffAdapter = StaffAdapter(staffList, this)
                    rvStaff.adapter = staffAdapter
                } else {
                    Log.d("STAFF", "EMPTY")
                    noDataImg.visibility = View.VISIBLE
                    rvStaff.visibility = View.GONE
                }
            }
            result.onFailure { err ->
                Log.d("STAFF", err.message!!)
                noDataImg.visibility = View.VISIBLE
                rvStaff.visibility = View.GONE
            }
        }

        btnBack.setOnClickListener { finish() }
    }
}