package com.example.androidproject


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import android.widget.TextView

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    var firestore : FirebaseFirestore ?= null
    var uid : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_home, container, false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        view.findViewById<RecyclerView>(R.id.H_Recycler).adapter = HomeRecyclerViewAdapter()
        view.findViewById<RecyclerView>(R.id.H_Recycler).layoutManager = LinearLayoutManager(activity)


        return view
    }
    inner class HomeRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        var UIDlist : ArrayList<String> = arrayListOf()
        init{
            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                UIDlist.clear()
                for(snapshot in querySnapshot!!.documents){
                    var item = snapshot.toObject(ContentDTO::class.java)
                    contentDTOs.add(item!!)
                    UIDlist.add(snapshot.id)
                }
                notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewholer = (holder as CustomViewHolder).itemView
            viewholer.findViewById<TextView>(R.id.profile_textView).text = contentDTOs!![position].userId
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewholer.findViewById(R.id.image_Content))
            viewholer.findViewById<TextView>(R.id.content).text = contentDTOs!![position].explain
            viewholer.findViewById<TextView>(R.id.favoriteCount).text = "Likes " + contentDTOs!![position].favoriteCount
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewholer.findViewById(R.id.profile_image))
            viewholer.findViewById<ImageView>(R.id.favoriteImage).setOnClickListener{
                favoriteEvent(position)
            }
            if(contentDTOs!![position].favorites.containsKey(uid)){
                viewholer.findViewById<ImageView>(R.id.favoriteImage).setImageResource(R.drawable.ic_baseline_favorite_24)
            }
            else{
                viewholer.findViewById<ImageView>(R.id.favoriteImage).setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }
        fun favoriteEvent(position: Int){
            var tsDoc = firestore?.collection("images")?.document(UIDlist[position])
            firestore?.runTransaction { transaction ->
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
}