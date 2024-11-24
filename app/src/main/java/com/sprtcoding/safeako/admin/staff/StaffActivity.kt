package com.sprtcoding.safeako.admin.staff

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.admin.staff.viewmodel.StaffViewModel
import com.sprtcoding.safeako.databinding.ActivityStaffBinding
import com.sprtcoding.safeako.utils.Constants.avatarList
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.Utility.showAvatarPickerDialog
import de.hdodenhof.circleimageview.CircleImageView

class StaffActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStaffBinding

    private lateinit var avatar: CircleImageView
    private lateinit var btnSelectAvatar: MaterialButton
    private lateinit var etName: TextInputEditText
    private lateinit var etJobDesc: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var tvChangePass: TextView
    private lateinit var btnUpdate: MaterialButton
    private lateinit var btnBack: ImageButton

    private lateinit var staffViewModel: StaffViewModel
    private lateinit var loading: ProgressDialog

    private var staffId: String? = null
    private var staffName: String? = null
    private var staffJob: String? = null
    private var staffPhone: String? = null
    private var selectedAvatar: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Inflate the layout using ViewBinding
        binding = ActivityStaffBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left + 60, systemBars.top + 60, systemBars.right + 60, systemBars.bottom + 60)
            insets
        }

        initViews()
        init()
        afterInit()
    }

    private fun initViews() {
        btnSelectAvatar = binding.btnOpenCamera
        avatar = binding.avatar
        btnUpdate = binding.btnSave
        btnBack = binding.btnBack
        etName = binding.etName
        etJobDesc = binding.etJobDesc
        etPhone = binding.etPhone
        tvChangePass = binding.tvChangePassword
    }

    private fun init() {
        staffId = intent.getStringExtra("STAFF_ID")
        staffName = intent.getStringExtra("STAFF_NAME")
        staffJob = intent.getStringExtra("STAFF_JOB")
        staffPhone = intent.getStringExtra("STAFF_PHONE")

        Utility.getAvatar(staffId!!, avatar)
        etName.setText(staffName)
        etJobDesc.setText(staffJob)
        etPhone.setText(staffPhone)

        loading = ProgressDialog(this)

        staffViewModel = ViewModelProvider(this)[StaffViewModel::class.java]

        etPhone.addTextChangedListener(object : TextWatcher {
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
                        etPhone.setText(formattedNumber)
                        etPhone.setSelection(formattedNumber.length)
                    } else {
                        etPhone.error = "Invalid phone number!"
                    }
                } catch (e: NumberParseException) {
                    Log.d("TAG", "Error parsing phone number: $e")
                }
            }
        })
    }

    private fun afterInit() {
        btnBack.setOnClickListener {
            finish()
        }

        tvChangePass.setOnClickListener {
            Utility.showChangePasswordDialog(this, layoutInflater, staffId!!, staffViewModel, loading)
        }

        btnSelectAvatar.setOnClickListener {
            // Check for storage permissions
            showAvatarPickerDialog(this, avatarList, avatar) { selectedAvatar ->
                this.selectedAvatar = selectedAvatar
            }
        }

        btnUpdate.setOnClickListener {
            loading.show()
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

            val name = etName.text.toString()
            val job = etJobDesc.text.toString()
            val phone = etPhone.text.toString()

            if(name.isNotEmpty() && job.isNotEmpty() && phone.isNotEmpty()) {
                val staff = if(selectedAvatar != null) {
                    mapOf<String, String>(
                        "fullName" to name,
                        "jobDesc" to job,
                        "phone" to phone,
                        "avatar" to avatarName
                    )
                } else {
                    mapOf<String, String>(
                        "fullName" to name,
                        "jobDesc" to job,
                        "phone" to phone
                    )
                }

                staffViewModel.updateStaff(staffId!!, staff)
            } else {
                loading.dismiss()
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Update Staff",
                    "Empty fields!",
                    "Ok"
                ) {

                }
            }
        }

        observer()
    }

    private fun observer() {
        staffViewModel.isPasswordUpdate.observe(this) { isPasswordUpdated ->
            if(isPasswordUpdated) {
                loading.dismiss()
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Change Password",
                    "Password change successfully!",
                    "Ok"
                ) {
                    finish()
                }
            } else {
                loading.dismiss()
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Change Password",
                    "Failed to change password!",
                    "Ok"
                ) {
                    return@showAlertDialog
                }
            }
        }

        staffViewModel.isUpdate.observe(this) { isPasswordUpdated ->
            if(isPasswordUpdated) {
                loading.dismiss()
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Staff",
                    "Staff profile updated!",
                    "Ok"
                ) {
                    finish()
                }
            } else {
                loading.dismiss()
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Staff",
                    "Failed to update staff!",
                    "Ok"
                ) {
                    return@showAlertDialog
                }
            }
        }
    }
}