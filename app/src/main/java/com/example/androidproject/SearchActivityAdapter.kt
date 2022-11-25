package com.example.androidproject

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidproject.databinding.ItemDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SearchActivityAdapter(private val postList : ArrayList<ContentDTO>,private val UIDlist : ArrayList<String>) : RecyclerView.Adapter<SearchActivityAdapter.SearchActivityViewHolder>(){
    class SearchActivityViewHolder(private val binding: ItemDetailBinding) : RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchActivityViewHolder {
        return SearchActivityViewHolder(ItemDetailBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: SearchActivityViewHolder, position: Int) {
        var viewholer=(holder as SearchActivityViewHolder).itemView
        viewholer.findViewById<TextView>(R.id.profile_textView).text = postList!![position]?.UN
        Glide.with(holder.itemView.context).load(postList!![position].imageUrl).into(viewholer.findViewById(R.id.image_Content))
        viewholer.findViewById<TextView>(R.id.content).text = postList!![position].explain
        viewholer.findViewById<TextView>(R.id.favoriteCount).text = "Likes " + postList!![position].favoriteCount
        Glide.with(holder.itemView.context).load(postList!![position].UPI).into(viewholer.findViewById(R.id.profile_image))
        viewholer.findViewById<ImageView>(R.id.favoriteImage).setOnClickListener{
            favoriteEvent(position)
        }
        if(postList!![position].favorites.containsKey(FirebaseAuth.getInstance().currentUser?.uid)){
            viewholer.findViewById<ImageView>(R.id.favoriteImage).setImageResource(R.drawable.ic_baseline_favorite_24)
        }
        else{
            viewholer.findViewById<ImageView>(R.id.favoriteImage).setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }


    }

    override fun getItemCount(): Int {
        return postList.size
    }

    fun favoriteEvent(position: Int){

        var tsDoc = FirebaseFirestore.getInstance()?.collection("images")?.document(UIDlist[position])
        FirebaseFirestore.getInstance()?.runTransaction { transaction ->
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
            if(contentDTO!!.favorites.containsKey(uid)){ // 좋아요 눌렀을 때
                contentDTO?.favoriteCount = contentDTO?.favoriteCount?.minus(1)
                if (uid != null) {
                    contentDTO?.favorites?.remove(uid)
                };
            }
            else{
                contentDTO?.favoriteCount = contentDTO?.favoriteCount?.plus(1)
                contentDTO?.favorites?.set(uid!!, true)
            }
            transaction.set(tsDoc,contentDTO)
        }
    }
}