package com.sprtcoding.safeako.admin.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.cardview.widget.CardView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.card.MaterialCardView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.admin.assessment.AssessmentActivity
import com.sprtcoding.safeako.admin.appointment.ViewAppointment
import com.sprtcoding.safeako.admin.appointment.contract.IAppointment
import com.sprtcoding.safeako.authentication.login.LoginActivity
import com.sprtcoding.safeako.firebaseUtils.Utils
import com.sprtcoding.safeako.user.fragment.contract.IAssessment
import com.sprtcoding.safeako.utils.Constants
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.Utility.animateCardView

class MainFragment : Fragment() {
    private lateinit var view: View
    private lateinit var barChart: BarChart
    private lateinit var entries: ArrayList<BarEntry>
    private lateinit var btnProfile: MaterialCardView
    private lateinit var cardAssessment: CardView
    private lateinit var cardCounseling: CardView
    private lateinit var cardTesting: CardView
    private lateinit var tvViewAllAssessment: TextView
    private lateinit var tvCountAssessment: TextView
    private lateinit var tvCountCounseling: TextView
    private lateinit var tvCountTesting: TextView
    private lateinit var badgeDrawable: BadgeDrawable
    private lateinit var mainFrameL: FrameLayout
    private lateinit var notificationIcon: ImageView
    private lateinit var profileSettingCard: CardView
    private lateinit var btnLogOut: RelativeLayout
    private lateinit var tvNameSetting: TextView
    private lateinit var loading: ProgressDialog
    private var myId: String? = null
    private var pendingOperations = 3 // You have 3 async calls

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false)

        initViews()
        init()
        afterInit()

        return view
    }

    private fun initViews() {
        barChart = view.findViewById(R.id.bar_chart)
        btnProfile = view.findViewById(R.id.btn_profile)
        cardAssessment = view.findViewById(R.id.card_assessment)
        tvViewAllAssessment = view.findViewById(R.id.tv_view_all_assessment)
        tvCountAssessment = view.findViewById(R.id.tv_count_assessment)
        mainFrameL = view.findViewById(R.id.main_fl)
        notificationIcon = view.findViewById(R.id.notification_icon)
        profileSettingCard = view.findViewById(R.id.profile_settings_card)
        btnLogOut = view.findViewById(R.id.btn_log_out)
        tvNameSetting = view.findViewById(R.id.tv_name_settings)
        tvCountCounseling = view.findViewById(R.id.tv_count_counseling)
        tvCountTesting = view.findViewById(R.id.tv_count_testing)
        cardCounseling = view.findViewById(R.id.card_counseling)
        cardTesting = view.findViewById(R.id.card_testing)
    }

    @OptIn(ExperimentalBadgeUtils::class)
    private fun init() {
        myId = arguments?.getString("userId")

        loading = ProgressDialog(context)
        loading.setMessage("Please wait...")

        entries = ArrayList()

        // Initialize BadgeDrawable
        badgeDrawable = BadgeDrawable.create(requireContext())

        // Attach the BadgeDrawable to the FrameLayout (ImageView)
        BadgeUtils.attachBadgeDrawable(badgeDrawable, notificationIcon, mainFrameL)
    }

    private fun afterInit() {

        Utils.getUsers(myId!!) { success, user, _ ->
            if(success) {
                tvNameSetting.text = user?.fullName
            }
        }

        getAllData()

        Utils.getAssessmentRequestCountStatus(object : IAssessment.GetCount {
            override fun count(count: Int) {
                if(count != 0) {
                    Utility.showNotificationDot(true, count.toLong(), badgeDrawable)
                } else {
                    Utility.showNotificationDot(false, 0, badgeDrawable)
                }
            }
        })

        btnProfile.setOnClickListener {
            if (profileSettingCard.visibility == View.GONE) {
                animateCardView(true, profileSettingCard)
            } else {
                animateCardView(false, profileSettingCard)
            }
        }

        btnLogOut.setOnClickListener { signOut() }

        cardAssessment.setOnClickListener {
            startActivity(Intent(requireContext(), AssessmentActivity::class.java).putExtra("MY_ID", myId))
        }

        tvViewAllAssessment.setOnClickListener {
            startActivity(Intent(requireContext(), AssessmentActivity::class.java).putExtra("MY_ID", myId))
        }

        notificationIcon.setOnClickListener {
            startActivity(Intent(requireContext(), AssessmentActivity::class.java).putExtra("MY_ID", myId))
        }

        cardCounseling.setOnClickListener {
            startActivity(Intent(requireContext(), ViewAppointment::class.java).putExtra("TYPE", Constants.COUNSELING_TAG))
        }

        cardTesting.setOnClickListener {
            startActivity(Intent(requireContext(), ViewAppointment::class.java).putExtra("TYPE", Constants.TESTING_TAG))
        }
    }

    private fun getAllData() {
        Utils.getAssessmentRequestCount(object : IAssessment.GetCount {
            override fun count(count: Int) {
                tvCountAssessment.text = count.toString()
                entries.add(BarEntry(1f, count.toFloat()))
                updateChartIfReady()
            }
        })

        Utils.getAppointmentCountByType(Constants.COUNSELING_TAG, object : IAppointment.GetCount {
            @SuppressLint("SetTextI18n")
            override fun count(count: Int) {
                tvCountCounseling.text = "$count"
                entries.add(BarEntry(2f, count.toFloat()))
                updateChartIfReady()
            }
        })

        Utils.getAppointmentCountByType(Constants.TESTING_TAG, object : IAppointment.GetCount {
            @SuppressLint("SetTextI18n")
            override fun count(count: Int) {
                tvCountTesting.text = "$count"
                entries.add(BarEntry(3f, count.toFloat()))
                updateChartIfReady()
            }
        })
    }

    private fun signOut() {
        Utility.showAlertDialogWithYesNo(
            requireContext(),
            layoutInflater,
            "Are you sure you want to logout?",
            "No",
            "Yes",
            null
        ) {
            loading.show()

            Handler(Looper.getMainLooper()).postDelayed({
                context?.let { it1 ->
                    Utils.logout({ success, message ->
                        if (success) {
                            loading.dismiss()
                            startActivity(Intent(it1, LoginActivity::class.java))
                            activity?.finish()
                        } else {
                            loading.dismiss()
                            Utility.showAlertDialog(
                                requireContext(),
                                layoutInflater,
                                "Log Out Failed",
                                message,
                                "Ok"
                            ) {}
                        }
                    }, it1)
                }
            }, 2000)
        }
    }

    private fun updateChartIfReady() {
        pendingOperations--
        if (pendingOperations == 0) {
            // All operations are done, update the chart here
            updateBarChart()
        }
    }

    private fun updateBarChart() {
        if (entries.isEmpty()) {
            Log.e("BarChart", "Entries list is empty")
            return
        }

        val dataSet = BarDataSet(entries, "Appointments")

        val colors = ArrayList<Int>()
        for (i in entries.indices) {
            Log.e("INDEX", i.toString())
            when (i) {
                0 -> colors.add(Color.parseColor("#E48898")) // Pink for Assessment
                1 -> colors.add(Color.parseColor("#88DEE4")) // Cyan for Counseling
                2 -> colors.add(Color.parseColor("#E4DB88")) // Yellow for Testing
            }
        }

        dataSet.colors = colors
        dataSet.valueTextColor = Color.BLACK

        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.setFitBars(true)

        // X-axis label customization
        val xAxis = barChart.xAxis
        val labels = arrayOf("Assessment", "Counseling", "Testing")
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.granularity = 1f  // Make sure the granularity is 1, to show each label
        xAxis.setCenterAxisLabels(true) // Align labels properly with bars
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        barChart.invalidate()
        barChart.animateY(1000)
    }

}