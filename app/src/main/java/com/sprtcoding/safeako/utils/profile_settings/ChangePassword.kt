package com.sprtcoding.safeako.utils.profile_settings

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils
import com.sprtcoding.safeako.utils.Utility
import com.sprtcoding.safeako.utils.Utility.encryptPassword
import com.sprtcoding.safeako.utils.profile_settings.viewmodel.ProfileSettingsViewModel

class ChangePassword : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var etOldPassword: TextInputEditText
    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etConfirmNewPassword: TextInputEditText
    private lateinit var saveBtn: MaterialButton
    private lateinit var profileSettingsViewModel: ProfileSettingsViewModel
    private lateinit var loading: ProgressDialog
    private var myId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left + 60, systemBars.top + 60, systemBars.right + 60, systemBars.bottom + 60)
            insets
        }

        initViews()
        init()
        afterInit()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back)
        etOldPassword = findViewById(R.id.et_old_password)
        etNewPassword = findViewById(R.id.et_new_password)
        etConfirmNewPassword = findViewById(R.id.et_confirm_new_password)
        saveBtn = findViewById(R.id.btn_save)
    }

    private fun init() {
        myId = intent.getStringExtra("UID")

        loading = ProgressDialog(this)
        loading.setTitle("Change Password")
        loading.setMessage("Please wait...")

        profileSettingsViewModel = ViewModelProvider(this)[ProfileSettingsViewModel::class.java]

        observer()
    }

    private fun observer() {
        profileSettingsViewModel.isPasswordUpdate.observe(this) { isPasswordUpdated ->
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
    }

    private fun afterInit() {
        saveBtn.setOnClickListener {
            loading.show()

            val oldPassword = etOldPassword.text.toString()
            val newPassword = etNewPassword.text.toString()
            val confirmNewPassword = etConfirmNewPassword.text.toString()

            if(oldPassword.isEmpty()) {
                loading.dismiss()
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Change Password",
                    "Please enter your old password to continue.",
                    "Ok"
                ) {
                    etOldPassword.requestFocus()
                    return@showAlertDialog
                }
            } else if(newPassword.isEmpty()) {
                loading.dismiss()
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Change Password",
                    "Please enter your new password to continue.",
                    "Ok"
                ) {
                    etNewPassword.requestFocus()
                    return@showAlertDialog
                }
            } else if(confirmNewPassword.isEmpty()) {
                loading.dismiss()
                Utility.showAlertDialog(
                    this,
                    layoutInflater,
                    "Change Password",
                    "Please confirm your new password to continue.",
                    "Ok"
                ) {
                    etConfirmNewPassword.requestFocus()
                    return@showAlertDialog
                }
            } else {
                val encryptedOldPassword = encryptPassword(oldPassword)
                Utils.checkPassword(myId!!, encryptedOldPassword) { isPasswordCorrect ->
                    if(isPasswordCorrect) {
                        if(confirmNewPassword == newPassword) {
                            val encryptedNewPassword = encryptPassword(newPassword)

                            profileSettingsViewModel.updatePassword(myId!!, encryptedNewPassword)
                        } else {
                            loading.dismiss()
                            Utility.showAlertDialog(
                                this,
                                layoutInflater,
                                "Change Password",
                                "New password not match. please try again!",
                                "Ok"
                            ) {
                                etConfirmNewPassword.requestFocus()
                                return@showAlertDialog
                            }
                        }
                    } else {
                        loading.dismiss()
                        Utility.showAlertDialog(
                            this,
                            layoutInflater,
                            "Change Password",
                            "Your old password is incorrect. please try again!",
                            "Ok"
                        ) {
                            etOldPassword.requestFocus()
                            return@showAlertDialog
                        }
                    }
                }
            }
        }
    }
}