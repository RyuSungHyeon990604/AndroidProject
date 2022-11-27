package com.example.androidproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.androidproject.databinding.ActivityCommentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommentActivity : AppCompatActivity() {
    var contentUid : String ?= null
    var auth : FirebaseAuth ?= null
    var firestore : FirebaseFirestore ?= null
    var username : String ?= null
    var imageUrl : String ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        contentUid = intent.getStringExtra("contentUid")
        imageUrl=intent.getStringExtra("imageUrl")

        binding.commentRecycler.adapter = CommentRecyclerViewAdapter()
        binding.commentRecycler.layoutManager = LinearLayoutManager(this)
        Glide.with(this).load(imageUrl).into(binding.post)
        binding.commentSend.setOnClickListener {
            val uid : String? = auth?.currentUser?.uid
            firestore?.collection("user")?.get()?.addOnSuccessListener { result ->
                for(document in result){
                    if(document.get("uid") == uid){
                        username = document.get("name") as String?
                    }
                }
                println(username)
                var comment = ContentDTO.Comment()
                comment.userId = username
                comment.uid = FirebaseAuth.getInstance().currentUser?.uid
                comment.comment = binding.commentEdit.text.toString()
                comment.timestamp = System.currentTimeMillis()
                FirebaseFirestore.getInstance().collection("images").document(contentUid!!).collection("comments").document().set(comment)
                binding.commentEdit.setText("")
            }

        }
        binding.commentEdit.setOnKeyListener { view, i, keyEvent ->
            if ((keyEvent.action== KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                val uid : String? = auth?.currentUser?.uid
                firestore?.collection("user")?.get()?.addOnSuccessListener { result ->
                    for(document in result){
                        if(document.get("uid") == uid){
                            username = document.get("name") as String?
                        }
                    }
                    println(username)
                    var comment = ContentDTO.Comment()
                    comment.userId = username
                    comment.uid = FirebaseAuth.getInstance().currentUser?.uid
                    comment.comment = binding.commentEdit.text.toString()
                    comment.timestamp = System.currentTimeMillis()
                    FirebaseFirestore.getInstance().collection("images").document(contentUid!!).collection("comments").document().set(comment)
                    binding.commentEdit.setText("")
                }
                true
            } else {
                false
            }
        }
    }

    inner class CommentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var comments : ArrayList<ContentDTO.Comment> = arrayListOf()
        init {
            FirebaseFirestore.getInstance()
                .collection("images")
                .document(contentUid!!)
                .collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    comments.clear()
                    if(querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot.documents!!){
                        comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                    }
                    notifyDataSetChanged()
                }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
            return CustomViewHolder(view)
        }
        private inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var view = holder.itemView
            view.findViewById<TextView>(R.id.comment).text = comments[position].comment
            view.findViewById<TextView>(R.id.commentprofileId).text = comments[position].userId
            FirebaseFirestore.getInstance()
                .collection("user")
                .document(comments[position].userId!!)
                .get()
                .addOnCompleteListener { task ->
                    var url = task.result.get("profileimageUrl")
                    Glide.with(holder.itemView.context).load(url)
                        .apply(RequestOptions().circleCrop())
                        .into(view.findViewById<ImageView>(R.id.commentprofileImage))
                }
        }

        override fun getItemCount(): Int {
            return comments.size
        }

    }
}