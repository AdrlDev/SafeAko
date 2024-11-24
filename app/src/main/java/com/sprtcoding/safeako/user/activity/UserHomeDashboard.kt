package com.sprtcoding.safeako.user.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.user.fragment.AssessmentFragment
import com.sprtcoding.safeako.user.fragment.HomeFragment
import com.sprtcoding.safeako.user.fragment.MessageFragment
import com.sprtcoding.safeako.user.fragment.NewAssessmentFragment
import com.sprtcoding.safeako.utils.Utility.replaceFragment

class UserHomeDashboard : AppCompatActivity() {
    private lateinit var frameLayout: FrameLayout
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var homeFragment: HomeFragment
    private lateinit var assessmentFragment: NewAssessmentFragment
    private lateinit var messageFragment: MessageFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_home_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        init()
        afterInit()
    }

    private fun initViews() {
        frameLayout = findViewById(R.id.frame_layout)
        bottomNav = findViewById(R.id.bottom_nav)
    }

    private fun init() {
        // Create a Bundle to pass the string
        val userId = intent.getStringExtra("userId")

        val bundle = Bundle()
        bundle.putString("userId", userId)

        assessmentFragment = NewAssessmentFragment()
        messageFragment = MessageFragment()
        homeFragment = HomeFragment()
        homeFragment.arguments = bundle
        messageFragment.arguments = bundle
        assessmentFragment.arguments = bundle

        replaceFragment(homeFragment, supportFragmentManager)
    }

    private fun afterInit() {
        bottomNav.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.home -> replaceFragment(homeFragment, supportFragmentManager)
                R.id.assessment -> replaceFragment(assessmentFragment, supportFragmentManager)
                R.id.message -> replaceFragment(messageFragment, supportFragmentManager)
            }
            true
        }
    }
}