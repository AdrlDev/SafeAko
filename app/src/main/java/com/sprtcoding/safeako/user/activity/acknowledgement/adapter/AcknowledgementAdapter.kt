package com.sprtcoding.safeako.user.activity.acknowledgement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.model.AcknowledgementModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AcknowledgementAdapter(
    private val context: Context,
    private val acknowledgementList: List<AcknowledgementModel>
) : RecyclerView.Adapter<AcknowledgementAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.acknowledgement_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val acknowledgement= acknowledgementList[position]
        holder.bind(acknowledgement)
    }

    override fun getItemCount(): Int {
        return acknowledgementList.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDisplayName: TextView = itemView.findViewById(R.id.tv_display_name)
        private val tvPosition: TextView = itemView.findViewById(R.id.tv_position)
        private val tvOtherPosition: TextView = itemView.findViewById(R.id.tv_other_position)
        private val avatar: CircleImageView = itemView.findViewById(R.id.avatar)

        @SuppressLint("SetTextI18n")
        fun bind(acknowledgement: AcknowledgementModel) {
            if(acknowledgement.otherPosition == null) {
                tvOtherPosition.visibility = View.GONE
                tvDisplayName.text = acknowledgement.name!!
                tvPosition.text = acknowledgement.position!!
                Picasso.get().load(acknowledgement.img!!).placeholder(R.drawable.avatar_man).into(avatar)
            } else {
                tvDisplayName.text = acknowledgement.name!!
                tvPosition.text = acknowledgement.position!!
                tvOtherPosition.text = acknowledgement.otherPosition!!
                Picasso.get().load(acknowledgement.img!!).placeholder(R.drawable.avatar_man).into(avatar)
            }
        }
    }
}