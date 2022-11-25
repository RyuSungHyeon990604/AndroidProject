package com.example.androidproject



import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class MyPageFragment : Fragment() {
    var fragmentView: View? = null
    var fragmentView2: View? = null
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var auth: FirebaseAuth? = null
    var currentUserUid: String? = null
    var myProfileImages: String? = null
    var myName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView =
            LayoutInflater.from(activity).inflate(R.layout.fragment_mypage, container, false)
        fragmentView2 =
            LayoutInflater.from(activity).inflate(R.layout.activity_home, container, false)
        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserUid = auth?.currentUser?.uid
        fragmentView?.findViewById<RecyclerView>(R.id.account_reyclerview)?.adapter =
            MyPageFragmentRecyclerViewAdapter()
        fragmentView?.findViewById<RecyclerView>(R.id.account_reyclerview)?.layoutManager =
            GridLayoutManager(activity!!, 3)
        fragmentView?.findViewById<ImageView>(R.id.account_iv_profile)?.setOnClickListener {
            startActivity(Intent(context,ProfileActivity::class.java))
        }

        return fragmentView;
    }

    inner class MyPageFragmentRecyclerViewAdapter :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf();

        init {
            firestore?.collection("images")?.whereEqualTo("uid", currentUserUid)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    //Sometimes, This code return null of querySnapshot when it signout
                    if (querySnapshot == null) return@addSnapshotListener

                    //Get data
                    for (snapshot in querySnapshot.documents) {
                        contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                    }
                    println("contentDTOs : " + contentDTOs)
                    println("uid : " + currentUserUid)
                    fragmentView?.findViewById<TextView>(R.id.account_tv_post_count)?.text =
                        contentDTOs.size.toString()
                    notifyDataSetChanged()
                }


        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels / 3

            var imageview = ImageView(p0.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            getProfileImage()
            return CustomViewHolder(imageview)
        }

        inner class CustomViewHolder(var imageview: ImageView) :
            RecyclerView.ViewHolder(imageview) {

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageview = (holder as CustomViewHolder).imageview


            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl).apply(RequestOptions().centerCrop()).into(imageview)
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        fun getProfileImage() {
            val s = FirebaseStorage.getInstance();
            firestore?.collection("user")?.addSnapshotListener { querySnapshot, error ->
                for (snapshot in querySnapshot!!.documents) {
                    if (snapshot.getString("uid")!!
                            .contains(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    ) {
                        myProfileImages = snapshot.toObject(Users::class.java)!!.profileimageUrl
                        myName = snapshot.toObject(Users::class.java)!!.name
                    }
                }
                fragmentView?.let {
                    Glide.with(activity!!).load(myProfileImages)
                        .apply(RequestOptions().circleCrop()).into(
                        it.findViewById<ImageView>(R.id.account_iv_profile)
                    )
                }

            }
        }
    }
}
