package com.sprtcoding.safeako.user.activity.user_avatar

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.authentication.login.LoginActivity
import com.sprtcoding.safeako.user.activity.user_avatar.viewmodel.AvatarViewModel
import com.sprtcoding.safeako.utils.Constants.avatarList
import com.sprtcoding.safeako.utils.Utility.showAlertDialogWithYesNo
import com.sprtcoding.safeako.utils.Utility.showAvatarPickerDialog
import de.hdodenhof.circleimageview.CircleImageView

class AddAvatarActivity : AppCompatActivity() {
    private lateinit var btnOpenCamera: MaterialButton
    private lateinit var avatar: CircleImageView
    private lateinit var btnSave: MaterialButton
    private lateinit var tvMunicipality: AutoCompleteTextView
    private var selectedAvatar: Int? = null
    private var userId: String? = null
    private lateinit var loadingDialog: ProgressDialog
    private lateinit var avatarViewModel: AvatarViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_avatar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        init()
        afterInit()
    }

    private fun initViews() {
        btnOpenCamera = findViewById(R.id.btn_open_camera)
        avatar = findViewById(R.id.avatar)
        btnSave = findViewById(R.id.btn_save)
        tvMunicipality = findViewById(R.id.tv_municipality)
    }

    private fun init() {
        userId = intent.getStringExtra("userId")
        loadingDialog = ProgressDialog(this)
        loadingDialog.setMessage("Uploading image...")

        avatarViewModel = ViewModelProvider(this)[AvatarViewModel::class.java]

        val municipalityList = listOf(
            "San Jose",
            "Magsaysay"
        )

        // Create an ArrayAdapter with the list of items
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, municipalityList)
        tvMunicipality.setAdapter(adapter)
    }

    private fun afterInit() {
        btnOpenCamera.setOnClickListener {
            // Check for storage permissions
            showAvatarPickerDialog(this, avatarList, avatar) { selectedAvatar ->
                this.selectedAvatar = selectedAvatar
            }
        }

        btnSave.setOnClickListener {
            loadingDialog.show()
            userId?.let { uid ->

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

                val municipality = tvMunicipality.text.toString()

                if(municipality.isNotEmpty()) {
                    avatarViewModel.saveSelectedAvatar(avatarName, municipality, uid)
                } else {
                    loadingDialog.dismiss()
                    showAlertDialogWithYesNo(
                        this,
                        layoutInflater,
                        "Municipality cannot be empty!",
                        "Cancel",
                        "Ok",
                        null
                    ) {}
                }

            }
        }

        avatarViewModel.isAddSuccess.observe(this) { result ->
            result.onSuccess { pairResult ->
                val isSuccess = pairResult.first
                val message = pairResult.second
                if(isSuccess) {
                    loadingDialog.dismiss()
                    showAlertDialogWithYesNo(
                        this,
                        layoutInflater,
                        message,
                        "Cancel",
                        "Ok", {
                            startActivity(Intent(this, LoginActivity::class.java)
                                .putExtra("userId", userId))
                            finish()
                        }, {
                            startActivity(Intent(this, LoginActivity::class.java)
                                .putExtra("userId", userId))
                            finish()
                        }
                    )
                }
            }
        }
    }

}