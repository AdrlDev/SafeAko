package com.sprtcoding.safeako.api.google_docs_api.model

data class CopyRequest(
    var name: String? = null,  // New name for the copied document
    val parents: List<String>? = null
)
