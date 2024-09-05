package com.sprtcoding.safeako.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.firebaseUtils.Utils
import com.sprtcoding.safeako.model.Message
import com.sprtcoding.safeako.user.activity.message_activity.ChatViewModel
import com.sprtcoding.safeako.utils.Constants
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(
    private val viewModel: ChatViewModel,
    private val userId: String,
    val arrayList: ArrayList<Message>,
    val context: Context
): RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {

        if(viewType == Constants.MSG_TYPE_RIGHT) {
            val root = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_right,parent,false)
            return MessageViewHolder(root)
        } else {
            val root = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_left,parent,false)
            return MessageViewHolder(root)
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(arrayList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return if(arrayList[position].senderId == userId) {
            Constants.MSG_TYPE_RIGHT
        } else {
            Constants.MSG_TYPE_LEFT
        }
    }

    inner  class MessageViewHolder(private val binding: View) : RecyclerView.ViewHolder(binding) {
        private val imgUserPic: CircleImageView = binding.findViewById(R.id.img_user_pic)
        private val tvChat: TextView = binding.findViewById(R.id.tv_chat)

        fun bind(message: Message){

            message.senderId?.let {
                Utils.getUsers(it) { success, user, message ->
                    if (success) {
                        if (user != null) {
                            Picasso
                                .get()
                                .load(user.avatar)
                                .placeholder(R.drawable.avatar)
                                .into(imgUserPic)
                        }
                    }
                }
            }
            tvChat.text = message.message

        }
    }
}