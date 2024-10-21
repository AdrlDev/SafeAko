package com.sprtcoding.safeako.user.activity.admin_list

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.user.activity.admin_list.adapter.AdminListAdapter
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.activity.admin_list.viewmodel.AdminListViewModel
import com.sprtcoding.safeako.user.activity.chat_activity.ChatActivity

class AdminListActivity : AppCompatActivity(), IAdminListCallBack {
    private lateinit var btnBack: ImageView
    private lateinit var rvAdmin: RecyclerView
    private lateinit var noAdmin: LinearLayout
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var viewModel: AdminListViewModel
    private lateinit var adapter: AdminListAdapter
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_list)
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
        btnBack = findViewById(R.id.btn_back)
        rvAdmin = findViewById(R.id.rv_admins)
        noAdmin = findViewById(R.id.no_user_ll)
    }

    private fun init() {
        viewManager = LinearLayoutManager(this)
        rvAdmin.layoutManager = viewManager
        viewModel = ViewModelProvider(this)[AdminListViewModel::class.java]
        adapter = AdminListAdapter(this, arrayListOf(), this)

        userId = intent.getStringExtra("userId").toString()
    }

    private fun afterInit() {
        getAdmins()
        observeData()

        btnBack.setOnClickListener { finish() }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeData(){
        viewModel.liveUsers.observe(this) {
            Log.i("data", it.toString())
            adapter.userList.clear()
            adapter.userList.addAll(it)
            adapter.notifyDataSetChanged()
            // Scroll to the top when a new message is added
            rvAdmin.scrollToPosition(0)
            rvAdmin.adapter = adapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAdmins() {
        viewModel.getAdminUsers(this)
        rvAdmin.adapter?.notifyDataSetChanged()
    }

    override fun onSuccess() {
        // Fetch messages for a specific receiverId
        noAdmin.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        noAdmin.visibility = View.VISIBLE
    }

    override fun onError(error: String) {
        noAdmin.visibility = View.VISIBLE
    }

    override fun onClick(users: Users) {
        startActivity(Intent(this, ChatActivity::class.java)
            .putExtra("receiverId", users.userId)
            .putExtra("receiverName", users.displayName)
            .putExtra("userId", userId))
    }
}