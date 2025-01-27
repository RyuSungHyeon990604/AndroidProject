package com.example.androidproject


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidproject.databinding.ActivitySearchBinding
import com.example.androidproject.databinding.ItemDetailBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class SearchActivity : AppCompatActivity() {
    var firestore : FirebaseFirestore?=null
    var postList : ArrayList<ContentDTO> = arrayListOf();
    var postUidList : ArrayList<String> = arrayListOf();

    lateinit var binding : ActivitySearchBinding;
    lateinit var binding2 : ItemDetailBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        binding2= ItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestore=FirebaseFirestore.getInstance()


        println("first")
        binding.backBtn.setOnClickListener {
            super.onBackPressed()
        }
        binding.searchBtn.setOnClickListener {
            val search=findViewById<EditText>(R.id.searchText).text.toString()
                takeDataFromFirebase(search)
        }

    }

    fun initializeView(postArray : ArrayList<ContentDTO>){
        binding.postList.layoutManager=LinearLayoutManager(this)
        (binding.postList.layoutManager as LinearLayoutManager).reverseLayout=true
        (binding.postList.layoutManager as LinearLayoutManager).stackFromEnd=true
        binding.postList.adapter=SearchActivityAdapter(postArray,postUidList)
    }
    fun takeDataFromFirebase(search: String) : ArrayList<ContentDTO>{
        if(search == ""){
            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, error ->
                postList.clear()
                postUidList.clear()
                for(snapshot in querySnapshot!!.documents){
                    var item=snapshot.toObject(ContentDTO::class.java)
                    postList.add(item!!)
                    postUidList.add(snapshot.id)
                    println(search+"add")
                }
                initializeView(postList)
            }
        }
        else{
            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, error ->
                postList.clear()
                postUidList.clear()
                println("search clear")
                for(snapshot in querySnapshot!!.documents){
                    var item=snapshot.toObject(ContentDTO::class.java)
                    if(snapshot.getString("explain")!!.contains(search)) {
                        postList.add(item!!)
                        postUidList.add(snapshot.id)
                        println(search+"add")
                    }
                }
                initializeView(postList)
            }
        }

        return postList;
    }

}