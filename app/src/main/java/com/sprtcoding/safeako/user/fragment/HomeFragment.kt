package com.sprtcoding.safeako.user.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.lifecycle.lifecycleScope
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.card.MaterialCardView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.utils.profile_settings.ProfileSettings
import com.sprtcoding.safeako.api.google_docs_api.AuthTokenManager
import com.sprtcoding.safeako.api.google_docs_api.MetadataCallback
import com.sprtcoding.safeako.authentication.login.LoginActivity
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.user.activity.Acknowledgement
import com.sprtcoding.safeako.user.activity.VideoPlayerActivity
import com.sprtcoding.safeako.user.activity.about.AboutUs
import com.sprtcoding.safeako.user.activity.all_vid.ViewAllVideo
import com.sprtcoding.safeako.user.activity.contact.ContactFacility
import com.sprtcoding.safeako.user.activity.impact_awareness.ImpactAwareness
import com.sprtcoding.safeako.user.activity.notification.NotificationActivity
import com.sprtcoding.safeako.user.activity.notification.viewmodel.UserAppointmentViewModel
import com.sprtcoding.safeako.user.activity.prevention.PreventionMethodActivity
import com.sprtcoding.safeako.user.activity.sti.StiActivity
import com.sprtcoding.safeako.user.fragment.viewmodel.AssessmentViewModel
import com.sprtcoding.safeako.utils.Constants
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.Utility.animateCardView
import com.sprtcoding.safeako.utils.Utility.getAvatar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeFragment : Fragment() {
    private lateinit var view: View
    private lateinit var tvUserId: TextView
    private lateinit var btnProfile: MaterialCardView
    private lateinit var videoThumbnail: ImageView
    private lateinit var notification: ImageView
    private lateinit var stiBtn: CardView
    private lateinit var preventionCard: CardView
    private lateinit var impactAwarenessCard: CardView
    private lateinit var contactCard: CardView
    private lateinit var avatar: CircleImageView
    private lateinit var loading: ProgressDialog
    private lateinit var userId: String
    private lateinit var tvViewAll: TextView
    private lateinit var profileSettingCard: CardView
    private lateinit var btnLogOut: RelativeLayout
    private lateinit var btnProfileSetting: RelativeLayout
    private lateinit var btnAcknowledgement: RelativeLayout
    private lateinit var btnAbout: RelativeLayout
    private lateinit var tvNameSetting: TextView
    private lateinit var shimmerContainer: ShimmerFrameLayout
    private lateinit var assessmentViewModel: AssessmentViewModel
    private lateinit var userAppointmentViewModel: UserAppointmentViewModel
    private lateinit var mainFrameL: FrameLayout
    private lateinit var badgeDrawable: BadgeDrawable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false)

        initViews()
        init()
        afterInit()

        return view
    }

    private fun initViews() {
        tvUserId = view.findViewById(R.id.tv_user_id)
        btnProfile = view.findViewById(R.id.btn_profile)
        avatar = view.findViewById(R.id.avatar)
        videoThumbnail = view.findViewById(R.id.videoThumbnail)
        stiBtn = view.findViewById(R.id.sti_card)
        tvViewAll = view.findViewById(R.id.tv_view_all_vid)
        profileSettingCard = view.findViewById(R.id.profile_settings_card)
        btnLogOut = view.findViewById(R.id.btn_log_out)
        tvNameSetting = view.findViewById(R.id.tv_name_settings)
        preventionCard = view.findViewById(R.id.prevention_card)
        impactAwarenessCard = view.findViewById(R.id.testing_card)
        shimmerContainer = view.findViewById(R.id.shimmer_home)
        btnProfileSetting = view.findViewById(R.id.btn_profile_setting)
        contactCard = view.findViewById(R.id.contact_card)
        notification = view.findViewById(R.id.notification_icon)
        mainFrameL = view.findViewById(R.id.main_frame)
        btnAcknowledgement = view.findViewById(R.id.btn_acknowledge)
        btnAbout = view.findViewById(R.id.btn_about_us)
    }

    @OptIn(ExperimentalBadgeUtils::class)
    @SuppressLint("SetTextI18n")
    private fun init() {
        loading = ProgressDialog(context)
        loading.setMessage("Please wait...")

        // Retrieve the arguments in onCreateView if needed
        arguments?.let {
            userId = it.getString("userId").toString()
            // Use the string as needed
            tvUserId.text = "Welcome, \n$userId"
            tvNameSetting.text = userId
        }

        // Initialize BadgeDrawable
        badgeDrawable = BadgeDrawable.create(requireContext())

        // Attach the BadgeDrawable to the FrameLayout (ImageView)
        BadgeUtils.attachBadgeDrawable(badgeDrawable, notification, mainFrameL)

        assessmentViewModel = ViewModelProvider(this)[AssessmentViewModel::class.java]
        userAppointmentViewModel = ViewModelProvider(this)[UserAppointmentViewModel::class.java]

        assessmentViewModel.refreshToken(requireContext())

        assessmentViewModel.token.observe(viewLifecycleOwner) { result ->
            result.onSuccess { token ->
                AuthTokenManager.accessToken = token
            }
        }

        getAvatar(userId, avatar)

        userAppointmentViewModel.getUserAppointmentCount(userId)
    }

    @SuppressLint("SetTextI18n")
    private fun afterInit() {

        btnProfile.setOnClickListener {
            if (profileSettingCard.visibility == View.GONE) {
                animateCardView(true, profileSettingCard)
            } else {
                animateCardView(false, profileSettingCard)
            }
        }

        notification.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java)
                .putExtra("UID", userId))
        }

        var fileNames = ""

        lifecycleScope.launch {
            assessmentViewModel.getDriveFileMetadata(requireContext(), Constants.allVideoId[0], object :
                MetadataCallback {
                override fun onSuccess(thumbnailLink: String?, fileName: String?) {
                    // Check if thumbnailLink is not null before proceeding
                    if (!thumbnailLink.isNullOrEmpty()) {
                        // Switch to the main thread for UI operations
                        fileNames = fileName ?: ""

                        lifecycleScope.launch {
                            shimmerContainer.startShimmer()
                            videoThumbnail.scaleType = ImageView.ScaleType.CENTER_INSIDE

                            Picasso.get()
                                .load(thumbnailLink)
                                .placeholder(R.drawable.reel)
                                .into(videoThumbnail, object : Callback {
                                    override fun onSuccess() {
                                        shimmerContainer.stopShimmer()
                                        shimmerContainer.setShimmer(null)
                                        videoThumbnail.scaleType = ImageView.ScaleType.FIT_XY
                                    }

                                    override fun onError(e: Exception?) {
                                        shimmerContainer.stopShimmer()
                                        shimmerContainer.setShimmer(null)
                                        videoThumbnail.setImageResource(R.drawable.reel)
                                        videoThumbnail.scaleType = ImageView.ScaleType.CENTER_INSIDE
                                    }
                                })
                        }
                    } else {
                        // Handle the case where the thumbnail link is null or empty
                        println("Failed to retrieve a valid thumbnail link for video: ${Constants.allVideoId[0]}")
                        // Optionally set a default image or handle it accordingly
                        // Optionally set a default image or handle it accordingly
                        lifecycleScope.launch {
                            videoThumbnail.setImageResource(R.drawable.reel)
                            videoThumbnail.scaleType = ImageView.ScaleType.CENTER_INSIDE
                        }
                    }
                }

                override fun onError(errorMessage: String) {
                    // Handle the case where the thumbnail link is null or empty
                    println(errorMessage)
                }
            })
        }

        videoThumbnail.setOnClickListener {
            val intent = Intent(requireContext(), VideoPlayerActivity::class.java)
            intent.putExtra("VIDEO_RES_ID", Constants.allVideoId[0])
            intent.putExtra("FILENAME", fileNames)
            startActivity(intent)
        }

        tvViewAll.setOnClickListener {
            startActivity(Intent(requireContext(), ViewAllVideo::class.java))
        }

        stiBtn.setOnClickListener {
            startActivity(Intent(requireContext(), StiActivity::class.java))
        }

        preventionCard.setOnClickListener {
            startActivity(Intent(requireContext(), PreventionMethodActivity::class.java))
        }

        contactCard.setOnClickListener {
            startActivity(Intent(requireContext(), ContactFacility::class.java)
                .putExtra("UID", userId))
        }

        impactAwarenessCard.setOnClickListener {
            startActivity(Intent(requireContext(), ImpactAwareness::class.java))
        }

        btnProfileSetting.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileSettings::class.java)
                .putExtra("UID", userId))
        }

        btnAcknowledgement.setOnClickListener {
            startActivity(Intent(requireContext(), Acknowledgement::class.java))
        }

        btnAbout.setOnClickListener {
            startActivity(Intent(requireContext(), AboutUs::class.java))
        }

        btnLogOut.setOnClickListener { signOut() }

        observer()
    }

    private fun observer() {
        userAppointmentViewModel.appointmentCount.observe(viewLifecycleOwner) { count ->
            if(count != 0) {
                Utility.showNotificationDot(true, count.toLong(), badgeDrawable)
            } else {
                Utility.showNotificationDot(false, 0, badgeDrawable)
            }
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

}