package com.sprtcoding.safeako.user.activity.sti.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.api.google_docs_api.MetadataCallback
import com.sprtcoding.safeako.user.fragment.viewmodel.AssessmentViewModel
import com.sprtcoding.safeako.utils.Constants
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class STIAdapter(private val videoFileIds: List<String>,
                 private val context: Context,
                 private val onVideoClick: (String, String) -> Unit,
                 private val coroutineScope: CoroutineScope,
                 private val assessmentViewModel: AssessmentViewModel
) :
    RecyclerView.Adapter<STIAdapter.VideoViewHolder>() {
    class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val videoThumbnail: ImageView = view.findViewById(R.id.thumbnail_img)
        val videoTitle: TextView = view.findViewById(R.id.tv_title)
        val shimmerContainer: ShimmerFrameLayout = view.findViewById(R.id.shimmer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.video_layout, parent, false)
        return VideoViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {

        val fileId = videoFileIds[position]
        var fileNames = ""

        // Load thumbnail asynchronously to avoid blocking the UI thread
        coroutineScope.launch {
            assessmentViewModel.getDriveFileMetadata(context, fileId, object :
                MetadataCallback {
                override fun onSuccess(thumbnailLink: String?, fileName: String?) {
                    // Check if thumbnailLink is not null before proceeding
                    if (!thumbnailLink.isNullOrEmpty()) {
                        // Switch to the main thread for UI operations
                        fileNames = fileName ?: ""
                        coroutineScope.launch {
                            holder.videoThumbnail.visibility = View.INVISIBLE
                            holder.videoThumbnail.scaleType = ImageView.ScaleType.CENTER_INSIDE
                            holder.shimmerContainer.startShimmer()

                            Picasso.get()
                                .load(thumbnailLink)
                                .placeholder(R.drawable.reel)
                                .into(holder.videoThumbnail, object : Callback {
                                    override fun onSuccess() {
                                        // Stop shimmer effect when the thumbnail is loaded
                                        holder.shimmerContainer.stopShimmer()
                                        holder.shimmerContainer.setShimmer(null)
                                        holder.videoThumbnail.visibility = View.VISIBLE
                                        holder.videoThumbnail.scaleType = ImageView.ScaleType.FIT_XY
                                    }

                                    override fun onError(e: Exception?) {
                                        // Handle the error case, stop shimmer and show default image
                                        holder.shimmerContainer.stopShimmer()
                                        holder.shimmerContainer.setShimmer(null)
                                        holder.videoThumbnail.visibility = View.VISIBLE
                                        holder.videoThumbnail.setImageResource(R.drawable.reel)
                                        holder.videoThumbnail.scaleType = ImageView.ScaleType.CENTER_INSIDE
                                    }
                                })
                        }
                    } else {
                        // Handle the case where the thumbnail link is null or empty
                        println("Failed to retrieve a valid thumbnail link for video: $fileId")
                        // Optionally set a default image or handle it accordingly
                        // Optionally set a default image or handle it accordingly
                        coroutineScope.launch {
                            holder.videoThumbnail.setImageResource(R.drawable.reel)
                            holder.videoThumbnail.scaleType = ImageView.ScaleType.CENTER_INSIDE
                        }
                    }
                }

                override fun onError(errorMessage: String) {
                    // Handle the case where the thumbnail link is null or empty
                    println(errorMessage)
                }
            })
        }

        holder.itemView.setOnClickListener {
            onVideoClick(fileId, fileNames)
        }

        holder.videoTitle.text = Constants.videoTitle[position]
    }

    override fun getItemCount(): Int = videoFileIds.size
}