package com.example.androidproject



import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MyPageFragment : Fragment() {
    var fragmentView: View? = null
    var fragmentView2: View? = null
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var auth: FirebaseAuth? = null
    var currentUserUid: String? = null
    var contentDTOs: ArrayList<ContentDTO> = arrayListOf();
    var profileImageurl : String ?= null
    var ce : ArrayList<String> ?= null
    var username : String ?= null
    var followCount : Int = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        println("create")
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
    inner class MyPageFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            firestore?.collection("images")?.whereEqualTo("uid", currentUserUid)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    //Sometimes, This code return null of querySnapshot when it signout
                    contentDTOs.clear()
                    if (querySnapshot == null) return@addSnapshotListener

                    //Get data
                    for (snapshot in querySnapshot.documents) {
                        contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                    }
                    fragmentView?.findViewById<TextView>(R.id.account_tv_post_count)?.text =
                        contentDTOs.size.toString()
                    notifyDataSetChanged()
                }
            getMyFollower()
            getProfileImage()
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
        fun searchMyName(name : String, MainArray : ArrayList<String>?): Boolean {
            var SearchArray : ArrayList<String>? = MainArray
            println(SearchArray)
            if (SearchArray != null) {
                for(friend in SearchArray){
                    println(friend)
                    if(friend.equals(name)){
                        return true
                    }
                }
            }
            return false
        }
        fun getMyFollower(){
            val uid : String? = auth?.currentUser?.uid
            firestore?.collection("user")?.orderBy("uid")?.addSnapshotListener { querySnapshot, error ->
                for(snapshot in querySnapshot!!.documents) {
                    if (snapshot.getString("uid")!!.contains(uid.toString())) {
                        var user : Users? = snapshot.toObject(Users::class.java)
                        println(user)
                        username = user?.name
                        ce = user?.friends
                        println("이름은 "+ username)
                    }
                }
                followCount = 0
                firestore?.collection("user")?.get()?.addOnSuccessListener { result ->
                    for(document in result){
                        println("이름은 "+ username)
                        if(searchMyName(username!!, document.get("friends") as ArrayList<String>?)){
                            println("친구 잇음, +1")
                            followCount++
                        }
                    }
                    println("최종 숫자는 : "+followCount)
                    fragmentView?.findViewById<TextView>(R.id.account_tv_follower_count)?.setText(followCount.toString())
                    if(ce==null){
                        fragmentView?.findViewById<TextView>(R.id.account_tv_following_count)?.setText("0")
                    }
                    else
                        fragmentView?.findViewById<TextView>(R.id.account_tv_following_count)?.setText(ce?.size.toString())
                }
            }
        }
        fun getProfileImage() {
            val uid : String? = auth?.currentUser?.uid
            var username : String ?= null
            firestore?.collection("user")?.addSnapshotListener { querySnapshot, error ->
                for(snapshot in querySnapshot!!.documents){
                    if(snapshot.getString("uid")!!.contains(uid.toString())){
                        profileImageurl = snapshot.get("profileimageUrl") as String?
                    }
                }
                fragmentView?.let {
                    var defaultImage = R.drawable.friend_icon
                    activity?.let { it1 ->
                        Glide.with(it1).load(profileImageurl).placeholder(defaultImage) // 이미지 로딩 시작하기 전 표시할 이미지
                            .error(defaultImage) // 로딩 에러 발생 시 표시할 이미지
                            .fallback(defaultImage)
                            .apply(RequestOptions().circleCrop()).into(
                                it.findViewById<ImageView>(R.id.account_iv_profile)
                            )
                    }
                }
            }
        }
    }




}
