package com.sprtcoding.safeako.welcome_page

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager.widget.ViewPager
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE
import com.sprtcoding.safeako.utils.Constants.NOTIFICATION_PERMISSION_REQUEST_CODE
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.welcome_page.adapter.ViewPagerAdapter

class WelcomeActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var nextBtn: ImageView
    private lateinit var tvPrev: TextView
    private lateinit var tvSkip: TextView
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var dots: Array<TextView>
    private lateinit var mDotLayout: LinearLayout

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("Notification Permission", "Permission granted.")
        } else {
            // TODO: Inform user that that your app will not show notifications.
            Utility.showAlertDialog(
                this,
                layoutInflater,
                "Notification Permission",
                "Permission denied. You cannot received notification.",
                "Ok"
            ){}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()
        initViews()
        afterInit()
        askNotificationPermission()
    }

    private fun init() {
        viewPagerAdapter = ViewPagerAdapter(this)
    }

    private fun initViews() {
        viewPager = findViewById(R.id.my_viewpager)
        nextBtn = findViewById(R.id.next_btn)
        tvPrev = findViewById(R.id.tv_prev)
        tvSkip = findViewById(R.id.tv_skip)
        mDotLayout = findViewById(R.id.indicator_ll)
    }

    private fun afterInit() {
        viewPager.adapter = viewPagerAdapter
        setIndicator(0)
        viewPager.addOnPageChangeListener(viewListener)

        setClickListener()
    }

    private fun setIndicator(position: Int) {
        dots = Array(3) { TextView(this) }
        mDotLayout.removeAllViews()

        for (i in dots.indices) {
            dots[i].text = Html.fromHtml("&#8226")
            dots[i].textSize = 35f
            dots[i].setTextColor(resources.getColor(R.color.dark_text, applicationContext.theme))
            mDotLayout.addView(dots[i])
        }

        dots[position].setTextColor(resources.getColor(R.color.g1, applicationContext.theme))

        if (position > 0) {
            tvPrev.visibility = View.VISIBLE
        } else {
            tvPrev.visibility = View.GONE
        }
    }

    private val viewListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {

        }

        override fun onPageSelected(position: Int) {
            setIndicator(position)

            if (position > 0) {
                tvPrev.visibility = View.VISIBLE
            } else {
                tvPrev.visibility = View.GONE
            }

            // Update nextBtn image
            when (position) {
                0 -> {
                    nextBtn.setImageResource(R.drawable.next_btn_img) // Set "done" image
                }
                1 -> {
                    nextBtn.setImageResource(R.drawable.next_btn_img2)
                }
                else -> {
                    nextBtn.setImageResource(R.drawable.next_btn_img3) // Set "next" image
                }
            }
        }

        override fun onPageScrollStateChanged(state: Int) {

        }

    }

    private fun getItem(i: Int): Int {
        return viewPager.currentItem + i
    }

    private fun setClickListener() {
        tvPrev.setOnClickListener {
            if (getItem(0) > 0) {
                viewPager.setCurrentItem(getItem(-1), true)
            }
        }

        nextBtn.setOnClickListener {
            // Apply cross fade animation
            val fade = AnimationUtils.loadAnimation(applicationContext, R.anim.crossfade)
            nextBtn.startAnimation(fade)

            if (getItem(0) < 2) {
                viewPager.setCurrentItem(getItem(1), true)
            } else {
                welcomeLogin()
            }
        }

        tvSkip.setOnClickListener {
            welcomeLogin()
        }
    }

    private fun welcomeLogin() {
        startActivity(Intent(this, WelcomeLoginActivity::class.java))
        finish()
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            checkOtherPermissions()
        }
    }

    private fun checkOtherPermissions() {
        // Example: Check for location permission on lower API levels
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
        // Add other permission checks as needed
    }
}