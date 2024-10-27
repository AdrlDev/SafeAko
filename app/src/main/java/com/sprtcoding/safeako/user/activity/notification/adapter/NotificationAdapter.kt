package com.sprtcoding.safeako.user.activity.notification.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.admin.appointment.ViewSchedule
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils.getUsers
import com.sprtcoding.safeako.model.AppointmentModel
import com.sprtcoding.safeako.model.StaffModel
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.activity.notification.viewmodel.UserAppointmentViewModel
import com.sprtcoding.safeako.user.activity.user_appointment.ViewUserAppointment
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.Utility.getAvatar
import de.hdodenhof.circleimageview.CircleImageView

class NotificationAdapter(
    val arrayList: ArrayList<AppointmentModel>,
    private val userAppointmentViewModel: UserAppointmentViewModel,
    val context: Context
): RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.user_notification,parent,false)
        return ViewHolder(root)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = arrayList[position]

        holder.bind(appointment)

        val formattedDate = appointment.dateOfAppointment?.let { Utility.formatDateToDateString(it) }.toString()
        val formattedTime = appointment.timeOfAppointment?.let { Utility.formatDateTo12Hour(it) }.toString()

        holder.itemView.setOnClickListener {
            userAppointmentViewModel.updateReadState(appointment.id!!)
            context.startActivity(Intent(context, ViewUserAppointment::class.java)
                .putExtra("myID", appointment.userId)
                .putExtra("adminID", appointment.senderId)
                .putExtra("appointmentId", appointment.id)
                .putExtra("appointmentNote", appointment.note)
                .putExtra("appointmentType", appointment.type)
                .putExtra("appointmentDate", formattedDate)
                .putExtra("appointmentTime", formattedTime)
                .putExtra("appointmentStatus", appointment.status))
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
        private val tvInformation: TextView = binding.findViewById(R.id.tv_info)

        @SuppressLint("SetTextI18n")
        fun bind(appointment: AppointmentModel){
            // Format the date into desired format
            // Initialize Handler and Runnable for periodic update

            if (!appointment.read) {
                tvUsername.setTextColor(ContextCompat.getColor(context, R.color._dark))
                tvInformation.setTextColor(ContextCompat.getColor(context, R.color._dark))
                tvDate.setTextColor(ContextCompat.getColor(context, R.color._dark))
            } else {
                tvUsername.setTextColor(ContextCompat.getColor(context, R.color.dark_text))
                tvInformation.setTextColor(ContextCompat.getColor(context, R.color.dark_text))
                tvDate.setTextColor(ContextCompat.getColor(context, R.color.dark_text))
            }

            Log.d("READ_STATUS", "${appointment.read}")

            val formatedDate = Utility.formatDateTime(appointment.createdAt!!)
            tvDate.text = formatedDate

            getUsers(appointment.senderId!!) { success, user, _ ->
                if(success) {
                    when(user) {
                        is StaffModel -> {
                            tvUsername.text = "${user.displayName} (Staff)"
                        }
                        is Users -> {
                            tvUsername.text = user.displayName
                        }
                    }
                }
            }

            getAvatar(appointment.senderId, imgUserPic)

            val formattedDate = appointment.dateOfAppointment?.let { Utility.formatDateToDateString(it) }.toString()
            val formattedTime = appointment.timeOfAppointment?.let { Utility.formatDateTo12Hour(it) }.toString()

            tvInformation.text = "Set a ${appointment.type} on $formattedDate at $formattedTime"
        }
    }
}