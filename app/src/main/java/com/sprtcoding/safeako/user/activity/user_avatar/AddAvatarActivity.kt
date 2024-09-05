package com.sprtcoding.safeako.user.activity.user_avatar

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.button.MaterialButton
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.authentication.login.LoginActivity
import com.sprtcoding.safeako.firebaseUtils.Utils.uploadImageToFirebaseStorage
import com.sprtcoding.safeako.utils.Constants.Companion.PERMISSIONS_REQUEST_READ_WRITE_STORAGE
import com.sprtcoding.safeako.utils.Utility.showImagePickerOptions
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

class AddAvatarActivity : AppCompatActivity(), IDetailsCallBack, EasyPermissions.PermissionCallbacks {
    private lateinit var btnOpenCamera: MaterialButton
    private lateinit var avatar: CircleImageView
    private lateinit var btnSave: MaterialButton
    private var selectedImageUri: Uri? = null
    private var userId: String? = null
    private lateinit var loadingDialog: ProgressDialog

    private val perms = if(Build.VERSION.SDK_INT >= 33) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

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
    }

    private fun init() {
        userId = intent.getStringExtra("userId")
        loadingDialog = ProgressDialog(this)
        loadingDialog.setMessage("Uploading image...")
    }

    private fun afterInit() {
        btnOpenCamera.setOnClickListener {
            // Check for storage permissions
            if (checkPermissions()) {
                // If permissions are already granted, initialize image picker
                showImagePickerOptions(this)
            } else {
                // Request storage permissions
                requestPermissions()
            }
        }

        btnSave.setOnClickListener {
            loadingDialog.show()
            selectedImageUri?.let { uri ->
                userId?.let { it1 -> uploadImageToFirebaseStorage(uri, it1, this) }
            } ?: run {
                loadingDialog.dismiss()
                Toast.makeText(this, "No image selected to save", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return EasyPermissions.hasPermissions(this, *perms)
    }

    private fun requestPermissions() {
        if (EasyPermissions.hasPermissions(this, *perms)) {
            // Permissions already granted, proceed with your logic
            showImagePickerOptions(this)
        } else {
            // Request permissions through EasyPermissions
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(this, PERMISSIONS_REQUEST_READ_WRITE_STORAGE, *perms)
                    .setRationale("Permissions are required to pick images.")
                    .setPositiveButtonText("Grant")
                    .setNegativeButtonText("Cancel")
                    .build()
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                val uri: Uri = data?.data!!

                // Use Uri object instead of File to avoid storage permissions
                Picasso.get()
                    .load(uri)
                    .into(avatar)

                selectedImageUri = uri
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSuccess(message: String) {
        loadingDialog.dismiss()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java)
            .putExtra("userId", userId))
        finish()
    }

    override fun onFailed(failed: String) {
        loadingDialog.dismiss()
        Toast.makeText(this, failed, Toast.LENGTH_SHORT).show()
    }

    override fun onError(error: String) {
        loadingDialog.dismiss()
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        showImagePickerOptions(this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        // Permissions denied
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            // Show rationale and redirect to app settings
            Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
        }
    }

}