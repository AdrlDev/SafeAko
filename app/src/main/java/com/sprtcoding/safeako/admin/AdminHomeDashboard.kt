package com.sprtcoding.safeako.admin

import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.admin.fragment.MainFragment
import com.sprtcoding.safeako.user.fragment.MessageFragment

class AdminHomeDashboard : AppCompatActivity() {
    private lateinit var frameLayout: FrameLayout
    private lateinit var messageFragment: MessageFragment
    private lateinit var mainFragment: MainFragment
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_home_dashboard)
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

        messageFragment = MessageFragment()
        mainFragment = MainFragment()
        messageFragment.arguments = bundle

        replaceFragment(mainFragment)
    }

    private fun afterInit() {
        bottomNav.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.home -> replaceFragment(mainFragment)
                R.id.request -> Toast.makeText(this, "Request", Toast.LENGTH_SHORT).show()
                R.id.message -> replaceFragment(messageFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}