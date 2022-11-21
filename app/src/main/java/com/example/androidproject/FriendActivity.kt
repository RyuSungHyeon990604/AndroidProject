package com.example.androidproject

import ListAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidproject.databinding.ActivityFriendBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class FriendActivity : AppCompatActivity() {
    private lateinit var binding : ActivityFriendBinding    // 뷰 바인딩
    val db = FirebaseFirestore.getInstance()    // Firestore 인스턴스 선언
    val itemList = arrayListOf<ListLayout>()    // 리스트 아이템 배열
    val adapter = ListAdapter(itemList)         // 리사이클러 뷰 어댑터
    val user = Firebase.auth.currentUser
    var user_name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)       // 리사이클러 뷰 어댑터

        binding.re.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.re.adapter = adapter
        db.collection("uid")
            .get()
            .addOnSuccessListener {
                result ->
                for (document in result) {
                    println(document.id)
                    if(document.id==user?.uid.toString()){ //document.data == {name=유재원}
                        user_name= document.data.values.toString()
                        user_name =user_name.substring(1,user_name.length)
                        user_name =user_name.substring(0,user_name.length-1)
                        println("성공 "+user_name)
                    }

                }
                db.collection(user_name)   // 작업할 컬렉션
                    .get()      // 문서 가져오기
                    .addOnSuccessListener { result ->
                        // 성공할 경우
                        itemList.clear()
                        for (document in result) {  // 가져온 문서들은 result에 들어감
                            val item = ListLayout(document["name"] as String, document["friend"] as Boolean)
                            itemList.add(item)
                        }
                        adapter.notifyDataSetChanged()  // 리사이클러 뷰 갱신
                    }
                    .addOnFailureListener { exception ->
                        // 실패할 경우
                        Log.w("FriendActivity", "Error getting documents: $exception")
                    }
            }





    }
}