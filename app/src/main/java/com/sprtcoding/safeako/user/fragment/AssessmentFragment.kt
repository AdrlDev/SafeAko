package com.sprtcoding.safeako.user.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.api.google_docs_api.AuthTokenManager
import com.sprtcoding.safeako.api.google_docs_api.model.CopyRequest
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.model.StaffModel
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.fragment.viewmodel.AssessmentViewModel
import com.sprtcoding.safeako.utils.Constants.COPY_DOC_ID
import com.sprtcoding.safeako.utils.Constants.FOLDER_ID
import com.sprtcoding.safeako.utils.Constants.cat1QArray
import com.sprtcoding.safeako.utils.Constants.cat2QArray
import com.sprtcoding.safeako.utils.Constants.cat3QArray
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.Utility.generateRandomDocumentName
import kotlinx.coroutines.launch

class AssessmentFragment : Fragment() {
    private lateinit var view: View
    private lateinit var cbCatA1: MaterialCheckBox
    private lateinit var cbCatA2: MaterialCheckBox
    private lateinit var cbCatA3: MaterialCheckBox
    private lateinit var cbCatA4: MaterialCheckBox
    private lateinit var cbCat2A1: MaterialCheckBox
    private lateinit var cbCat2A2: MaterialCheckBox
    private lateinit var cbCat2A3: MaterialCheckBox
    private lateinit var cbCat2A4: MaterialCheckBox
    private lateinit var cbCat3Q1A1: MaterialCheckBox
    private lateinit var cbCat3Q1A2: MaterialCheckBox
    private lateinit var cbCat3Q1A3: MaterialCheckBox
    private lateinit var cbCat3Q2A1: MaterialCheckBox
    private lateinit var cbCat3Q2A2: MaterialCheckBox
    private lateinit var cbCat3Q2A3: MaterialCheckBox
    private lateinit var cbCat3Q3A1: MaterialCheckBox
    private lateinit var cbCat3Q3A2: MaterialCheckBox
    private lateinit var cbCat3Q3A3: MaterialCheckBox
    private lateinit var cbCat3Q4A1: MaterialCheckBox
    private lateinit var cbCat3Q4A2: MaterialCheckBox
    private lateinit var cbCat3Q4A3: MaterialCheckBox
    private lateinit var cbCat3Q5A1: MaterialCheckBox
    private lateinit var cbCat3Q5A2: MaterialCheckBox
    private lateinit var cbCat3Q5A3: MaterialCheckBox
    private lateinit var cbCat3Q6A1: MaterialCheckBox
    private lateinit var cbCat3Q6A2: MaterialCheckBox
    private lateinit var cbCat3Q6A3: MaterialCheckBox
    private lateinit var cbCat3Q7A1: MaterialCheckBox
    private lateinit var cbCat3Q7A2: MaterialCheckBox
    private lateinit var cbCat3Q7A3: MaterialCheckBox
    private lateinit var cbCat3Q8A1: MaterialCheckBox
    private lateinit var cbCat3Q8A2: MaterialCheckBox
    private lateinit var cbCat3Q8A3: MaterialCheckBox
    private lateinit var cbCat3Q9A1: MaterialCheckBox
    private lateinit var cbCat3Q9A2: MaterialCheckBox
    private lateinit var cbCat3Q9A3: MaterialCheckBox
    private lateinit var cbCat3Q10A1: MaterialCheckBox
    private lateinit var cbCat3Q10A2: MaterialCheckBox
    private lateinit var cbCat3Q10A3: MaterialCheckBox
    private lateinit var btnSubmit: MaterialButton
    private var checkedItems = mutableListOf<String>()
    private var checkboxPositions = mutableListOf<String>()
    private lateinit var assessmentViewModel: AssessmentViewModel
    private var copiedID = ""
    private var docId = ""
    private lateinit var userId: String
    private lateinit var loading: ProgressDialog
    // Add a flag to check if the document has been copied
    private var isDocumentCopied = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_assessment, container, false)

        initViews()
        init()

        return view
    }

    private fun initViews() {
        cbCatA1 = view.findViewById(R.id.cb_cat1_a1)
        cbCatA2 = view.findViewById(R.id.cb_cat1_a2)
        cbCatA3 = view.findViewById(R.id.cb_cat1_a3)
        cbCatA4 = view.findViewById(R.id.cb_cat1_a4)
        cbCat2A1 = view.findViewById(R.id.cb_cat2_a1)
        cbCat2A2 = view.findViewById(R.id.cb_cat2_a2)
        cbCat2A3 = view.findViewById(R.id.cb_cat2_a3)
        cbCat2A4 = view.findViewById(R.id.cb_cat2_a4)
        cbCat3Q1A1 = view.findViewById(R.id.cb_cat3_q1_a1)
        cbCat3Q1A2 = view.findViewById(R.id.cb_cat3_q1_a2)
        cbCat3Q1A3 = view.findViewById(R.id.cb_cat3_q1_a3)
        cbCat3Q2A1 = view.findViewById(R.id.cb_cat3_q2_a1)
        cbCat3Q2A2 = view.findViewById(R.id.cb_cat3_q2_a2)
        cbCat3Q2A3 = view.findViewById(R.id.cb_cat3_q2_a3)
        cbCat3Q3A1 = view.findViewById(R.id.cb_cat3_q3_a1)
        cbCat3Q3A2 = view.findViewById(R.id.cb_cat3_q3_a2)
        cbCat3Q3A3 = view.findViewById(R.id.cb_cat3_q3_a3)
        cbCat3Q4A1 = view.findViewById(R.id.cb_cat3_q4_a1)
        cbCat3Q4A2 = view.findViewById(R.id.cb_cat3_q4_a2)
        cbCat3Q4A3 = view.findViewById(R.id.cb_cat3_q4_a3)
        cbCat3Q5A1 = view.findViewById(R.id.cb_cat3_q5_a1)
        cbCat3Q5A2 = view.findViewById(R.id.cb_cat3_q5_a2)
        cbCat3Q5A3 = view.findViewById(R.id.cb_cat3_q5_a3)
        cbCat3Q6A1 = view.findViewById(R.id.cb_cat3_q6_a1)
        cbCat3Q6A2 = view.findViewById(R.id.cb_cat3_q6_a2)
        cbCat3Q6A3 = view.findViewById(R.id.cb_cat3_q6_a3)
        cbCat3Q7A1 = view.findViewById(R.id.cb_cat3_q7_a1)
        cbCat3Q7A2 = view.findViewById(R.id.cb_cat3_q7_a2)
        cbCat3Q7A3 = view.findViewById(R.id.cb_cat3_q7_a3)
        cbCat3Q8A1 = view.findViewById(R.id.cb_cat3_q8_a1)
        cbCat3Q8A2 = view.findViewById(R.id.cb_cat3_q8_a2)
        cbCat3Q8A3 = view.findViewById(R.id.cb_cat3_q8_a3)
        cbCat3Q9A1 = view.findViewById(R.id.cb_cat3_q9_a1)
        cbCat3Q9A2 = view.findViewById(R.id.cb_cat3_q9_a2)
        cbCat3Q9A3 = view.findViewById(R.id.cb_cat3_q9_a3)
        cbCat3Q10A1 = view.findViewById(R.id.cb_cat3_q10_a1)
        cbCat3Q10A2 = view.findViewById(R.id.cb_cat3_q10_a2)
        cbCat3Q10A3 = view.findViewById(R.id.cb_cat3_q10_a3)
        btnSubmit = view.findViewById(R.id.btn_submit)
    }

    private fun init() {
        loading = ProgressDialog(requireContext())
        loading.setMessage("Please wait...")

        // Retrieve the arguments in onCreateView if needed
        arguments?.let {
            userId = it.getString("userId").toString()
        }

        assessmentViewModel = ViewModelProvider(this)[AssessmentViewModel::class.java]

        for(i in 1 until 39) {
            checkboxPositions.add("q$i ")
        }

        assessmentViewModel.token.observe(viewLifecycleOwner) { result ->
            result.onSuccess { token ->
                AuthTokenManager.accessToken = token
            }
        }

        // Observe the copyResult and set the flag once the copy is successful
        assessmentViewModel.copyResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { driveFile ->
                copiedID = driveFile.id
                isDocumentCopied = true // Set the flag to true after copying
            }

            result.onFailure { exception ->
                Log.e("DocsClient", "Failed to copy document: ${exception.message}")
            }
        }

        // Check if the document has already been copied
        if (!isDocumentCopied) {
            // Call copyTemplate only if the document hasn't been copied
            assessmentViewModel.copyTemplate(
                requireContext(),
                templateFileId = COPY_DOC_ID,
                copyRequest = CopyRequest(name = generateRandomDocumentName(), parents = listOf(FOLDER_ID))
            )
        }

        setUpCB()

        setUpCheckboxListeners()

        btnSubmit.setOnClickListener {
            loading.show()
            lifecycleScope.launch {
                assessmentViewModel.updateGoogleDocCheckBox(checkedItems, checkboxPositions, copiedID)
            }
        }

        assessmentViewModel.response.observe(viewLifecycleOwner) { result ->
            result.onSuccess { res ->
                docId = res.docId!!
                assessmentViewModel.fileName.observe(viewLifecycleOwner) { filename ->
                    sendNotification(userId, docId, filename)
                    assessmentViewModel.setAssessment(userId, docId, filename!!)
                }
            }
        }

        assessmentViewModel.isSubmitted.observe(viewLifecycleOwner) { result ->
            result.onSuccess { pair ->
                val isSuccess = pair.first
                val message = pair.second
                if(isSuccess) {
                    loading.dismiss()
                    Utility.showAlertDialog(
                        requireContext(),
                        layoutInflater,
                        "Success",
                        message,
                        "Ok"
                    ) {
                        clearCheckBox()
                    }
                } else {
                    loading.dismiss()
                    Utility.showAlertDialog(
                        requireContext(),
                        layoutInflater,
                        "Error",
                        message,
                        "Ok"
                    ) {}
                }
            }
        }

    }

    private fun sendNotification(userId: String, docId: String, filename: String?) {
        Utils.getUser(userId) { user ->
            when(user) {
                is Users -> {
                    val municipality = user.municipality
                    Log.d("MUNICIPALITY", municipality!!)
                    Utils.getUserAdmin(municipality) { adminList ->
                        if(adminList != null) {
                            for(admin in adminList) {
                                when(admin) {
                                    is StaffModel -> {
                                        val adminId = admin.staffId
                                        Log.d("MUNICIPALITY_ID_STAFF", adminId)
                                        Utils.sendNotification(
                                            adminId,
                                            "${user.userId}",
                                            "${user.userId} sent assessment.\nfilename: $filename\ndocument id: $docId",
                                            "assessment",
                                            requireContext()
                                        )
                                    }
                                    is Users -> {
                                        val adminId = admin.userId
                                        Log.d("MUNICIPALITY_ID", adminId!!)
                                        Utils.sendNotification(
                                            adminId,
                                            "${user.userId}",
                                            "${user.userId} sent assessment.\nfilename: $filename\ndocument id: $docId",
                                            "assessment",
                                            requireContext()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setUpCB() {
        cbCatA1.text = cat1QArray[0]
        cbCatA2.text = cat1QArray[1]
        cbCatA3.text = cat1QArray[2]
        cbCatA4.text = cat1QArray[3]

        cbCat2A1.text = cat2QArray[0]
        cbCat2A2.text = cat2QArray[1]
        cbCat2A3.text = cat2QArray[2]
        cbCat2A4.text = cat2QArray[3]

        cbCat3Q1A1.text = cat3QArray[0]
        cbCat3Q1A2.text = cat3QArray[1]
        cbCat3Q1A3.text = cat3QArray[2]

        cbCat3Q2A1.text = cat3QArray[0]
        cbCat3Q2A2.text = cat3QArray[1]
        cbCat3Q2A3.text = cat3QArray[2]

        cbCat3Q3A1.text = cat3QArray[0]
        cbCat3Q3A2.text = cat3QArray[1]
        cbCat3Q3A3.text = cat3QArray[2]

        cbCat3Q4A1.text = cat3QArray[0]
        cbCat3Q4A2.text = cat3QArray[1]
        cbCat3Q4A3.text = cat3QArray[2]

        cbCat3Q5A1.text = cat3QArray[0]
        cbCat3Q5A2.text = cat3QArray[1]
        cbCat3Q5A3.text = cat3QArray[2]

        cbCat3Q6A1.text = cat3QArray[0]
        cbCat3Q6A2.text = cat3QArray[1]
        cbCat3Q6A3.text = cat3QArray[2]

        cbCat3Q7A1.text = cat3QArray[0]
        cbCat3Q7A2.text = cat3QArray[1]
        cbCat3Q7A3.text = cat3QArray[2]

        cbCat3Q8A1.text = cat3QArray[0]
        cbCat3Q8A2.text = cat3QArray[1]
        cbCat3Q8A3.text = cat3QArray[2]

        cbCat3Q9A1.text = cat3QArray[0]
        cbCat3Q9A2.text = cat3QArray[1]
        cbCat3Q9A3.text = cat3QArray[2]

        cbCat3Q10A1.text = cat3QArray[0]
        cbCat3Q10A2.text = cat3QArray[1]
        cbCat3Q10A3.text = cat3QArray[2]
    }

    private fun clearCheckBox() {
        cbCatA1.isChecked = false
        cbCatA2.isChecked = false
        cbCatA3.isChecked = false
        cbCatA4.isChecked = false

        cbCat2A1.isChecked = false
        cbCat2A2.isChecked = false
        cbCat2A3.isChecked = false
        cbCat2A4.isChecked = false

        cbCat3Q1A1.isChecked = false
        cbCat3Q1A2.isChecked = false
        cbCat3Q1A3.isChecked = false

        cbCat3Q2A1.isChecked = false
        cbCat3Q2A2.isChecked = false
        cbCat3Q2A3.isChecked = false

        cbCat3Q3A1.isChecked = false
        cbCat3Q3A2.isChecked = false
        cbCat3Q3A3.isChecked = false

        cbCat3Q4A1.isChecked = false
        cbCat3Q4A2.isChecked = false
        cbCat3Q4A3.isChecked = false

        cbCat3Q5A1.isChecked = false
        cbCat3Q5A2.isChecked = false
        cbCat3Q5A3.isChecked = false

        cbCat3Q6A1.isChecked = false
        cbCat3Q6A2.isChecked = false
        cbCat3Q6A3.isChecked = false

        cbCat3Q7A1.isChecked = false
        cbCat3Q7A2.isChecked = false
        cbCat3Q7A3.isChecked = false

        cbCat3Q8A1.isChecked = false
        cbCat3Q8A2.isChecked = false
        cbCat3Q8A3.isChecked = false

        cbCat3Q9A1.isChecked = false
        cbCat3Q9A2.isChecked = false
        cbCat3Q9A3.isChecked = false

        cbCat3Q10A1.isChecked = false
        cbCat3Q10A2.isChecked = false
        cbCat3Q10A3.isChecked = false
    }

    private fun setUpCheckboxListeners() {
        cbCatA1.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCatA2.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCatA3.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCatA4.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }

        cbCat2A1.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat2A2.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat2A3.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat2A4.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }

        cbCat3Q1A1.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q1A2.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q1A3.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }

        cbCat3Q2A1.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q2A2.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q2A3.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }

        cbCat3Q3A1.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q3A2.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q3A3.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }

        cbCat3Q4A1.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q4A2.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q4A3.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }

        cbCat3Q5A1.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q5A2.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q5A3.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }

        cbCat3Q6A1.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q6A2.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q6A3.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }

        cbCat3Q7A1.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q7A2.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q7A3.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }

        cbCat3Q8A1.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q8A2.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q8A3.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }

        cbCat3Q9A1.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q9A2.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q9A3.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }

        cbCat3Q10A1.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q10A2.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
        cbCat3Q10A3.setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
    }

    private fun updateCheckboxes() {
        if (cbCatA1.isChecked) checkedItems.add(checkboxPositions[0])
        if (cbCatA2.isChecked) checkedItems.add(checkboxPositions[1])
        if (cbCatA3.isChecked) checkedItems.add(checkboxPositions[2])
        if (cbCatA4.isChecked) checkedItems.add(checkboxPositions[3])

        if (cbCat2A1.isChecked) checkedItems.add(checkboxPositions[4])
        if (cbCat2A2.isChecked) checkedItems.add(checkboxPositions[5])
        if (cbCat2A3.isChecked) checkedItems.add(checkboxPositions[6])
        if (cbCat2A4.isChecked) checkedItems.add(checkboxPositions[7])

        if (cbCat3Q1A1.isChecked) checkedItems.add(checkboxPositions[8])
        if (cbCat3Q1A2.isChecked) checkedItems.add(checkboxPositions[9])
        if (cbCat3Q1A3.isChecked) checkedItems.add(checkboxPositions[10])

        if (cbCat3Q2A1.isChecked) checkedItems.add(checkboxPositions[11])
        if (cbCat3Q2A2.isChecked) checkedItems.add(checkboxPositions[12])
        if (cbCat3Q2A3.isChecked) checkedItems.add(checkboxPositions[13])

        if (cbCat3Q3A1.isChecked) checkedItems.add(checkboxPositions[14])
        if (cbCat3Q3A2.isChecked) checkedItems.add(checkboxPositions[15])
        if (cbCat3Q3A3.isChecked) checkedItems.add(checkboxPositions[16])

        if (cbCat3Q4A1.isChecked) checkedItems.add(checkboxPositions[17])
        if (cbCat3Q4A2.isChecked) checkedItems.add(checkboxPositions[18])
        if (cbCat3Q4A3.isChecked) checkedItems.add(checkboxPositions[19])

        if (cbCat3Q5A1.isChecked) checkedItems.add(checkboxPositions[20])
        if (cbCat3Q5A2.isChecked) checkedItems.add(checkboxPositions[21])
        if (cbCat3Q5A3.isChecked) checkedItems.add(checkboxPositions[22])

        if (cbCat3Q6A1.isChecked) checkedItems.add(checkboxPositions[23])
        if (cbCat3Q6A2.isChecked) checkedItems.add(checkboxPositions[24])
        if (cbCat3Q6A3.isChecked) checkedItems.add(checkboxPositions[25])

        if (cbCat3Q7A1.isChecked) checkedItems.add(checkboxPositions[26])
        if (cbCat3Q7A2.isChecked) checkedItems.add(checkboxPositions[27])
        if (cbCat3Q7A3.isChecked) checkedItems.add(checkboxPositions[28])

        if (cbCat3Q8A1.isChecked) checkedItems.add(checkboxPositions[29])
        if (cbCat3Q8A2.isChecked) checkedItems.add(checkboxPositions[30])
        if (cbCat3Q8A3.isChecked) checkedItems.add(checkboxPositions[31])

        if (cbCat3Q9A1.isChecked) checkedItems.add(checkboxPositions[32])
        if (cbCat3Q9A2.isChecked) checkedItems.add(checkboxPositions[33])
        if (cbCat3Q9A3.isChecked) checkedItems.add(checkboxPositions[34])

        if (cbCat3Q10A1.isChecked) checkedItems.add(checkboxPositions[35])
        if (cbCat3Q10A2.isChecked) checkedItems.add(checkboxPositions[36])
        if (cbCat3Q10A3.isChecked) checkedItems.add(checkboxPositions[37])
    }

}