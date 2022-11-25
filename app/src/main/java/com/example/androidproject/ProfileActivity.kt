package com.example.androidproject

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.androidproject.databinding.ActivityProfileBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProfileActivity : AppCompatActivity() {
    var PickImage = 0;
    var storage : FirebaseStorage ?= null
    var photouri : Uri?= null
    var auth : FirebaseAuth?= null
    var firestore : FirebaseFirestore ?= null
    lateinit var imageView : ImageView
    var CUItem : Users? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageView = binding.Pimage
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        search()
        println(auth?.currentUser?.uid + " : CUID")


        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        binding.ProfileUpload.setOnClickListener {
            startActivityForResult(photoPickerIntent, PickImage)
        }
        binding.CompleteButton.setOnClickListener {
            profileUpload()
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
    fun search(){
        val uid : String? = auth?.currentUser?.uid
        firestore?.collection("user")?.orderBy("uid")?.addSnapshotListener { querySnapshot, error ->
            for(snapshot in querySnapshot!!.documents) {
                if (snapshot.getString("uid")!!.contains(uid.toString())) {
                    CUItem = snapshot.toObject(Users::class.java)!!
                }
            }
        }
    }
    fun profileUpload(){
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE " + timestamp + "_.png"
        var storageRef = storage?.reference?.child("profileimages")?.child(imageFileName)
        storageRef?.putFile(photouri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { url ->
            var username = CUItem?.name
            if (username != null) {
                firestore?.collection("user")?.document(username)?.update("profileimageUrl", url.toString())
            }
            setResult(Activity.RESULT_OK)
        }
    }
}