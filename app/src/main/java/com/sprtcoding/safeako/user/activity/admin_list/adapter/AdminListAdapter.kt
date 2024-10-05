package com.sprtcoding.safeako.user.activity.admin_list.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.activity.admin_list.viewmodel.AdminListViewModel
import com.sprtcoding.safeako.user.activity.admin_list.IAdminListCallBack
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AdminListAdapter(
    private val viewModel: AdminListViewModel,
    private val context: Context,
    val userList: ArrayList<Users>,
    private val onUserClick: IAdminListCallBack
) : RecyclerView.Adapter<AdminListAdapter.UserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDisplayName: TextView = itemView.findViewById(R.id.tv_display_name)
        private val tvPhone: TextView = itemView.findViewById(R.id.tv_phone)
        private val tvEmail: TextView = itemView.findViewById(R.id.tv_email)
        private val tvFullName: TextView = itemView.findViewById(R.id.tv_full_name)
        private val tvOccupation: TextView = itemView.findViewById(R.id.tv_occupation)
        private val avatar: CircleImageView = itemView.findViewById(R.id.avatar)


        fun bind(user: Users) {
            tvDisplayName.text = user.displayName
            tvPhone.text = user.phone
            tvEmail.text = user.email
            tvFullName.text = user.fullName
            tvOccupation.text = user.occupation
            Picasso.get()
                .load(user.avatar)
                .placeholder(R.drawable.avatar)
                .fit()
                .into(avatar)
            itemView.setOnClickListener { onUserClick.onClick(user) }
        }
    }
}