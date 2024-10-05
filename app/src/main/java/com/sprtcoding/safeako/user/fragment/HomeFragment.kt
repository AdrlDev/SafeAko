package com.sprtcoding.safeako.user.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.authentication.login.LoginActivity
import com.sprtcoding.safeako.firebaseUtils.Utils
import com.sprtcoding.safeako.user.activity.VideoPlayerActivity
import com.sprtcoding.safeako.user.activity.sti.StiActivity
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.Utility.animateCardView
import com.sprtcoding.safeako.utils.Utility.generateExoPlayerThumbnail
import com.sprtcoding.safeako.utils.Utility.getAvatar
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private lateinit var view: View
    private lateinit var tvUserId: TextView
    private lateinit var btnProfile: MaterialCardView
    private lateinit var videoThumbnail: ImageView
    private lateinit var stiBtn: CardView
    private lateinit var avatar: CircleImageView
    private lateinit var loading: ProgressDialog
    private lateinit var userId: String
    private lateinit var tvViewAll: TextView
    private lateinit var profileSettingCard: CardView
    private lateinit var btnLogOut: RelativeLayout
    private lateinit var tvNameSetting: TextView

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
    }

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

        getAvatar(userId, avatar)
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

        val videoPath = "android.resource://" + requireContext().packageName + "/" + R.raw.ph1_stds
        val videoUri = Uri.parse(videoPath)

        lifecycleScope.launch {
            val thumbnail = loadThumbnailAsync(videoUri)
            videoThumbnail.setImageBitmap(thumbnail)
        }

        videoThumbnail.setOnClickListener {
            val intent = Intent(requireContext(), VideoPlayerActivity::class.java)
            intent.putExtra("VIDEO_RES_ID", R.raw.ph1_stds)
            startActivity(intent)
        }

        tvViewAll.setOnClickListener {
            startActivity(Intent(requireContext(), StiActivity::class.java))
        }

        stiBtn.setOnClickListener {
            startActivity(Intent(requireContext(), StiActivity::class.java))
        }

        btnLogOut.setOnClickListener { signOut() }
    }

    // Function to generate the thumbnail on a background thread
    private suspend fun loadThumbnailAsync(videoUri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        generateExoPlayerThumbnail(videoUri, requireContext())
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