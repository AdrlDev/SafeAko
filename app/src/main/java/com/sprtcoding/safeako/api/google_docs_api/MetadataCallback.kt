package com.sprtcoding.safeako.api.google_docs_api

interface MetadataCallback {
    fun onSuccess(thumbnailLink: String?, fileName: String?)
    fun onError(errorMessage: String)
}