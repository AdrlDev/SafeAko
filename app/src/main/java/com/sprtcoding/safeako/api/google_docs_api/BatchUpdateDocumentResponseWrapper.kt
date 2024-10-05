package com.sprtcoding.safeako.api.google_docs_api

import com.google.gson.annotations.SerializedName

class BatchUpdateDocumentResponseWrapper(
    @SerializedName("writeControl") val writeControl: Map<String, Any>,
    @SerializedName("documentId") val documentId: String
)