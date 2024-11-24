package com.sprtcoding.safeako.user.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.api.google_docs_api.AuthTokenManager
import com.sprtcoding.safeako.api.google_docs_api.model.CopyRequest
import com.sprtcoding.safeako.firebase.firebaseUtils.AssessmentQuestionsCallback
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils.getAssessmentQuestions
import com.sprtcoding.safeako.model.AnswerOption
import com.sprtcoding.safeako.model.AssessmentQuestion
import com.sprtcoding.safeako.model.StaffModel
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.fragment.adapter.AnswerAdapter
import com.sprtcoding.safeako.user.fragment.adapter.AnswerAdapter.CheckboxCallback
import com.sprtcoding.safeako.user.fragment.viewmodel.AssessmentViewModel
import com.sprtcoding.safeako.utils.Constants.COPY_DOC_ID
import com.sprtcoding.safeako.utils.Constants.FOLDER_ID
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.Utility.generateRandomDocumentName
import kotlinx.coroutines.launch

class NewAssessmentFragment : Fragment(), CheckboxCallback {
    private lateinit var view: View
    private lateinit var btnSubmit: MaterialButton
    private lateinit var tvQuestion: TextView
    private lateinit var rvAnswers: RecyclerView
    private lateinit var tvQuestion2: TextView
    private lateinit var rvAnswers2: RecyclerView
    private lateinit var tvQuestion3: TextView
    private lateinit var rvAnswers3: RecyclerView
    private lateinit var tvQuestion4: TextView
    private lateinit var rvAnswers4: RecyclerView
    private lateinit var tvQuestion5: TextView
    private lateinit var rvAnswers5: RecyclerView
    private lateinit var tvQuestion6: TextView
    private lateinit var rvAnswers6: RecyclerView
    private lateinit var tvQuestion7: TextView
    private lateinit var rvAnswers7: RecyclerView
    private lateinit var tvQuestion8: TextView
    private lateinit var rvAnswers8: RecyclerView
    private lateinit var tvQuestion9: TextView
    private lateinit var rvAnswers9: RecyclerView
    private lateinit var tvQuestion10: TextView
    private lateinit var rvAnswers10: RecyclerView
    private lateinit var tvQuestion11: TextView
    private lateinit var rvAnswers11: RecyclerView
    private lateinit var tvQuestion12: TextView
    private lateinit var rvAnswers12: RecyclerView
    private lateinit var tvQuestion13: TextView
    private lateinit var rvAnswers13: RecyclerView
    private lateinit var tvQuestion14: TextView
    private lateinit var rvAnswers14: RecyclerView
    private lateinit var assessmentViewModel: AssessmentViewModel
    private var checkedItems = mutableListOf<String>()
    private var checkboxPositions = mutableListOf<String>()
    private var selectedAnswers = mutableListOf<String>()
    private var copiedID = ""
    private var docId = ""
    private lateinit var userId: String
    private lateinit var loading: ProgressDialog
    // Add a flag to check if the document has been copied
    private var isDocumentCopied = false
    private lateinit var answerAdapter: AnswerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_assessment, container, false)

        initViews()
        afterInit()

        return view
    }

    private fun initViews() {
        btnSubmit = view.findViewById(R.id.btn_submit)
        tvQuestion = view.findViewById(R.id.tv_question)
        rvAnswers = view.findViewById(R.id.rv_answers)
        tvQuestion2 = view.findViewById(R.id.tv_question2)
        rvAnswers2 = view.findViewById(R.id.rv_answers2)
        tvQuestion3 = view.findViewById(R.id.tv_question3)
        rvAnswers3 = view.findViewById(R.id.rv_answers3)
        tvQuestion4 = view.findViewById(R.id.tv_question4)
        rvAnswers4= view.findViewById(R.id.rv_answers4)
        tvQuestion5= view.findViewById(R.id.tv_question5)
        rvAnswers5 = view.findViewById(R.id.rv_answers5)
        tvQuestion6 = view.findViewById(R.id.tv_question6)
        rvAnswers6 = view.findViewById(R.id.rv_answers6)
        tvQuestion7 = view.findViewById(R.id.tv_question7)
        rvAnswers7 = view.findViewById(R.id.rv_answers7)
        tvQuestion8 = view.findViewById(R.id.tv_question8)
        rvAnswers8 = view.findViewById(R.id.rv_answers8)
        tvQuestion9 = view.findViewById(R.id.tv_question9)
        rvAnswers9 = view.findViewById(R.id.rv_answers9)
        tvQuestion10 = view.findViewById(R.id.tv_question10)
        rvAnswers10 = view.findViewById(R.id.rv_answers10)
        tvQuestion11 = view.findViewById(R.id.tv_question11)
        rvAnswers11 = view.findViewById(R.id.rv_answers11)
        tvQuestion12 = view.findViewById(R.id.tv_question12)
        rvAnswers12 = view.findViewById(R.id.rv_answers12)
        tvQuestion13 = view.findViewById(R.id.tv_question13)
        rvAnswers13 = view.findViewById(R.id.rv_answers13)
        tvQuestion14 = view.findViewById(R.id.tv_question14)
        rvAnswers14 = view.findViewById(R.id.rv_answers14)
    }

    private fun afterInit() {
        assessmentViewModel = ViewModelProvider(this)[AssessmentViewModel::class.java]
        answerAdapter = AnswerAdapter(emptyList(), this)

        loading = ProgressDialog(requireContext())
        loading.setMessage("Please wait...")

        // Retrieve the arguments in onCreateView if needed
        arguments?.let {
            userId = it.getString("userId").toString()
        }

        for(i in 1 until 51) {
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
                    assessmentViewModel.setAssessment(userId, docId, filename!!, selectedAnswers)
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
                        answerAdapter.resetCheckboxes()
                        btnSubmit.isEnabled = false
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

        loadData()
    }

    private fun loadData() {
        getAssessmentQuestions(object : AssessmentQuestionsCallback {
            override fun onSuccess(assessmentQuestions: AssessmentQuestion) {

                for(i in 0 until 14) {
                    setViewToAdapter(
                        getTextViewById(i),
                        getAnswerList(i, assessmentQuestions),
                        getRecyclerViewById(i),
                        getQuestions(i, assessmentQuestions)!!
                    )
                }
            }

            private fun setViewToAdapter(
                tv: TextView,
                options: List<AnswerOption>,
                rv: RecyclerView,
                question: String
            ) {
                // Debugging: Log all available options
                options.forEach { option ->
                    Log.d("OPTIONS", option.answerText)
                }
                tv.text = question
                answerAdapter = AnswerAdapter(options, this@NewAssessmentFragment)
                rv.layoutManager = LinearLayoutManager(requireContext())
                rv.adapter = answerAdapter
            }

            override fun onFailure(exception: Exception) {
                Utility.showAlertDialog(
                    requireContext(),
                    layoutInflater,
                    "Error",
                    exception.message!!,
                    "Ok"
                ){}
            }
        })
    }

    private fun getAnswerList(questionId: Int, assessmentQuestions: AssessmentQuestion): List<AnswerOption> {
        return when (questionId) {
            0 -> listOf(
                AnswerOption(assessmentQuestions.gender.q!!, assessmentQuestions.gender.a1 ?: "", 0),
                AnswerOption(assessmentQuestions.gender.q!!, assessmentQuestions.gender.a2 ?: "", 1)
            )
            1 -> listOf(
                AnswerOption(assessmentQuestions.relationships.q!!, assessmentQuestions.relationships.a1 ?: "", 2),
                AnswerOption(assessmentQuestions.relationships.q!!, assessmentQuestions.relationships.a2 ?: "", 3),
                AnswerOption(assessmentQuestions.relationships.q!!, assessmentQuestions.relationships.a3 ?: "", 4)
            )
            2 -> listOf(
                AnswerOption(assessmentQuestions.education.q!!, assessmentQuestions.education.a1 ?: "", 5),
                AnswerOption(assessmentQuestions.education.q!!, assessmentQuestions.education.a2 ?: "", 6),
                AnswerOption(assessmentQuestions.education.q!!, assessmentQuestions.education.a3 ?: "", 7),
                AnswerOption(assessmentQuestions.education.q!!, assessmentQuestions.education.a4 ?: "", 8),
                AnswerOption(assessmentQuestions.education.q!!, assessmentQuestions.education.a5 ?: "", 9),
                AnswerOption(assessmentQuestions.education.q!!, assessmentQuestions.education.a6 ?: "", 10),
                AnswerOption(assessmentQuestions.education.q!!, assessmentQuestions.education.a7 ?: "", 11)
            )
            3 -> listOf(
                AnswerOption(assessmentQuestions.interest.q!!, assessmentQuestions.interest.a1 ?: "", 12),
                AnswerOption(assessmentQuestions.interest.q!!, assessmentQuestions.interest.a2 ?: "", 13),
                AnswerOption(assessmentQuestions.interest.q!!, assessmentQuestions.interest.a3 ?: "", 14),
                AnswerOption(assessmentQuestions.interest.q!!, assessmentQuestions.interest.a4 ?: "", 15)
            )
            4 -> listOf(
                AnswerOption(assessmentQuestions.birthMother.q!!, assessmentQuestions.birthMother.a1 ?: "", 16),
                AnswerOption(assessmentQuestions.birthMother.q!!, assessmentQuestions.birthMother.a2 ?: "", 17),
                AnswerOption(assessmentQuestions.birthMother.q!!, assessmentQuestions.birthMother.a3 ?: "", 18)
            )
            5 -> listOf(
                AnswerOption(assessmentQuestions.sexualHistory.q!!, assessmentQuestions.sexualHistory.a1 ?: "", 19),
                AnswerOption(assessmentQuestions.sexualHistory.q!!, assessmentQuestions.sexualHistory.a2 ?: "", 20),
                AnswerOption(assessmentQuestions.sexualHistory.q!!, assessmentQuestions.sexualHistory.a3 ?: "", 21)
            )
            6 -> listOf(
                AnswerOption(assessmentQuestions.sexual2.q!!, assessmentQuestions.sexual2.a1 ?: "", 22),
                AnswerOption(assessmentQuestions.sexual2.q!!, assessmentQuestions.sexual2.a2 ?: "", 23),
                AnswerOption(assessmentQuestions.sexual2.q!!, assessmentQuestions.sexual2.a3 ?: "", 24)
            )
            7 -> listOf(
                AnswerOption(assessmentQuestions.sexual3.q!!, assessmentQuestions.sexual3.a1 ?: "", 25),
                AnswerOption(assessmentQuestions.sexual3.q!!, assessmentQuestions.sexual3.a2 ?: "", 26),
                AnswerOption(assessmentQuestions.sexual3.q!!, assessmentQuestions.sexual3.a3 ?: "", 27)
            )
            8 -> listOf(
                AnswerOption(assessmentQuestions.sexual4.q!!, assessmentQuestions.sexual4.a1 ?: "", 28),
                AnswerOption(assessmentQuestions.sexual4.q!!, assessmentQuestions.sexual4.a2 ?: "", 29),
                AnswerOption(assessmentQuestions.sexual4.q!!, assessmentQuestions.sexual4.a3 ?: "", 30)
            )
            9 -> listOf(
                AnswerOption(assessmentQuestions.sexual5.q!!, assessmentQuestions.sexual5.a1 ?: "", 31),
                AnswerOption(assessmentQuestions.sexual5.q!!, assessmentQuestions.sexual5.a2 ?: "", 32),
                AnswerOption(assessmentQuestions.sexual5.q!!, assessmentQuestions.sexual5.a3 ?: "", 33)
            )
            10 -> listOf(
                AnswerOption(assessmentQuestions.sexual6.q!!, assessmentQuestions.sexual6.a1 ?: "", 34),
                AnswerOption(assessmentQuestions.sexual6.q!!, assessmentQuestions.sexual6.a2 ?: "", 35),
                AnswerOption(assessmentQuestions.sexual6.q!!, assessmentQuestions.sexual6.a3 ?: "", 36)
            )
            11 -> listOf(
                AnswerOption(assessmentQuestions.sexual7.q!!, assessmentQuestions.sexual7.a1 ?: "", 37),
                AnswerOption(assessmentQuestions.sexual7.q!!, assessmentQuestions.sexual7.a2 ?: "", 38),
                AnswerOption(assessmentQuestions.sexual7.q!!, assessmentQuestions.sexual7.a3 ?: "", 39)
            )
            12 -> listOf(
                AnswerOption(assessmentQuestions.reason.q!!, assessmentQuestions.reason.a1 ?: "", 40),
                AnswerOption(assessmentQuestions.reason.q!!, assessmentQuestions.reason.a2 ?: "", 41),
                AnswerOption(assessmentQuestions.reason.q!!, assessmentQuestions.reason.a3 ?: "", 42),
                AnswerOption(assessmentQuestions.reason.q!!, assessmentQuestions.reason.a4 ?: "", 43),
                AnswerOption(assessmentQuestions.reason.q!!, assessmentQuestions.reason.a5 ?: "", 44)
            )
            13 -> listOf(
                AnswerOption(assessmentQuestions.medicalHistory.q!!, assessmentQuestions.medicalHistory.a1 ?: "", 45),
                AnswerOption(assessmentQuestions.medicalHistory.q!!, assessmentQuestions.medicalHistory.a2 ?: "", 46),
                AnswerOption(assessmentQuestions.medicalHistory.q!!, assessmentQuestions.medicalHistory.a3 ?: "", 47),
                AnswerOption(assessmentQuestions.medicalHistory.q!!, assessmentQuestions.medicalHistory.a4 ?: "", 48),
                AnswerOption(assessmentQuestions.medicalHistory.q!!, assessmentQuestions.medicalHistory.a5 ?: "", 49)
            )
            // Add all other RecyclerViews...
            else -> throw IllegalArgumentException("Invalid question ID")
        }
    }

    private fun getTextViewById(questionId: Int): TextView {
        return when (questionId) {
            0 -> tvQuestion
            1 -> tvQuestion2
            2 -> tvQuestion3
            3 -> tvQuestion4
            4 -> tvQuestion5
            5 -> tvQuestion6
            6 -> tvQuestion7
            7 -> tvQuestion8
            8 -> tvQuestion9
            9 -> tvQuestion10
            10 -> tvQuestion11
            11 -> tvQuestion12
            12 -> tvQuestion13
            13 -> tvQuestion14
            // Add all other RecyclerViews...
            else -> throw IllegalArgumentException("Invalid question ID")
        }
    }

    private fun getQuestions(questionId: Int, question: AssessmentQuestion): String? {
        return when (questionId) {
            0 -> question.gender.q
            1 -> question.relationships.q
            2 -> question.education.q
            3 -> question.interest.q
            4 -> question.birthMother.q
            5 -> question.sexualHistory.q
            6 -> question.sexual2.q
            7 -> question.sexual3.q
            8 -> question.sexual4.q
            9 -> question.sexual5.q
            10 -> question.sexual6.q
            11 -> question.sexual7.q
            12 -> question.reason.q
            13 -> question.medicalHistory.q
            // Add all other RecyclerViews...
            else -> throw IllegalArgumentException("Invalid question ID")
        }
    }

    private fun getRecyclerViewById(questionId: Int): RecyclerView {
        return when (questionId) {
            0 -> rvAnswers
            1 -> rvAnswers2
            2 -> rvAnswers3
            3 -> rvAnswers4
            4 -> rvAnswers5
            5 -> rvAnswers6
            6 -> rvAnswers7
            7 -> rvAnswers8
            8 -> rvAnswers9
            9 -> rvAnswers10
            10 -> rvAnswers11
            11 -> rvAnswers12
            12 -> rvAnswers13
            13 -> rvAnswers14
            // Add all other RecyclerViews...
            else -> throw IllegalArgumentException("Invalid question ID")
        }
    }


    override fun onCheckboxChecked(position: Int, selectedAnswers: String, q: String, isCheck: Boolean) {
        if(isCheck) {
            checkedItems.add(checkboxPositions[position])
            this.selectedAnswers.add("$q: $selectedAnswers")
        } else {
            if (checkedItems.contains(checkboxPositions[position])) {
                checkedItems.remove(checkboxPositions[position])
            }
            // Remove the selected answer with the same question index
            this.selectedAnswers.remove("$q: $selectedAnswers")
        }
        Log.d("QUESTIONS", "${this.selectedAnswers}")
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

}