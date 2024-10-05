package com.sprtcoding.safeako.admin.appointment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.admin.appointment.ViewSchedule
import com.sprtcoding.safeako.firebaseUtils.Utils.getUsers
import com.sprtcoding.safeako.model.AppointmentModel
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.Utility.getAvatar
import de.hdodenhof.circleimageview.CircleImageView

class AppointmentAdapter(
    val arrayList: ArrayList<AppointmentModel>,
    val context: Context
): RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.appointment_request,parent,false)
        return ViewHolder(root)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = arrayList[position]

        holder.bind(appointment)

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, ViewSchedule::class.java)
                .putExtra("APPOINTMENT_ID", appointment.id)
                .putExtra("USER_ID", appointment.userId)
                .putExtra("SENDER_ID", appointment.senderId)
                .putExtra("TYPE", appointment.type)
            )
        }
    }

    fun removeItem(position: Int) {
        if (position < arrayList.size) {
            val appointment = arrayList[position]
            arrayList.removeAt(position)
            notifyItemRemoved(position)
            //viewModel.remove(message)
        }
    }

    inner  class ViewHolder(binding: View) : RecyclerView.ViewHolder(binding) {
        private val tvUsername: TextView = binding.findViewById(R.id.tv_user_name)
        private val imgUserPic: CircleImageView = binding.findViewById(R.id.user_pic)
        private val tvDate: TextView = binding.findViewById(R.id.tv_date)
        private val tvApprovedBy: TextView = binding.findViewById(R.id.tv_approved_by)
        private lateinit var formattedDate: String
        private lateinit var formattedTime: String

        @SuppressLint("SetTextI18n")
        fun bind(appointment: AppointmentModel){
            // Format the date into desired format
            // Initialize Handler and Runnable for periodic update

            formattedDate = appointment.dateOfAppointment?.let { Utility.formatDateToDateString(it) }.toString()
            formattedTime = appointment.timeOfAppointment?.let { Utility.formatDateTo12Hour(it) }.toString()

            tvDate.text = "$formattedDate at $formattedTime"

            getUsers(appointment.userId!!) { success, user, _ ->
                if(success) {
                    tvUsername.text = user?.userId
                }
            }

            getUsers(appointment.senderId!!) { success, user, _ ->
                if(success) {
                    tvApprovedBy.text = "Approved By: ${user?.fullName}"
                }
            }

            getAvatar(appointment.userId, imgUserPic)
        }
    }
}