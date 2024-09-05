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
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.card.MaterialCardView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.authentication.login.LoginActivity
import com.sprtcoding.safeako.firebaseUtils.Utils
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class HomeFragment : Fragment() {
    private lateinit var view: View
    private lateinit var tvUserId: TextView
    private lateinit var btnProfile: MaterialCardView
    private lateinit var avatar: CircleImageView
    private lateinit var loading: ProgressDialog
    private lateinit var userId: String

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
        }

        Utils.getUsers(userId) { success, user, message ->
            if (success) {
                if (user != null) {
                    Picasso.get()
                        .load(user.avatar)
                        .placeholder(R.drawable.avatar)
                        .fit()
                        .into(avatar)
                }
            } else {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun afterInit() {

        btnProfile.setOnClickListener {
            loading.show()

            Handler(Looper.getMainLooper()).postDelayed({
                context?.let { it1 -> Utils.logout({success, message ->
                    if (success) {
                        loading.dismiss()
                        Toast.makeText(it1, message, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(it1, LoginActivity::class.java))
                        activity?.finish()
                    } else {
                        loading.dismiss()
                        Toast.makeText(it1, message, Toast.LENGTH_SHORT).show()
                    }
                }, it1) }
            }, 2000)
        }
    }

}