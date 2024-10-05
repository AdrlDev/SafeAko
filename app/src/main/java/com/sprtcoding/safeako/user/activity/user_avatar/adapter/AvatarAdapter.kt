package com.sprtcoding.safeako.user.activity.user_avatar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R

class AvatarAdapter(
    private val avatarList: List<Int>,
    private val onAvatarClickListener: (Int) -> Unit
) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    inner class AvatarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.avatar_img)

        fun bind(avatarResId: Int) {
            imageView.setImageResource(avatarResId)
            itemView.setOnClickListener {
                onAvatarClickListener(avatarResId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.avatar_image, parent, false)
        return AvatarViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        holder.bind(avatarList[position])
    }

    override fun getItemCount(): Int = avatarList.size
}