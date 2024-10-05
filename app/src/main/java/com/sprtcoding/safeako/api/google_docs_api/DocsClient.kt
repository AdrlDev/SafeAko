package com.sprtcoding.safeako.api.google_docs_api

import com.google.api.services.docs.v1.model.Document
import com.google.api.services.docs.v1.model.WriteControl
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sprtcoding.safeako.api.google_docs_api.deserializer.DocumentDeserializer
import com.sprtcoding.safeako.api.google_docs_api.deserializer.WriteControlDeserializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DocsClient {
    private const val BASE_URL = "https://docs.googleapis.com/v1/"
    private const val DRIVE_URL = "https://www.googleapis.com/"

    private val logging = HttpLoggingInterceptor()

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            println("Using Token: ${AuthTokenManager.accessToken}") // Debugging line
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${AuthTokenManager.accessToken}")
                .build()

            chain.proceed(request)
        }
        .build()

    private val clientDrive = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            println("Using Token: ${AuthTokenManager.accessToken}") // Debugging line
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${AuthTokenManager.accessToken}")
                .build()

            chain.proceed(request)
        }
        .build()

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(WriteControl::class.java, WriteControlDeserializer())
        .create()

    private val gson2: Gson = GsonBuilder()
        .registerTypeAdapter(Document::class.java, DocumentDeserializer())
        .create()

    val instanceGet: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson2))
            .client(client)
            .build()
    }

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    val instanceDrive: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(DRIVE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(clientDrive)
            .build()
    }
}