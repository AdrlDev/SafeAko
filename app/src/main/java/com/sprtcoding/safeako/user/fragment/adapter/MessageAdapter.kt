package com.sprtcoding.safeako.user.fragment.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.model.Message
import com.sprtcoding.safeako.user.activity.chat_activity.ChatActivity
import com.sprtcoding.safeako.user.fragment.viewmodel.MessageViewModel
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.Utility.getAvatar
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(
    private val viewModel: MessageViewModel,
    val arrayList: ArrayList<Message>,
    private val uid: String,
    val context: Context): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.message_item,parent,false)
        return MessageViewHolder(root)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = arrayList[position]

        holder.bind(message)

        holder.itemView.setOnClickListener {
            if(message.senderId != uid) {
                context.startActivity(
                    Intent(context, ChatActivity::class.java)
                        .putExtra("userId", message.receiverId)
                        .putExtra("receiverId", message.senderId)
                        .putExtra("receiverName", message.senderName)
                )
            } else {
                context.startActivity(
                    Intent(context, ChatActivity::class.java)
                        .putExtra("userId", message.senderId)
                        .putExtra("receiverId", message.receiverId)
                        .putExtra("receiverName", message.receiverName)
                )
            }
        }
    }

    fun removeItem(position: Int) {
        if (position < arrayList.size) {
            val message = arrayList[position]
            arrayList.removeAt(position)
            notifyItemRemoved(position)
            viewModel.remove(message)
        }
    }

    inner  class MessageViewHolder(binding: View) : RecyclerView.ViewHolder(binding) {
        private val tvUsername: TextView = binding.findViewById(R.id.tv_user_name)
        private val imgUserPic: CircleImageView = binding.findViewById(R.id.user_pic)
        private val tvMessage: TextView = binding.findViewById(R.id.tv_message)
        private val tvDate: TextView = binding.findViewById(R.id.tv_date)
        private lateinit var formattedDate: String

        fun bind(message: Message){
            // Format the date into desired format
            // Initialize Handler and Runnable for periodic update

            formattedDate = message.receivedOn?.let { Utility.formatDateTime(it) }.toString()

            if(message.senderId != uid) {
                message.senderId?.let { senderUID ->
                    getAvatar(senderUID, imgUserPic)
                }

                tvUsername.text = message.senderName
            } else {
                message.receiverId?.let { receiverUID ->
                    getAvatar(receiverUID, imgUserPic)
                }

                tvUsername.text = message.receiverName
            }
            tvMessage.text = message.message
            tvDate.text = formattedDate
        }
    }
}