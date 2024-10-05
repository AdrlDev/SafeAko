package com.sprtcoding.safeako.user.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.user.fragment.adapter.MessageAdapter
import com.sprtcoding.safeako.firebaseUtils.Utils
import com.sprtcoding.safeako.user.activity.admin_list.AdminListActivity
import com.sprtcoding.safeako.user.fragment.viewmodel.MessageViewModel

class MessageFragment : Fragment() {
    private lateinit var view: View
    private lateinit var rvMessage: RecyclerView
    private lateinit var noMessage: LinearLayout
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var viewModel: MessageViewModel
    private lateinit var userId: String
    private lateinit var fabNewMsg: FloatingActionButton
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_message, container, false)

        initViews()
        init()
        afterInit()

        return view
    }

    private fun initViews() {
        rvMessage = view.findViewById(R.id.rv_message)
        fabNewMsg = view.findViewById(R.id.fab_new_message)
        noMessage = view.findViewById(R.id.no_message_ll)
    }

    private fun init() {

        // Retrieve the arguments in onCreateView if needed
        arguments?.let {
            userId = it.getString("userId").toString()
        }

        viewManager = LinearLayoutManager(context)
        rvMessage.layoutManager = viewManager
        viewModel = ViewModelProvider(this)[MessageViewModel::class.java]
        messageAdapter = context?.let { MessageAdapter(viewModel, arrayListOf(), userId, it) }!!

    }

    private fun afterInit() {
        fabNewMsg.setOnClickListener {
            //do something
            startActivity(Intent(context, AdminListActivity::class.java)
                .putExtra("userId", userId))
        }

        Utils.checkIfAdmin(userId, callback = { success, isAdmin ->
            if (success) {
                if(isAdmin) {
                    fabNewMsg.visibility = View.GONE
                }
            }
        })

        getMessages()
        observeData()
        swipeToDelete()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeData(){
        viewModel.liveMessage.observe(viewLifecycleOwner) {
            Log.i("data", it.toString())
            messageAdapter.arrayList.clear()
            messageAdapter.arrayList.addAll(it)
            messageAdapter.notifyDataSetChanged()
            // Scroll to the top when a new message is added
            rvMessage.scrollToPosition(0)
            rvMessage.adapter = messageAdapter

            if(messageAdapter.arrayList.size == 0) {
                noMessage.visibility = View.VISIBLE
            } else {
                noMessage.visibility = View.GONE
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getMessages(){
        // Fetch messages for a specific receiverId
        viewModel.fetchMessages(userId) { success, message ->
            if (!success) {
                // Handle error
                if (message != null) {
                    Log.i("data", message)
                }
            }
        }
        rvMessage.adapter?.notifyDataSetChanged()

    }

    private fun swipeToDelete() {
        // Set up ItemTouchHelper for swipe-to-delete
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // No drag-and-drop functionality
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Handle swipe-to-delete
                val position = viewHolder.adapterPosition
                messageAdapter.removeItem(position)
            }
        })

        itemTouchHelper.attachToRecyclerView(rvMessage)
    }

}