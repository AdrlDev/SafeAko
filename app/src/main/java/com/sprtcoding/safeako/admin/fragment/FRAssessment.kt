package com.sprtcoding.safeako.admin.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.admin.assessment.adapter.AssessmentAdapter
import com.sprtcoding.safeako.user.fragment.viewmodel.AssessmentViewModel
import com.sprtcoding.safeako.utils.Utility

class FRAssessment : Fragment() {
    private lateinit var view: View
    private lateinit var rvAssessmentRequest: RecyclerView
    private lateinit var assessmentAdapter: AssessmentAdapter
    private lateinit var assessmentViewModel: AssessmentViewModel
    private lateinit var noData: ImageView
    private var myId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_assessment_admin, container, false)

        initViews()
        init()
        afterInit()

        return view
    }

    private fun initViews() {
        rvAssessmentRequest = view.findViewById(R.id.rv_assessment_request)
        noData = view.findViewById(R.id.no_data)
    }

    private fun init() {
        // Retrieve the arguments in onCreateView if needed
        arguments?.let {
            myId = it.getString("userId")
        }

        val viewManager = LinearLayoutManager(requireContext())
        rvAssessmentRequest.layoutManager = viewManager
        assessmentViewModel = ViewModelProvider(this)[AssessmentViewModel::class.java]
        assessmentAdapter = AssessmentAdapter(arrayListOf(), requireContext(), myId!!)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun afterInit() {
        assessmentViewModel.getAllAssessment(myId!!)

        assessmentViewModel.assessmentList.observe(viewLifecycleOwner) { result ->
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
                    requireContext(),
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
    }

}