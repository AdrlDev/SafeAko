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
import androidx.lifecycle.ViewModelProvider
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
import com.sprtcoding.safeako.admin.fragment.viewmodel.MainViewModel
import com.sprtcoding.safeako.utils.profile_settings.ProfileSettings
import com.sprtcoding.safeako.admin.staff.AddStaff
import com.sprtcoding.safeako.admin.staff.MyStaff
import com.sprtcoding.safeako.authentication.login.LoginActivity
import com.sprtcoding.safeako.firebaseUtils.Utils
import com.sprtcoding.safeako.model.StaffModel
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.fragment.contract.IAssessment
import com.sprtcoding.safeako.utils.Constants
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.Utility.animateCardView
import de.hdodenhof.circleimageview.CircleImageView

class MainFragment : Fragment() {
    private lateinit var view: View
    private lateinit var barChart: BarChart
    private lateinit var btnProfile: MaterialCardView
    private lateinit var avatar: CircleImageView
    private lateinit var cardAssessment: CardView
    private lateinit var cardCounseling: CardView
    private lateinit var cardTesting: CardView
    private lateinit var tvViewAllAssessment: TextView
    private lateinit var tvCountAssessment: TextView
    private lateinit var tvCountCounseling: TextView
    private lateinit var tvName: TextView
    private lateinit var tvCountTesting: TextView
    private lateinit var badgeDrawable: BadgeDrawable
    private lateinit var mainFrameL: FrameLayout
    private lateinit var notificationIcon: ImageView
    private lateinit var profileSettingCard: CardView
    private lateinit var btnLogOut: RelativeLayout
    private lateinit var btnAddStaff: RelativeLayout
    private lateinit var btnMyStaff: RelativeLayout
    private lateinit var btnProfileSetting: RelativeLayout
    private lateinit var tvNameSetting: TextView
    private lateinit var mainViewModel: MainViewModel
    private lateinit var lineStaff: View
    private lateinit var lineMyStaff: View
    private lateinit var loading: ProgressDialog
    private var myId: String? = null

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
        btnAddStaff = view.findViewById(R.id.btn_add_staff)
        btnMyStaff = view.findViewById(R.id.btn_my_staff)
        btnProfileSetting = view.findViewById(R.id.btn_profile_setting)
        avatar = view.findViewById(R.id.avatar)
        tvName = view.findViewById(R.id.tv_name)
        lineStaff = view.findViewById(R.id.line_staff)
        lineMyStaff = view.findViewById(R.id.line_my_staff)
    }

    @OptIn(ExperimentalBadgeUtils::class)
    private fun init() {
        myId = arguments?.getString("userId")

        loading = ProgressDialog(context)
        loading.setMessage("Please wait...")

        // Initialize BadgeDrawable
        badgeDrawable = BadgeDrawable.create(requireContext())

        // Attach the BadgeDrawable to the FrameLayout (ImageView)
        BadgeUtils.attachBadgeDrawable(badgeDrawable, notificationIcon, mainFrameL)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        getCount()
    }

    private fun getCount() {
        mainViewModel.getAssessment(myId!!)
        mainViewModel.getCounseling(myId!!)
        mainViewModel.getTesting(myId!!)
    }

    private fun afterInit() {

        Utils.getUsers(myId!!) { success, user, _ ->
            if(success) {
                when(user) {
                    is StaffModel -> {
                        tvNameSetting.text = user.fullName
                        tvName.text = user.fullName

                        lineStaff.visibility = View.GONE
                        lineMyStaff.visibility = View.GONE
                        btnAddStaff.visibility = View.GONE
                        btnMyStaff.visibility = View.GONE
                    }
                    is Users -> {
                        tvNameSetting.text = user.fullName
                        tvName.text = user.displayName

                        lineStaff.visibility = View.VISIBLE
                        lineMyStaff.visibility = View.VISIBLE
                        btnAddStaff.visibility = View.VISIBLE
                        btnMyStaff.visibility = View.VISIBLE
                    }
                }
            }
        }

        Utility.getAvatar(myId!!, avatar)

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
            startActivity(Intent(requireContext(), ViewAppointment::class.java)
                .putExtra("UID", myId)
                .putExtra("TYPE", Constants.COUNSELING_TAG))
        }

        cardTesting.setOnClickListener {
            startActivity(Intent(requireContext(), ViewAppointment::class.java)
                .putExtra("UID", myId)
                .putExtra("TYPE", Constants.TESTING_TAG))
        }

        btnAddStaff.setOnClickListener {
            startActivity(Intent(requireContext(), AddStaff::class.java).putExtra("UID", myId))
        }

        btnMyStaff.setOnClickListener {
            startActivity(Intent(requireContext(), MyStaff::class.java).putExtra("UID", myId))
        }

        btnProfileSetting.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileSettings::class.java).putExtra("UID", myId))
        }
    }

    override fun onResume() {
        super.onResume()
        getCount()
    }

    @SuppressLint("SetTextI18n")
    private fun getAllData() {
        val entries = mutableListOf<BarEntry>()

        mainViewModel.getAssessmentCount.observe(viewLifecycleOwner) { pair ->
            val assessmentCount = pair.first
            val entry = pair.second
            tvCountAssessment.text = assessmentCount.toString()
            entries.add(entry)
            updateChartIfReady(entries)
        }

        mainViewModel.getCounselingCount.observe(viewLifecycleOwner) { pair ->
            val counselingCount = pair.first
            val entry = pair.second
            tvCountCounseling.text = "$counselingCount"
            entries.add(entry)
            updateChartIfReady(entries)
        }

        mainViewModel.getTestingCount.observe(viewLifecycleOwner) { pair ->
            val testingCount = pair.first
            val entry = pair.second
            tvCountTesting.text = "$testingCount"
            entries.add(entry)
            updateChartIfReady(entries)
        }
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

    private fun updateChartIfReady(entries: List<BarEntry>) {
        val barDataSet = BarDataSet(entries, "Appointments")
        val barData = BarData(barDataSet)

        val colors = ArrayList<Int>()
        for (i in entries.indices) {
            Log.e("INDEX", i.toString())
            when (i) {
                0 -> colors.add(Color.parseColor("#E48898")) // Pink for Assessment
                1 -> colors.add(Color.parseColor("#88DEE4")) // Cyan for Counseling
                2 -> colors.add(Color.parseColor("#E4DB88")) // Yellow for Testing
            }
        }
        barDataSet.colors = colors
        barDataSet.valueTextColor = Color.BLACK
        // X-axis label customization
        val xAxis = barChart.xAxis
        val labels = arrayOf("Assessment", "Counseling", "Testing")
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.granularity = 1f  // Make sure the granularity is 1, to show each label
        xAxis.isGranularityEnabled = true
        xAxis.setDrawGridLines(false) // Remove vertical grid lines
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        // Enable value labels on the bars
        barDataSet.setDrawValues(true) // Draw values on the bars
        barDataSet.valueTextSize = 10f  // Optional: Customize the text size for the values

        // Customize Y-axis if needed (e.g., setting limits)
        barChart.axisLeft.axisMinimum = 0f // Start Y-axis from zero
        barChart.data = barData

        // Customize Left Y-axis
        val leftAxis = barChart.axisLeft
        leftAxis.setDrawGridLines(false) // Remove horizontal grid lines
        leftAxis.axisMinimum = 0f // Start Y-axis from zero

        // Customize Right Y-axis
        val rightAxis = barChart.axisRight
        rightAxis.setDrawGridLines(false) // Remove horizontal grid lines

        // Optionally remove chart description
        barChart.description.isEnabled = false

        barChart.invalidate() // Refresh the chart with new data
        barChart.animateY(1000)
    }

}