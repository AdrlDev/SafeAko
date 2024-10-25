package com.sprtcoding.safeako.user.activity.about.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.model.Developers
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AboutAdapter(
    private val context: Context,
    private val devs: List<Developers>
) : RecyclerView.Adapter<AboutAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.developers, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dev = devs[position]
        holder.bind(dev)
    }

    override fun getItemCount(): Int {
        return devs.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val tvEmail: TextView = itemView.findViewById(R.id.tv_email)
        private val avatar: CircleImageView = itemView.findViewById(R.id.user_pic)

        fun bind(dev: Developers) {
            tvName.text = dev.name
            tvEmail.text = dev.email

            Picasso.get()
                .load(dev.image)
                .placeholder(R.drawable.avatar_man)
                .fit()
                .into(avatar)
        }
    }
}