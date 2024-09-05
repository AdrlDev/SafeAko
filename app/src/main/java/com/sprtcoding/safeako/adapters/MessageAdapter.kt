package com.sprtcoding.safeako.adapters

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.firebaseUtils.Utils
import com.sprtcoding.safeako.model.Message
import com.sprtcoding.safeako.user.activity.message_activity.ChatActivity
import com.sprtcoding.safeako.viewmodels.MessageViewModel
import com.sprtcoding.safeako.utils.Utility
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(
    private val viewModel: MessageViewModel,
    val arrayList: ArrayList<Message>,
    val context: Context): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        var root = LayoutInflater.from(parent.context).inflate(R.layout.message_item,parent,false)
        return MessageViewHolder(root)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(arrayList[position])

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, ChatActivity::class.java)
                .putExtra("userId", arrayList[position].receiverId)
                .putExtra("receiverId", arrayList[position].senderId)
                .putExtra("receiverName", arrayList[position].senderName))
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

    inner  class MessageViewHolder(private val binding: View) : RecyclerView.ViewHolder(binding) {
        private val tvUsername: TextView = binding.findViewById(R.id.tv_user_name)
        private val imgUserPic: CircleImageView = binding.findViewById(R.id.user_pic)
        private val tvMessage: TextView = binding.findViewById(R.id.tv_message)
        private val tvDate: TextView = binding.findViewById(R.id.tv_date)
        private lateinit var formattedDate: String

        fun bind(message: Message){
            // Format the date into desired format
            // Initialize Handler and Runnable for periodic update

            formattedDate = message.receivedOn?.let { Utility.formatDateTime(it) }.toString()

            message.senderId?.let {
                Utils.getUsers(it) { success, user, message ->
                    if(success) {
                        if (user != null) {
                            Picasso.get()
                                .load(user.avatar)
                                .placeholder(R.drawable.avatar)
                                .fit()
                                .into(imgUserPic)
                        }
                    }
                }
            }
            tvUsername.text = message.senderName
            tvMessage.text = message.message
            tvDate.text = formattedDate
        }
    }
}