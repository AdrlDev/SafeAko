package com.sprtcoding.safeako.admin.staff

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.setSelection
import androidx.compose.ui.semantics.setText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.model.StaffModel
import com.sprtcoding.safeako.model.Users
import com.sprtcoding.safeako.user.activity.user_avatar.viewmodel.AvatarViewModel
import com.sprtcoding.safeako.utils.Constants.avatarList
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.Utility.showAvatarPickerDialog
import de.hdodenhof.circleimageview.CircleImageView

class AddStaff : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var avatar: CircleImageView
    private lateinit var chooseAvatarBtn: MaterialButton
    private lateinit var saveBtn: MaterialButton
    private lateinit var nameEt: TextInputEditText
    private lateinit var jobDescEt: TextInputEditText
    private lateinit var phoneEt: TextInputEditText
    private lateinit var passwordEt: TextInputEditText
    private lateinit var avatarViewModel: AvatarViewModel
    private lateinit var loadingDialog: ProgressDialog
    private var selectedAvatar: Int? = null
    private var myId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_staff)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left + 50, systemBars.top + 50, systemBars.right + 50, systemBars.bottom + 50)
            insets
        }

        initViews()
        init()
        afterInit()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back)
        avatar = findViewById(R.id.avatar)
        chooseAvatarBtn = findViewById(R.id.btn_open_camera)
        saveBtn = findViewById(R.id.btn_save)
        nameEt = findViewById(R.id.et_name)
        jobDescEt = findViewById(R.id.et_job_desc)
        phoneEt = findViewById(R.id.et_phone)
        passwordEt = findViewById(R.id.et_password)
    }

    private fun init() {
        myId = intent.getStringExtra("UID")
        loadingDialog = ProgressDialog(this)
        loadingDialog.setTitle("Saving Staff")
        loadingDialog.setMessage("Please wait...")

        avatarViewModel = ViewModelProvider(this)[AvatarViewModel::class.java]

        avatarViewModel.getUser(myId!!)

        phoneEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    return
                }

                if (s.toString().startsWith("+63")) {
                    // Country code is already present, do nothing
                    return
                }

                try {
                    val phoneNumberUtil = PhoneNumberUtil.getInstance()
                    val number = phoneNumberUtil.parse(s.toString(), "PH") // PH for Philippines

                    if (phoneNumberUtil.isValidNumber(number)) {
                        val formattedNumber = phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
                        phoneEt.setText(formattedNumber)
                        phoneEt.setSelection(formattedNumber.length)
                    } else {
                        phoneEt.error = "Invalid phone number!"
                    }
                } catch (e: NumberParseException) {
                    Log.d("TAG", "Error parsing phone number: $e")
                }
            }
        })
    }

    private fun afterInit() {
        chooseAvatarBtn.setOnClickListener {
            // Check for storage permissions
            showAvatarPickerDialog(this, avatarList, avatar) { selectedAvatar ->
                this.selectedAvatar = selectedAvatar
            }
        }

        saveBtn.setOnClickListener {
            loadingDialog.show()

            val name = nameEt.text.toString()
            val jobDesc = jobDescEt.text.toString()
            val phone = phoneEt.text.toString()
            val password = passwordEt.text.toString()
            val encryptedPass = Utility.encryptPassword(passwordEt.text.toString())

            // Convert resource ID to a string name to store in Firestore (e.g., "avatar1", "avatar2", etc.)
            val avatarName = when (selectedAvatar) {
                R.drawable.avatar_1 -> "avatar1"
                R.drawable.avatar_2 -> "avatar2"
                R.drawable.avatar_3 -> "avatar3"
                R.drawable.avatar_4 -> "avatar4"
                R.drawable.avatar_5 -> "avatar5"
                R.drawable.avatar_6 -> "avatar6"
                R.drawable.avatar_7 -> "avatar7"
                R.drawable.avatar_8 -> "avatar8"
                R.drawable.avatar_9 -> "avatar9"
                R.drawable.avatar_10 -> "avatar10"
                R.drawable.avatar_11 -> "avatar11"
                R.drawable.avatar_12 -> "avatar12"
                else -> "default_avatar"
            }

            if(name.isNotEmpty() && jobDesc.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {

                avatarViewModel.user.observe(this) { user ->
                    val data = when(user) {
                        is Users -> {
                            StaffModel(
                                Utility.generateStaffId(),
                                myId!!,
                                avatarName,
                                user.displayName!!,
                                name,
                                jobDesc,
                                phone,
                                encryptedPass,
                                "Staff",
                                "offline"
                            )
                        } else -> {
                            null
                        }
                    }

                    avatarViewModel.setStaff(data)
                }

            } else {
                Utility.showAlertDialog(this, layoutInflater, "Add Staff", "Please fill required fields.",
                    "Ok") {
                    nameEt.requestFocus()
                    return@showAlertDialog
                }
            }
        }

        avatarViewModel.isStaffAddSuccess.observe(this) { result ->
            result.onSuccess { pair ->
                val success = pair.first
                val uid = pair.second
                val message = "$uid $success"

                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Add Staff Success",
                    message,
                    "Ok"
                ){finish()}
            }
            result.onFailure { err ->
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Add Staff Failed",
                    err.message!!,
                    "Ok"
                ){}
            }
        }

        btnBack.setOnClickListener { finish() }
    }
}