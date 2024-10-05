package com.sprtcoding.safeako.user.activity.sti.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.utils.Utility.generateExoPlayerThumbnail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class STIAdapter(private val videos: List<Int>,
                 private val context: Context,
                 private val onVideoClick: (Int) -> Unit,
                 private val coroutineScope: CoroutineScope
) :
    RecyclerView.Adapter<STIAdapter.VideoViewHolder>() {
    class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val videoThumbnail: ImageView = view.findViewById(R.id.thumbnail_img)
        val videoTitle: TextView = view.findViewById(R.id.tv_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.video_layout, parent, false)
        return VideoViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {

        val videoResId = videos[position]
        val videoUri = Uri.parse("android.resource://${context.packageName}/$videoResId")

        // Load thumbnail asynchronously to avoid blocking the UI thread
        coroutineScope.launch {
            val thumbnail: Bitmap? = loadThumbnailAsync(videoUri)
            if (thumbnail != null) {
                holder.videoThumbnail.setImageBitmap(thumbnail)
            }
        }

        holder.itemView.setOnClickListener {
            onVideoClick(videoResId)
        }

        when(position) {
            0 -> {
                holder.videoTitle.text = "Introduction"
            }
            1 -> {
                holder.videoTitle.text = "Type of STDs"
            }
            2 -> {
                holder.videoTitle.text = "Symptoms"
            }
            3 -> {
                holder.videoTitle.text = "Testing Method"
            }
            4 -> {
                holder.videoTitle.text = "Prevention"
            }
            5 -> {
                holder.videoTitle.text = "Awareness"
            }
            6 -> {
                holder.videoTitle.text = "Impact"
            }
            7 -> {
                holder.videoTitle.text = "Global Perspective"
            }
        }
    }

    // Function to generate the thumbnail on a background thread
    private suspend fun loadThumbnailAsync(videoUri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        generateExoPlayerThumbnail(videoUri, context)
    }

    override fun getItemCount(): Int = videos.size
}