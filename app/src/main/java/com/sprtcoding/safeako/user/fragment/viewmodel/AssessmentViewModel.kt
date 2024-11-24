package com.sprtcoding.safeako.user.fragment.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.api.services.docs.v1.model.BatchUpdateDocumentRequest
import com.google.api.services.docs.v1.model.ReplaceAllTextRequest
import com.google.api.services.docs.v1.model.Request
import com.google.api.services.docs.v1.model.SubstringMatchCriteria
import com.google.api.services.drive.model.File
import com.sprtcoding.safeako.api.google_docs_api.DocsClient
import com.sprtcoding.safeako.api.google_docs_api.GoogleDocsService
import com.sprtcoding.safeako.api.google_docs_api.MetadataCallback
import com.sprtcoding.safeako.api.google_docs_api.model.AssessmentResponse
import com.sprtcoding.safeako.api.google_docs_api.model.CopyRequest
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.model.AssessmentModel
import com.sprtcoding.safeako.model.StaffModel
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.fragment.contract.IAssessment
import com.sprtcoding.safeako.utils.Constants.CHECK_MARK
import com.sprtcoding.safeako.utils.Constants.EMPTY_BOX
import com.sprtcoding.safeako.utils.Utility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class AssessmentViewModel: ViewModel() {
    private val _token = MutableLiveData<Result<String>>()
    val token: LiveData<Result<String>> get() = _token

    private val _copyResult = MutableLiveData<Result<File>>()
    val copyResult: LiveData<Result<File>> get() = _copyResult

    private val _fileName = MutableLiveData<String>()
    val fileName: LiveData<String> get() = _fileName

    private val _response = MutableLiveData<Result<AssessmentResponse>>()
    val response: LiveData<Result<AssessmentResponse>> get() = _response

    private val _isSubmitted = MutableLiveData<Result<Pair<Boolean, String>>>()
    val isSubmitted: LiveData<Result<Pair<Boolean, String>>> get() = _isSubmitted

    private val _assessment = MutableLiveData<Result<AssessmentModel>>()
    val assessment: LiveData<Result<AssessmentModel>> get() = _assessment

    private val _assessmentList = MutableLiveData<Result<ArrayList<AssessmentModel>?>>()
    val assessmentList: LiveData<Result<ArrayList<AssessmentModel>?>> get() = _assessmentList

    // Function to refresh the token
    fun refreshToken(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Update the access token
                val newAccessToken = Utility.getAccessToken(context)
                if (newAccessToken != null) {
                    _token.postValue(Result.success(newAccessToken))
                    Log.d("DocsClient", "Access Token refreshed successfully")
                } else {
                    Log.e("DocsClient", "Failed to refresh access token")
                    _token.postValue(Result.failure(Exception("Failed to refresh access token")))
                }
            } catch (e: Exception) {
                Log.e("DocsClient", "Error refreshing access token: ${e.message}")
                _token.postValue(Result.failure(Exception("Failed to refresh access token")))
            }
        }
    }

    fun getDriveFileMetadata(context: Context, fileId: String, callback: MetadataCallback) {
        refreshToken(context)

        token.observe(context as LifecycleOwner) { result ->
            result.onSuccess { accessToken ->
                Log.d("TOKEN", accessToken)

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val docsGetService = DocsClient.instanceDrive.create(GoogleDocsService::class.java)
                        val response = docsGetService.getFileMetadata(fileId)

                        if (response.isSuccessful) {
                            val fileMetadata = response.body()

                            // Use fileMetadata here (e.g., return the thumbnail link or file name)
                            val thumbnailLink = fileMetadata?.thumbnailLink
                            val fileName = fileMetadata?.name
                            println("File Name: ${fileMetadata?.name}")
                            println("Thumbnail Link: $thumbnailLink")

                            // Call the success callback
                            callback.onSuccess(thumbnailLink, fileName)
                        } else {
                            // Log the error response body for debugging
                            val errorBody = response.errorBody()?.string()
                            println("Failed to retrieve file metadata: ${response.code()} - $errorBody")

                            // Call the error callback
                            callback.onError("Failed to retrieve file metadata: ${response.code()} - $errorBody")
                        }
                    } catch (e: Exception) {
                        Log.e("METADATA", e.message!!, e)
                        e.printStackTrace()

                        // Call the error callback
                        callback.onError(e.message ?: "Unknown error")
                    }
                }
            }
        }
    }


    // Function to copy a Google Docs template
    fun copyTemplate(context: Context, templateFileId: String, copyRequest: CopyRequest) {
        refreshToken(context) // Refresh the token before the operation

        token.observe(context as LifecycleOwner) { result ->
            result.onSuccess { accessToken ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val docsService = DocsClient.instanceDrive.create(GoogleDocsService::class.java)

                        Log.d("TOKEN", accessToken)

                        // Perform the template copy without awaitResponse
                        val copiedFile = docsService.copyTemplate(
                            templateFileId = templateFileId,
                            body = copyRequest
                        )

                        // Post the result to _copyResult LiveData
                        _copyResult.postValue(Result.success(copiedFile))
                        _fileName.postValue(copyRequest.name)
                        Log.d("DocsClient", "Template copied successfully")

                    } catch (e: Exception) {
                        // Check if there's a specific error message from the API
                        if (e is HttpException) {
                            Log.e("DocsClient", "HTTP Error: ${e.response()?.errorBody()?.string()}")
                        } else {
                            Log.e("DocsClient", "Exception while copying template: ${e.message}")
                        }
                        _copyResult.postValue(Result.failure(e))
                        Log.e("DocsClient", "Exception while copying template: ${e.message}")
                    }
                }
            }

            result.onFailure { error ->
                Log.e("DocsClient", "Failed to get access token for copying template: ${error.message}")
            }
        }
    }

    suspend fun updateGoogleDocCheckBox(checkedItems: List<String>, checkboxPositions: List<String>, copiedID: String) {
        val docsService = DocsClient.instance.create(GoogleDocsService::class.java)
        val docsGetService = DocsClient.instanceGet.create(GoogleDocsService::class.java)
        val requests = mutableListOf<Request>()

        Log.d("UpdateCheckBox", "Checkbox positions: $checkboxPositions")
        Log.d("UpdateCheckBox", "Checked items: $checkedItems")

        // Update checkboxes based on the checkedItems list
        checkboxPositions.forEachIndexed { _, label ->
            val textToInsert = if (checkedItems.contains(label)) CHECK_MARK else EMPTY_BOX
            Log.d("UpdateCheckBox", "Label: $label, Checked: ${checkedItems.contains(label)}")
            val insertTextRequest = Request().apply {
                replaceAllText = ReplaceAllTextRequest().apply {
                    containsText = SubstringMatchCriteria().apply {
                        this.text = label
                    }
                    replaceText = textToInsert
                }
            }
            requests.add(insertTextRequest)
        }

        // Build the batch update request
        val batchUpdateRequest = BatchUpdateDocumentRequest().setRequests(requests)

        try {
            val response = docsService.batchUpdateDocument(copiedID, batchUpdateRequest)
            if (response.isSuccessful) {
                // Retrieve the document information
                // Retrieve the document information asynchronously
                val copiedDocumentResponse = docsGetService.getFileMetadata(copiedID)
                if (copiedDocumentResponse.isSuccessful) {
                    val copiedDocument = copiedDocumentResponse.body()
                    val docName = copiedDocument?.name ?: "Untitled Document"
                    Log.d("FILENAME", docName)
                    _response.postValue(Result.success(AssessmentResponse(docName, copiedID, "Submitted successfully.")))
                } else {
                    _response.postValue(Result.success(AssessmentResponse("Error retrieving document", copiedID, "Failed to retrieve.")))
                }
            } else {
                // Handle error response
                _response.postValue(Result.success(AssessmentResponse("Error retrieving document", copiedID, "Error: ${response.errorBody()?.string()}")))
            }
        } catch (e: Exception) {
            // Handle failure
            _response.postValue(Result.failure(Exception("Failure: ${e.message}")))
        }
    }

    fun setAssessment(userId: String, docId: String, docName: String, selectedAnswers: List<String>) {
        Utils.getUser(userId) { user ->
            when(user) {
                is Users -> {
                    val municipality = user.municipality
                    Utils.setAssessmentRequest(userId, docId, docName, municipality!!, selectedAnswers, object : IAssessment {
                        override fun onAssessmentSubmit(success: Boolean, message: String) {
                            if(success) {
                                _isSubmitted.postValue(Result.success(Pair(true, message)))
                            } else {
                                _isSubmitted.postValue(Result.failure(Exception(message)))
                            }
                        }
                    })
                }
            }
        }
    }

    fun getAssessment(userId: String) {
        Utils.getAssessmentRequest(userId, object : IAssessment.Get {
            override fun assessment(assessment: AssessmentModel?) {
                if(assessment != null) {
                    _assessment.postValue(Result.success(assessment))
                } else {
                    _assessment.postValue(Result.failure(Exception("Failed to get assessment!")))
                }
            }
        })
    }

    fun getAllAssessment(uid: String, type: String) {
        Utils.getUser(uid) { user ->
            when(user) {
                is StaffModel -> {
                    val municipality = user.displayName.substringAfter("RHU ").trim()
                    Utils.getAllAssessmentRequest(municipality, type, object : IAssessment.GetAll {
                        override fun assessment(assessmentList: ArrayList<AssessmentModel>?) {
                            if(assessmentList != null) {
                                _assessmentList.postValue(Result.success(assessmentList))
                            } else {
                                _assessmentList.postValue(Result.success(null))
                            }
                        }

                        override fun onError(error: String) {
                            _assessmentList.postValue(Result.failure(Exception(error)))
                        }
                    })
                }
                is Users -> {
                    val municipality = user.displayName?.substringAfter("RHU ")?.trim()
                    Utils.getAllAssessmentRequest(municipality!!, type, object : IAssessment.GetAll {
                        override fun assessment(assessmentList: ArrayList<AssessmentModel>?) {
                            if(assessmentList != null) {
                                _assessmentList.postValue(Result.success(assessmentList))
                            } else {
                                _assessmentList.postValue(Result.success(null))
                            }
                        }

                        override fun onError(error: String) {
                            _assessmentList.postValue(Result.failure(Exception(error)))
                        }
                    })
                }
            }
        }
    }

}