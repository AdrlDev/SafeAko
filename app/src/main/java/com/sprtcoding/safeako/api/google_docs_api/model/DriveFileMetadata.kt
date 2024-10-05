package com.sprtcoding.safeako.api.google_docs_api.model

import com.google.gson.annotations.SerializedName

data class DriveFileMetadata(
    @SerializedName("name")
    val name: String?
)
