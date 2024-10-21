package com.sprtcoding.safeako.user.activity.contact.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.model.StaffModel
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.activity.contact.contract.IContact
import com.sprtcoding.safeako.utils.Utility
import de.hdodenhof.circleimageview.CircleImageView

class ContactAdapter(
    private val context: Context,
    private val contacts: List<Any>,
    private val onUserClick: IContact
) : RecyclerView.Adapter<ContactAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDisplayName: TextView = itemView.findViewById(R.id.tv_display_name)
        private val tvPhone: TextView = itemView.findViewById(R.id.tv_phone)
        private val tvEmail: TextView = itemView.findViewById(R.id.tv_email)
        private val tvFullName: TextView = itemView.findViewById(R.id.tv_full_name)
        private val tvOccupation: TextView = itemView.findViewById(R.id.tv_occupation)
        private val avatar: CircleImageView = itemView.findViewById(R.id.avatar)


        @SuppressLint("SetTextI18n")
        fun bind(user: Any) {
            when(user) {
                is StaffModel -> {
                    tvDisplayName.text = "${user.displayName} (Staff)"
                    tvPhone.text = user.phone
                    tvEmail.visibility = View.GONE
                    tvFullName.text = user.fullName
                    tvOccupation.text = user.role
                    Utility.getAvatar(user.staffId, avatar)
                    itemView.setOnClickListener { onUserClick.onClick(user) }
                }
                is Users -> {
                    tvDisplayName.text = user.displayName
                    tvPhone.text = user.phone
                    tvEmail.text = user.email
                    tvFullName.text = user.fullName
                    tvOccupation.text = user.occupation
                    Utility.getAvatar(user.userId!!, avatar)
                    itemView.setOnClickListener { onUserClick.onClick(user) }
                }
            }
        }
    }
}