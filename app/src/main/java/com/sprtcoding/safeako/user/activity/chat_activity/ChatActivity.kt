package com.sprtcoding.safeako.user.activity.chat_activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.user.activity.chat_activity.adapter.ChatAdapter
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.model.StaffModel
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.activity.chat_activity.viewmodels.ChatViewModel
import com.sprtcoding.safeako.utils.Constants.avatarMap
import com.sprtcoding.safeako.utils.Utility
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatActivity : AppCompatActivity() {
    private lateinit var rvChats: RecyclerView
    private lateinit var tvReceiverName: TextView
    private lateinit var etMessage: EditText
    private lateinit var avatar: CircleImageView
    private lateinit var btnSend: ImageButton
    private lateinit var btnBack: ImageView
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var viewModel: ChatViewModel
    private lateinit var adapter: ChatAdapter
    private var userID: String? = null
    private var receiverId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
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
        rvChats = findViewById(R.id.rv_chats)
        tvReceiverName = findViewById(R.id.tv_receiver_name)
        etMessage = findViewById(R.id.et_message)
        btnSend = findViewById(R.id.btn_send)
        btnBack = findViewById(R.id.btn_back)
        avatar = findViewById(R.id.avatar)
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        userID = intent.getStringExtra("userId")
        receiverId = intent.getStringExtra("receiverId")
        val receiverAvatar = intent.getStringExtra("receiverAvatar")
        val receiverName = intent.getStringExtra("receiverName")
        val name = receiverName ?: receiverId

        Utils.getUser(receiverId!!) { user ->
            when(user) {
                is StaffModel -> {
                    tvReceiverName.text = "$name (Staff)"
                }
                is Users -> {
                    tvReceiverName.text = "$name"
                }
            }
        }

        if (receiverId != null) {
            Utils.getUsers(receiverId!!) { success, user, message ->
                if (success) {
                    if (user != null) {
                        when(user) {
                            is StaffModel -> {
                                val avatarImg = user.avatar

                                if(avatarMap.containsKey(avatarImg)) {
                                    Picasso.get()
                                        .load(avatarMap[avatarImg]!!)
                                        .placeholder(R.drawable.avatar_man)
                                        .fit()
                                        .into(avatar)
                                } else {
                                    if(user.role == "Admin") {
                                        Picasso.get()
                                            .load(R.drawable.avatar)
                                            .placeholder(R.drawable.avatar_man)
                                            .fit()
                                            .into(avatar)
                                    } else {
                                        Picasso.get()
                                            .load(R.drawable.avatar_man)
                                            .placeholder(R.drawable.avatar_man)
                                            .fit()
                                            .into(avatar)
                                    }
                                }
                            }
                            is Users -> {
                                val avatarImg = user.avatar

                                if(avatarImg != null) {
                                    if(avatarMap.containsKey(avatarImg)) {
                                        Picasso.get()
                                            .load(avatarMap[avatarImg]!!)
                                            .placeholder(R.drawable.avatar_man)
                                            .fit()
                                            .into(avatar)
                                    } else {
                                        if(user.role.equals("Admin")) {
                                            Picasso.get()
                                                .load(R.drawable.avatar)
                                                .placeholder(R.drawable.avatar_man)
                                                .fit()
                                                .into(avatar)
                                        } else {
                                            Picasso.get()
                                                .load(R.drawable.avatar_man)
                                                .placeholder(R.drawable.avatar_man)
                                                .fit()
                                                .into(avatar)
                                        }
                                    }
                                } else {
                                    if(user.role.equals("Admin")) {
                                        Picasso.get()
                                            .load(R.drawable.avatar)
                                            .placeholder(R.drawable.avatar_man)
                                            .fit()
                                            .into(avatar)
                                    } else {
                                        Picasso.get()
                                            .load(R.drawable.avatar_man)
                                            .placeholder(R.drawable.avatar_man)
                                            .fit()
                                            .into(avatar)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Utility.showAlertDialogWithYesNo(this, layoutInflater, message!!, "", "Ok", null) {}
                }
            }
        }

        Picasso.get()
            .load(receiverAvatar)
            .placeholder(R.drawable.avatar_man)
            .fit()
            .into(avatar)

        viewManager = LinearLayoutManager(this)
        viewManager.stackFromEnd = true
        rvChats.setHasFixedSize(true)
        rvChats.layoutManager = viewManager
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        adapter = userID?.let { senderID ->
            ChatAdapter(senderID, arrayListOf(), this)
        }!!
        rvChats.adapter = adapter
    }

    private fun afterInit() {
        getChats()
        observeData()

        etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                btnSend.isEnabled = p0.toString().isNotEmpty()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        btnSend.setOnClickListener {
            val userId = intent.getStringExtra("userId")
            val receiverId = intent.getStringExtra("receiverId")
            val receiverName = intent.getStringExtra("receiverName")
            val name = receiverName ?: receiverId
            var senderName: String

            if (userId != null) {
                Utils.getUsers(userId) { success, user, message ->
                    if(success) {
                        if (user != null) {
                            when(user) {
                                is StaffModel -> {
                                    senderName = user.displayName

                                    val msg = etMessage.text.toString()

                                    if (msg.isNotEmpty() && receiverId != null) {
                                        if (name != null) {
                                            viewModel.addChats(
                                                userId,
                                                receiverId,
                                                senderName,
                                                name,
                                                msg,
                                                callback = {isSuccess, txt ->
                                                    if (!isSuccess) {
                                                        // Handle error
                                                        if (message != null) {
                                                            Utility.showAlertDialogWithYesNo(
                                                                this,
                                                                layoutInflater,
                                                                txt!!,
                                                                "Cancel",
                                                                "Ok",
                                                                null
                                                            ) {}
                                                        }
                                                    } else {
                                                        sendNotifications(receiverId, senderName, msg)
                                                        Toast.makeText(this, "Message sent successfully", Toast.LENGTH_SHORT).show()
                                                        getChats()
                                                    }
                                                })
                                        }
                                        etMessage.text.clear()
                                    }
                                }
                                is Users -> {
                                    senderName = if(user.displayName == null) {
                                        user.userId.toString()
                                    } else {
                                        user.displayName.toString()
                                    }

                                    val msg = etMessage.text.toString()

                                    if (msg.isNotEmpty() && receiverId != null) {
                                        if (name != null) {
                                            viewModel.addChats(
                                                userId,
                                                receiverId,
                                                senderName,
                                                name,
                                                msg,
                                                callback = {isSuccess, txt ->
                                                    if (!isSuccess) {
                                                        // Handle error
                                                        if (message != null) {
                                                            Utility.showAlertDialogWithYesNo(
                                                                this,
                                                                layoutInflater,
                                                                txt!!,
                                                                "Cancel",
                                                                "Ok",
                                                                null
                                                            ) {}
                                                        }
                                                    } else {
                                                        sendNotifications(receiverId, senderName, msg)
                                                        Toast.makeText(this, "Message sent successfully", Toast.LENGTH_SHORT).show()
                                                        getChats()
                                                    }
                                                })
                                        }
                                        etMessage.text.clear()
                                    }
                                }
                            }
                        }
                    } else {
                        Utility.showAlertDialogWithYesNo(this, layoutInflater, message!!, "Cancel", "Ok", null) {}
                    }
                }
            }

        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun sendNotifications(receiverId: String, senderName: String, msg: String) {
        Utils.sendNotification(
            receiverId,
            "$senderName sent you a message",
            msg,
            "chat",
            this
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeData(){
        viewModel.liveMessage.observe(this) { message ->
            Log.i("data", message.toString())
            adapter.arrayList.clear()
            adapter.arrayList.addAll(message)
            adapter.notifyDataSetChanged()
            // Scroll RecyclerView to the bottom after new messages are added
            rvChats.scrollToPosition(adapter.itemCount - 1)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getChats(){
        // Fetch messages for a specific receiverId
        viewModel.retrieveChats(userID!!, receiverId!!, callback = { success, message ->
            if (!success) {
                // Handle error
                if (message != null) {
                    Utility.showAlertDialogWithYesNo(
                        this,
                        layoutInflater,
                        message,
                        "Cancel",
                        "Ok",
                        null
                    ){}
                }
            }
        })
        rvChats.adapter?.notifyDataSetChanged()
    }
}