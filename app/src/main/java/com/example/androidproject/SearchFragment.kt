package com.example.androidproject


import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore


class SearchFragment : Fragment() {
    var firestore : FirebaseFirestore?=null
    var searchText : String ?=null
    var postList : ArrayList<ContentDTO> = arrayListOf();
    var postUidList : ArrayList<String> = arrayListOf();
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view=LayoutInflater.from(activity).inflate(R.layout.fragment_search,container,false)
        firestore=FirebaseFirestore.getInstance();
        view.findViewById<RecyclerView>(R.id.SearchFragmentRecyclerView).adapter=SearchFragmentRecyclerViewAdapter()
        view.findViewById<RecyclerView>(R.id.SearchFragmentRecyclerView).layoutManager=LinearLayoutManager(activity)
        view?.findViewById<ImageView>(R.id.search)?.setOnClickListener {
            searchText=view.findViewById<EditText>(R.id.searchText).text.toString()
            ( view.findViewById<RecyclerView>(R.id.SearchFragmentRecyclerView).adapter as SearchFragmentRecyclerViewAdapter).search(
                "#"+searchText!!
            )
        }
        view?.findViewById<EditText>(R.id.searchText)?.setOnKeyListener(View.OnKeyListener { v, keyCode, event -> //Enter key Action
            if (event.action === KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                searchText=view.findViewById<EditText>(R.id.searchText).text.toString()
                ( view.findViewById<RecyclerView>(R.id.SearchFragmentRecyclerView).adapter as SearchFragmentRecyclerViewAdapter).search(
                    "#"+searchText!!
                )
                return@OnKeyListener true
            }
            false
        })
        return view
    }
    override fun onResume() {
        super.onResume()
        println(searchText)

    }
    inner class SearchFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){


        init{
            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, error ->
                postList.clear()
                postUidList.clear()
                for(snapshot in querySnapshot!!.documents){
                    var item=snapshot.toObject(ContentDTO::class.java)
                    postList.add(item!!)
                    postUidList.add(snapshot.id)
                }
                notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view=LayoutInflater.from(parent.context).inflate(R.layout.search_item_list,parent,false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewholder=(holder as CustomViewHolder).itemView
            viewholder.findViewById<TextView>(R.id.user_name).text=postList!![position].userId
            viewholder.findViewById<TextView>(R.id.description).text=postList!![position].explain
            Glide.with(holder.itemView.context).load(postList!![position].imageUrl).into(viewholder.findViewById(R.id.image))


        }

        override fun getItemCount(): Int {
            return postList.size
        }
        fun search(searchText : String){
            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, error ->
                postList.clear()
                postUidList.clear()
                for(snapshot in querySnapshot!!.documents){
                    if(snapshot.getString("explain")!!.contains(searchText)) {
                        var item = snapshot.toObject(ContentDTO::class.java)
                        postList.add(item!!)
                        postUidList.add(snapshot.id)
                    }
                }
                notifyDataSetChanged()
            }
        }
    }


}