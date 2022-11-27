package com.example.androidproject

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.androidproject.databinding.ActivityProfileBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*


class ProfileActivity : AppCompatActivity() {
    var PickImage = 0;
    var storage : FirebaseStorage ?= null
    var photouri : Uri?= null
    var auth : FirebaseAuth?= null
    var firestore : FirebaseFirestore ?= null
    var username : String ?= null
    lateinit var imageView : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageView = binding.Pimage
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        println(auth?.currentUser?.uid + " : CUID")

        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        binding.ProfileUpload.setOnClickListener {
            startActivityForResult(photoPickerIntent, PickImage)
        }
        binding.CompleteButton.setOnClickListener {
            val uid : String? = auth?.currentUser?.uid
            firestore?.collection("user")?.get()?.addOnSuccessListener { result ->
                for(document in result){
                    if(document.get("uid") == uid){
                        username = document.get("name") as String?
                    }
                }
                profileUpload(username)
                super.onBackPressed()

            }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PickImage){
            if(resultCode == Activity.RESULT_OK){
                photouri = data?.data
                imageView.setImageURI(photouri)
            }else{
                // 취소버튼
                finish()
            }
        }
    }
    fun profileUpload(UN: String?){
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = UN + "Profile_.png"
        var storageRef = storage?.reference?.child("profileimages")?.child(imageFileName)
        storageRef?.putFile(photouri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { url ->

            var username = UN
            if (username != null) {
                firestore?.collection("user")?.document(username)?.update("profileimageUrl", url.toString())

            }
            firestore?.collection("images")?.whereEqualTo("uid",auth?.currentUser?.uid.toString())
                ?.get()
                ?.addOnSuccessListener { document->
                    for(documents in document){
                        firestore?.collection("images")?.document(documents.id)?.update("upi", url.toString())
                    }
                }
                setResult(Activity.RESULT_OK)

        }
    }
}