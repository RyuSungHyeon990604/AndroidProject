package com.example.androidproject

import ListAdapter
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.arrayMapOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidproject.databinding.ActivityFriendBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FriendActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFriendBinding    // 뷰 바인딩
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    val itemList = arrayListOf<ListLayout>()    // 리스트 아이템 배열
    val adapter = ListAdapter(itemList)         // 리사이클러 뷰 어댑터
    var CUItem: Users? = null
    var allItem : Users? = null
    val allList = arrayListOf<Users>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)       // 리사이클러 뷰 어댑터

        binding.re.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.re.adapter = adapter


        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        search()


        val handler = Handler()
        handler.postDelayed(Runnable {
            itemList.clear()
            //CUItem?.friends? 내가가지고있는 친구목록 어레이리스트
            //allList[].name 이게 전체유저

            for(user in allList){
                println("상대방이름"+user.name)
                if(!CUItem?.name?.equals(user.name)!!){
                    if(CUItem?.friends?.contains(user.name) == true){
                        val item = ListLayout(user.name as String , "언팔로우")
                        itemList.add(item)
                    }
                    else{
                        val item = ListLayout(user.name as String , "팔로우")
                        itemList.add(item)
                    }
                }

            }
/*
            for(i in 0..allList.size-1){
                var a = false

                for (j in 0..(CUItem?.friends?.size?.minus(1) ?:0 )){
                    var str = CUItem?.friends?.get(j) as String
                    //str = str.substring(1, str.length)
                    println("현재"+str)
                    println("전체유저"+allList[i].name as String)
                    if(str == allList[i].name as String){
                        a= true
                        println("팔로우확인")
                    }
                    else{
                        a=false
                        println("팔로우아님")
                    }
                }


                if(allList[i].name as String != CUItem!!.name){ //본인꺼는 팔로우창에 안뜨게
                    val item = ListLayout(allList[i].name as String , a)
                    itemList.add(item)
                }
            }
            */


            adapter.notifyDataSetChanged()
        }, 2000)


    }

    fun search(){
        val uid : String? = auth?.currentUser?.uid
        firestore?.collection("user")?.orderBy("uid")?.addSnapshotListener { querySnapshot, error ->
            for(snapshot in querySnapshot!!.documents) {
                allItem = snapshot.toObject(Users::class.java)!!
                allList.add(allItem!!)
                if (snapshot.getString("uid")!!.contains(uid.toString())) {
                    CUItem = snapshot.toObject(Users::class.java)!!
                }
            }
        }
    }

}