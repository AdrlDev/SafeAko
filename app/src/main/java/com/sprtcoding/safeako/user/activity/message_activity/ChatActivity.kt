package com.sprtcoding.safeako.user.activity.message_activity

import android.annotation.SuppressLint
import android.content.Intent
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
import com.sprtcoding.safeako.adapters.ChatAdapter
import com.sprtcoding.safeako.firebaseUtils.Utils
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
    private lateinit var factory: ChatViewModelFactory

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

    private fun init() {
        val userId = intent.getStringExtra("userId")
        val receiverId = intent.getStringExtra("receiverId")
        val receiverAvatar = intent.getStringExtra("receiverAvatar")
        val receiverName = intent.getStringExtra("receiverName")
        val name = receiverName ?: receiverId

        tvReceiverName.text = name

        if (receiverId != null) {
            Utils.getUsers(receiverId) { success, user, message ->
                if (success) {
                    if (user != null) {
                        Picasso.get()
                            .load(user.avatar)
                            .placeholder(R.drawable.avatar)
                            .fit()
                            .into(avatar)
                    }
                }
            }
        }

        Picasso.get()
            .load(receiverAvatar)
            .placeholder(R.drawable.avatar)
            .fit()
            .into(avatar)

        viewManager = LinearLayoutManager(this)
        viewManager.stackFromEnd = true
        rvChats.setHasFixedSize(true)
        rvChats.layoutManager = viewManager
        factory = ChatViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[ChatViewModel::class.java]
        adapter = userId?.let { ChatAdapter(viewModel, it, arrayListOf(), this) }!!
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
            var senderName = ""

            if (userId != null) {
                Utils.getUsers(userId) { success, user, message ->
                    if(success) {
                        if (user != null) {
                            senderName = if(user.displayName == null) {
                                user.userId.toString()
                            } else {
                                user.displayName.toString()
                            }

                            val message = etMessage.text.toString()

                            if (message.isNotEmpty() && userId != null && receiverId != null) {
                                if (name != null) {
                                    viewModel.addChats(
                                        userId,
                                        receiverId,
                                        senderName,
                                        name,
                                        message,
                                        callback = {success, message ->
                                            if (!success) {
                                                // Handle error
                                                if (message != null) {
                                                    Log.i("data", message)
                                                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                                                }
                                            } else {
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
            }

        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeData(){
        viewModel.liveMessage.observe(this) {
            Log.i("data", it.toString())
            adapter.arrayList.clear()
            adapter.arrayList.addAll(it)
            adapter.notifyDataSetChanged()
            // Scroll RecyclerView to the bottom after new messages are added
            rvChats.scrollToPosition(adapter.itemCount - 1)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getChats(){
        val userId = intent.getStringExtra("userId")
        // Fetch messages for a specific receiverId
        if (userId != null) {
            viewModel.retrieveChats(userId, callback = { success, message ->
                if (!success) {
                    // Handle error
                    if (message != null) {
                        Log.i("data", message)
                    }
                }
            })
        }
        rvChats.adapter?.notifyDataSetChanged()
    }
}