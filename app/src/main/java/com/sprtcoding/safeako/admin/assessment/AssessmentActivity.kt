package com.sprtcoding.safeako.admin.assessment

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
import com.sprtcoding.safeako.admin.assessment.adapter.AssessmentAdapter
import com.sprtcoding.safeako.user.fragment.viewmodel.AssessmentViewModel
import com.sprtcoding.safeako.utils.Utility

class AssessmentActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var rvAssessmentRequest: RecyclerView
    private lateinit var assessmentAdapter: AssessmentAdapter
    private lateinit var assessmentViewModel: AssessmentViewModel
    private lateinit var noData: ImageView
    private var myId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_assessment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left + 50, systemBars.top + 50, systemBars.right + 50, systemBars.bottom + 50)
            insets
        }

        initViews()
        init()
        afterInit()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun afterInit() {
        assessmentViewModel.getAllAssessment()

        assessmentViewModel.assessmentList.observe(this) { result ->
            result.onSuccess { list ->
                if(list != null) {
                    assessmentAdapter.arrayList.clear()
                    assessmentAdapter.arrayList.addAll(list)
                    assessmentAdapter.notifyDataSetChanged()
                    // Scroll to the top when a new message is added
                    rvAssessmentRequest.scrollToPosition(0)
                    rvAssessmentRequest.adapter = assessmentAdapter

                    rvAssessmentRequest.visibility = View.VISIBLE
                    noData.visibility = View.GONE
                } else {
                    rvAssessmentRequest.visibility = View.GONE
                    noData.visibility = View.VISIBLE
                }
            }
            result.onFailure { e ->
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Assessment",
                    e.message!!,
                    "Ok"
                ) {
                    rvAssessmentRequest.visibility = View.GONE
                    noData.visibility = View.VISIBLE
                }
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back)
        rvAssessmentRequest = findViewById(R.id.rv_assessment_request)
        noData = findViewById(R.id.no_data)
    }

    private fun init() {
        myId = intent.getStringExtra("MY_ID")

        val viewManager = LinearLayoutManager(this)
        rvAssessmentRequest.layoutManager = viewManager
        assessmentViewModel = ViewModelProvider(this)[AssessmentViewModel::class.java]
        assessmentAdapter = AssessmentAdapter(arrayListOf(), this, myId!!)
    }
}