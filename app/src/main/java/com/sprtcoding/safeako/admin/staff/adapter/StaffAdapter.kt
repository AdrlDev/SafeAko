package com.sprtcoding.safeako.admin.staff.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.model.StaffModel
import com.sprtcoding.safeako.utils.Utility
import de.hdodenhof.circleimageview.CircleImageView

class StaffAdapter(private val staffList: List<StaffModel>, private val context: Context) :
    RecyclerView.Adapter<StaffAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvPhone: TextView = itemView.findViewById(R.id.tv_phone)
        val tvJobDesc: TextView = itemView.findViewById(R.id.tv_job_desc)
        val avatar: CircleImageView = itemView.findViewById(R.id.avatar)
        val statusImg: ImageButton = itemView.findViewById(R.id.img_status)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.staff_layout, parent, false)
        return ViewHolder(
            itemView
        )
    }

    override fun getItemCount(): Int = staffList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val staff = staffList[position]

        holder.tvName.text = staff.fullName
        holder.tvPhone.text = staff.phone
        holder.tvJobDesc.text = staff.jobDesc

        Utility.getAvatar(staff.staffId, holder.avatar)

        val redColor = ContextCompat.getColor(context, R.color._red)
        val greenColor = ContextCompat.getColor(context, R.color.green)

        if(staff.status == "offline") {
            holder.statusImg.imageTintList = ColorStateList.valueOf(redColor)
        } else {
            holder.statusImg.imageTintList = ColorStateList.valueOf(greenColor)
        }

    }
}