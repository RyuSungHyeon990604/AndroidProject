package com.example.androidproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProfileAdapter(val profileList : ArrayList<Profiles>) : RecyclerView.Adapter<ProfileAdapter.CustomViewHolder>() {

    //뷰홀더가 처음 생성될때
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return CustomViewHolder(view).apply { //클릭시 액션
            itemView.setOnClickListener { //여기서 itemview는 뷰홀더의 아이템들을 의미한다.

            }
        }
        //뷰홀더에 뷰를 넘겨주고 이 것을 반환한다.
    }

    //재활용해주는 곳 및 값을 넣어주는 곳
    override fun onBindViewHolder(holder: ProfileAdapter.CustomViewHolder, position: Int) {
        holder.profile.setImageResource(profileList.get(position).profile)
        holder.name.text = profileList.get(position).name
        holder.job.text = profileList.get(position).contents

    }

    //리스트의 갯수를 적어준다
    override fun getItemCount(): Int {
        return profileList.size
    }

    //뷰홀더 클래스(음료수처럼 잡아주는 홀더)
    //이곳에서 파인드뷰아이디로 리스트 아이템에 있는 뷰들을 참조한다.
    inner class CustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val profile = itemView.findViewById<ImageView>(R.id.imageView) //사진
        val name = itemView.findViewById<TextView>(R.id.textView) //이름
        val job = itemView.findViewById<TextView>(R.id.contentsView) //자기소개
    }
}