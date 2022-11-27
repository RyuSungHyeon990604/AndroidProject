import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidproject.ListLayout
import com.example.androidproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ListAdapter(val itemList: ArrayList<ListLayout>): RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    override fun onBindViewHolder(holder: ListAdapter.ViewHolder, position: Int) {

        holder.name.text = itemList[position].name
        holder.button.setText(itemList[position].follwer)
        var defaultImage = R.drawable.friend_icon //프사url을 못받아오면 기본으로 뛰워주는 이미지

        var url = itemList[position].url
        var img = holder.image
        Glide.with(holder.itemView.context)
            .load(url) // 불러올 이미지 url
            .placeholder(defaultImage) // 이미지 로딩 시작하기 전 표시할 이미지
            .error(defaultImage) // 로딩 에러 발생 시 표시할 이미지
            .fallback(defaultImage) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
            .circleCrop() // 동그랗게 자르기
            .into(img) // 이미지를 넣을 뷰
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val uid : String? = auth?.currentUser?.uid
        var friends : ArrayList<String>
        var drawable1 = ContextCompat.getDrawable(holder.itemView.context, R.drawable.followbutton1);
        var drawable2 = ContextCompat.getDrawable(holder.itemView.context, R.drawable.followbutton2);
        if(holder.button.text.equals("팔로우")) {
            holder.button.background = drawable1
        }
        else    {
            holder.button.background = drawable2
        }

        holder.button.setOnClickListener {
            if(holder.button.text.equals("팔로우")){ //친구가아닌상황
                firestore?.collection("user")?.get()?.addOnSuccessListener { result ->
                    for (document in result) {
                        if (document.get("uid") == uid) {
                            var username = document.get("name") as String?
                            if (document?.get("friends") == null) {
                                friends = arrayListOf()
                            } else {
                                friends = document.get("friends") as ArrayList<String>
                            }
                            println(friends)
                            friends.add(itemList[position].name) //팔로우를 누른 유저
                            println(friends)
                            if (username != null) {
                                firestore?.collection("user")?.document(username)
                                    ?.update("friends", friends)
                            }
                        }
                    }
                    holder.button.setText("언팔로우")
                    holder.button.background = drawable2
                }
            }
            else {
                firestore?.collection("user")?.get()?.addOnSuccessListener { result ->
                    for (document in result) {
                        if (document.get("uid") == uid) {
                            var username = document.get("name") as String?
                            if (document?.get("friends") == null) {
                                friends = arrayListOf()
                            } else {
                                friends = document.get("friends") as ArrayList<String>
                            }
                            friends.remove(holder.name.text)
                            if (username != null) {
                                firestore?.collection("user")?.document(username)
                                    ?.update("friends", friends)
                            }
                        }
                    }
                    holder.button.setText("팔로우")
                    holder.button.background = drawable1
                }
            }
        }









    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textView)
        val button: Button =  itemView.findViewById(R.id.followbutton)
        val image: ImageView = itemView.findViewById(R.id.imageView)
    }


}