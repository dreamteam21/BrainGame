package com.example.braingame

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream
import org.jetbrains.anko.toast

class ProfileFragment : Fragment() {

    val CAMERA_CAPTURE = 1000
    var mAuth: FirebaseAuth? = null
    private lateinit var imageUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        val userEmail = mAuth!!.currentUser!!.email
        profileFragmentEmail.setText(userEmail)

        profileFragmentDisplay.setOnClickListener {
            openCamera()
        }
    }

    private fun openCamera(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                startActivityForResult(pictureIntent, CAMERA_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK){
//            performCrop()
            val imageBitmap = data?.extras?.get("data") as Bitmap
            uploadImageAndSaveUri(imageBitmap)
        }
//        else if(requestCode == PIC_CROP){
//            val imageBitmap = data?.extras?.get("data") as Bitmap
//            uploadImageAndSaveUri(imageBitmap)
//        }
    }

//    private fun performCrop(){
//        try { //call the standard crop action intent (the user device may not support it)
//            val cropIntent = Intent("com.android.camera.action.CROP")
//            //indicate image type and Uri
//            cropIntent.setDataAndType(imageUri, "image/*")
//            //set crop properties
//            cropIntent.putExtra("crop", "true")
//            //indicate aspect of desired crop
//            cropIntent.putExtra("aspectX", 1)
//            cropIntent.putExtra("aspectY", 1)
//            //indicate output X and Y
//            cropIntent.putExtra("outputX", 256)
//            cropIntent.putExtra("outputY", 256)
//            //retrieve data on return
//            cropIntent.putExtra("return-data", true)
//            //start the activity - we handle returning in onActivityResult
//            startActivityForResult(cropIntent, PIC_CROP)
//        } catch (anfe: ActivityNotFoundException) { //display an error message
//            activity?.toast("Whoops - your device doesn't support the crop action!")
//        }
//    }

    private fun uploadImageAndSaveUri(bitmap: Bitmap){
        val byteArrayOutputStream = ByteArrayOutputStream()
        val storageReference = FirebaseStorage.getInstance().reference.child("profileDisplay/${mAuth!!.currentUser?.uid}")
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, byteArrayOutputStream)
        val image = byteArrayOutputStream.toByteArray()
        val upload = storageReference.putBytes(image)
        upload.addOnCompleteListener { uploadTask ->
            if(uploadTask.isSuccessful){
                storageReference.downloadUrl.addOnCompleteListener { urlTask ->
                    urlTask.result?.let{
                        imageUri = it
                        profileFragmentDisplay.setImageBitmap(bitmap)
                    }
                }
            }
            else{
                uploadTask.exception?.let {
                    activity?.toast(it.message!!)
                }
            }
        }
    }
}
