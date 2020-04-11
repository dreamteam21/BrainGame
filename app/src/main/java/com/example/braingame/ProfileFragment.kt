package com.example.braingame

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    companion object {
        private const val CAMERA_CAPTURE = 1000
        private const val PERMISSION_CODE = 2000
        private const val PIC_CROP = 3000
        private const val DEFAULT_DISPLAY_IMAGE = "https://i.ibb.co/37P7VFB/blank-profile.jpg"
    }
    var mAuth: FirebaseAuth? = null
    private lateinit var imageUri: Uri
    var imageUriBeforeCrop: Uri? = null
    private val TAG : String = "Profile Fragment"
    val score = Score(null)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        mAuth!!.currentUser?.let { user ->
            if(user.photoUrl != null){
                Glide.with(this)
                    .load(user.photoUrl)
                    .into(profileFragmentDisplay)
            }
            if(user.displayName != null) {
                profileFragmentUsername.setText(user.displayName)
            }
            profileFragmentEmail.setText(user.email)
        }

        var tempUserList : ArrayList<Score>? = ScoreModel.getData()
        if(tempUserList != null){
            for(user in tempUserList){
                if(user.uid == mAuth!!.currentUser?.uid){

                    score.uid = user.uid
                    score.score = user.score
                    break
                }
            }
        }


        profileFragmentDisplay.setOnClickListener {
            profileFragmentDisplayProgressBar.visibility = ProgressBar.VISIBLE
            if(checkSelfPermission(activity!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                    activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, PERMISSION_CODE)
            }
            else {
                openCamera()
            }
        }

        profileFragmentButton.setOnClickListener {
            profileFragmentProgressBar.visibility = ProgressBar.VISIBLE
            val photo = when { // switch
                ::imageUri.isInitialized -> imageUri
                mAuth!!.currentUser?.photoUrl == null -> Uri.parse(DEFAULT_DISPLAY_IMAGE)
                else -> mAuth!!.currentUser?.photoUrl
            }
            val name = profileFragmentUsername.text.toString().trim()
            if (name.isEmpty()) {
                profileFragmentProgressBar.visibility = ProgressBar.INVISIBLE
                profileFragmentUsername.error = getString(R.string.profile_username_error)
                profileFragmentUsername.requestFocus()
                return@setOnClickListener
            }
            val updateProfile =
                UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(photo).build()
            mAuth!!.currentUser?.updateProfile(updateProfile)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    score.name = name
                    ScoreModel.saveScore(score, OnCompleteListener { task ->
                        if(task.isComplete){
                            Log.d(TAG, "update user entry in db successfully.")
                        }
                    })
                    profileFragmentProgressBar.visibility = ProgressBar.INVISIBLE
                    activity!!.recreate()
                    context?.toast(R.string.profile_pic_upload_success)
                } else {
                    profileFragmentProgressBar.visibility = ProgressBar.INVISIBLE
                    context?.toast(task.exception?.message!!)
                }
            }
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(activity!!, "Permission denied", Toast.LENGTH_SHORT).show()
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                }
            }
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUriBeforeCrop = requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriBeforeCrop)
            pictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                startActivityForResult(pictureIntent, CAMERA_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK) {
            performCrop()
//            val imageBitmap = data?.extras?.get("data") as Bitmap
//            uploadImageAndSaveUri(imageBitmap)
        } else if (requestCode == PIC_CROP) {
            val uri: Uri? = data?.data
            if (uri != null) {
                try {
                    val imageBitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(
                            ImageDecoder.createSource(
                                requireContext().contentResolver,
                                uri
                            )
                        )
                    } else {
                        MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                    }
                    uploadImageAndSaveUri(imageBitmap)
                } catch (e: Exception) {
                    Log.e("ProfileFragment", "", e)
                }
            }
        }
    }

    private fun performCrop() {
        try { //call the standard crop action intent (the user device may not support it)
            val cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(imageUriBeforeCrop, "image/*")
            cropIntent.putExtra("crop", "true")
            cropIntent.putExtra("aspectX", 1)
            cropIntent.putExtra("aspectY", 1)
            cropIntent.putExtra("outputX", 720)
            cropIntent.putExtra("outputY", 720)
            cropIntent.putExtra("return-data", true)
            startActivityForResult(cropIntent, PIC_CROP)
        } catch (anfe: ActivityNotFoundException) {
            activity?.toast(R.string.profile_crop_not_support)
        }
    }

    private fun uploadImageAndSaveUri(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val storageReference = FirebaseStorage.getInstance()
            .reference.child("profileDisplay/${mAuth!!.currentUser?.uid}")
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val image = byteArrayOutputStream.toByteArray()
        val upload = storageReference.putBytes(image)
        upload.addOnCompleteListener { uploadTask ->
            if (uploadTask.isSuccessful) {
                storageReference.downloadUrl.addOnCompleteListener { urlTask ->
                    urlTask.result?.let {
                        imageUri = it
                        profileFragmentDisplay.setImageBitmap(bitmap)
                        profileFragmentDisplayProgressBar.visibility = ProgressBar.INVISIBLE
                    }
                }
            } else {
                uploadTask.exception?.let {
                    activity?.toast(it.message!!)
                    profileFragmentDisplayProgressBar.visibility = ProgressBar.INVISIBLE
                }
            }
        }

    }
}
