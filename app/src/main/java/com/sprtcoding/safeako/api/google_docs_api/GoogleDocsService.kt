package com.sprtcoding.safeako.api.google_docs_api

import com.google.api.services.docs.v1.model.BatchUpdateDocumentRequest
import com.google.api.services.docs.v1.model.Document
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.sprtcoding.safeako.api.google_docs_api.model.CopyRequest
import com.sprtcoding.safeako.api.google_docs_api.model.DriveFileMetadata
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleDocsService {
    @POST("documents/{documentId}:batchUpdate")
    suspend fun batchUpdateDocument(
        @Path("documentId") documentId: String,
        @Body batchUpdateRequest: BatchUpdateDocumentRequest
    ): Response<BatchUpdateDocumentResponseWrapper>

    @POST("drive/v3/files/{fileId}/copy")
    suspend fun copyTemplate(
        @Path("fileId") templateFileId: String,
        @Body body: CopyRequest
    ): File

    @GET("documents/{documentId}")
    suspend fun getDocument(@Path("documentId") documentId: String): Response<Document>

    @GET("files/{fileId}")
    suspend fun getFileMetadata(
        @Path("fileId") fileId: String,
        @Query("fields") fields: String = "name"
    ): Response<DriveFileMetadata>

}