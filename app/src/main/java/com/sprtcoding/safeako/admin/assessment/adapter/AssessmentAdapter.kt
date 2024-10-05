package com.sprtcoding.safeako.admin.assessment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.firebaseUtils.Utils.getUsers
import com.sprtcoding.safeako.model.AssessmentModel
import com.sprtcoding.safeako.user.fragment.viewmodel.AssessmentViewModel
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.Utility.getAvatar
import de.hdodenhof.circleimageview.CircleImageView

class AssessmentAdapter(
    val arrayList: ArrayList<AssessmentModel>,
    val context: Context,
    private val myId: String,
): RecyclerView.Adapter<AssessmentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.assessment_request,parent,false)
        return ViewHolder(root)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val assessment = arrayList[position]

        holder.bind(assessment)

        holder.itemView.setOnClickListener {
            Utility.loadDocument(assessment.docId!!, assessment.userId!!, assessment.id!!, myId, context)
        }
    }

    fun removeItem(position: Int) {
        if (position < arrayList.size) {
            val assessment = arrayList[position]
            arrayList.removeAt(position)
            notifyItemRemoved(position)
            //viewModel.remove(message)
        }
    }

    inner  class ViewHolder(binding: View) : RecyclerView.ViewHolder(binding) {
        private val tvUsername: TextView = binding.findViewById(R.id.tv_user_name)
        private val imgUserPic: CircleImageView = binding.findViewById(R.id.user_pic)
        private val tvDate: TextView = binding.findViewById(R.id.tv_date)
        private lateinit var formattedDate: String

        fun bind(assessment: AssessmentModel){
            // Format the date into desired format
            // Initialize Handler and Runnable for periodic update

            formattedDate = assessment.submitOn?.let { Utility.formatDateTime(it) }.toString()

            tvDate.text = formattedDate

            getUsers(assessment.userId!!) { success, user, _ ->
                if(success) {
                    tvUsername.text = user?.userId
                }
            }

            getAvatar(assessment.userId, imgUserPic)
        }
    }
}