import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val uid : String? = auth?.currentUser?.uid
        var friends : ArrayList<String>

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
                }
            }
        }









    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textView)
        val button: Button =  itemView.findViewById(R.id.followbutton)
    }


}