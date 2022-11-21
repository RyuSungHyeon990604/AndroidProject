package com.example.androidproject

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.androidproject.databinding.ActivityHomeBinding
import com.example.androidproject.databinding.ActivityWritingBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.Date

class WritingActivity : AppCompatActivity() {
    var PickImage = 0;
    var storage : FirebaseStorage ?= null
    var photouri : Uri?= null
    var auth : FirebaseAuth ?= null
    var firestore : FirebaseFirestore ?= null
    lateinit var imageView : ImageView
    lateinit var  explain : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageView = binding.addphotoimage
        explain = binding.editExplain
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        binding.imageupload.setOnClickListener {
            startActivityForResult(photoPickerIntent, PickImage)
        }
        binding.uploadButton.setOnClickListener {
            contentUpload()
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
    fun contentUpload(){
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE " + timestamp + "_.png"
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        storageRef?.putFile(photouri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { url ->
                var contentDTO = ContentDTO()
                contentDTO.imageUrl = url.toString()
                contentDTO.uid = auth?.currentUser?.uid
                contentDTO.userId = auth?.currentUser?.email
                contentDTO.explain = explain.text.toString()
                contentDTO.timestamp = System.currentTimeMillis()
                firestore?.collection("images")?.document()?.set(contentDTO)
                setResult(Activity.RESULT_OK)
                finish()
        }
    }
}