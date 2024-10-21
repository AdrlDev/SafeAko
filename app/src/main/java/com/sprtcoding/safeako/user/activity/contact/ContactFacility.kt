package com.sprtcoding.safeako.user.activity.contact

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.model.StaffModel
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.activity.chat_activity.ChatActivity
import com.sprtcoding.safeako.user.activity.contact.adapter.ContactAdapter
import com.sprtcoding.safeako.user.activity.contact.contract.IContact
import com.sprtcoding.safeako.user.activity.contact.viewmodel.ContactViewModel
import com.sprtcoding.safeako.utils.Utility

class ContactFacility : AppCompatActivity(), IContact {
    private lateinit var rvContact: RecyclerView
    private lateinit var contactViewModel: ContactViewModel
    private lateinit var viewManager: LinearLayoutManager
    private var myId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contact_facility)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left + 60, systemBars.top + 60, systemBars.right + 60, systemBars.bottom + 60)
            insets
        }

        initViews()
        init()
    }

    private fun initViews() {
        rvContact = findViewById(R.id.rv_contact)
    }

    private fun init() {
        myId = intent.getStringExtra("UID")

        viewManager = LinearLayoutManager(this)
        rvContact.layoutManager = viewManager

        contactViewModel = ViewModelProvider(this)[ContactViewModel::class.java]

        contactViewModel.getMunicipality(myId!!)

        observer()
    }

    private fun observer() {
        contactViewModel.municipality.observe(this) { municipality ->
            contactViewModel.getContactFacility("RHU $municipality")
        }

        contactViewModel.contactFacility.observe(this) { result ->
            result.onSuccess { contacts ->
                if(contacts != null) {
                    val adapter = ContactAdapter(this, contacts, this)
                    rvContact.adapter = adapter
                }
            }
            result.onFailure { err ->
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Contact Facility",
                    err.message!!,
                    "OK"
                ){}
            }
        }
    }

    override fun onClick(users: Any) {
        when(users) {
            is StaffModel -> {
                startActivity(
                    Intent(this, ChatActivity::class.java)
                        .putExtra("receiverId", users.staffId)
                        .putExtra("receiverName", users.displayName)
                        .putExtra("userId", myId))
            }
            is Users -> {
                startActivity(
                    Intent(this, ChatActivity::class.java)
                        .putExtra("receiverId", users.userId)
                        .putExtra("receiverName", users.displayName)
                        .putExtra("userId", myId))
            }
        }
    }
}