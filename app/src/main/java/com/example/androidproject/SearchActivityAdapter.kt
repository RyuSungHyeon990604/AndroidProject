package com.example.androidproject

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidproject.databinding.SearchItemListBinding
import com.google.firebase.firestore.FirebaseFirestore

class SearchActivityAdapter(private val postList : ArrayList<ContentDTO>) : RecyclerView.Adapter<SearchActivityAdapter.SearchActivityViewHolder>(){
    class SearchActivityViewHolder(private val binding :SearchItemListBinding) : RecyclerView.ViewHolder(binding.root){


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchActivityViewHolder {
        return SearchActivityViewHolder(SearchItemListBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: SearchActivityViewHolder, position: Int) {
        var viewholder=(holder as SearchActivityViewHolder).itemView
            viewholder.findViewById<TextView>(R.id.user_name).text=postList!![position].userId
            viewholder.findViewById<TextView>(R.id.description).text=postList!![position].explain
            Glide.with(holder.itemView.context).load(postList!![position].imageUrl).into(viewholder.findViewById(R.id.image))



    }

    override fun getItemCount(): Int {
        return postList.size
    }
}