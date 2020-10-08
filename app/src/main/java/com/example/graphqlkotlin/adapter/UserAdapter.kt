package com.example.graphqlkotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.graphqlkotlin.FeedResultQuery
import com.example.graphqlkotlin.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class UserAdapter(val userList: MutableList<FeedResultQuery.People>): RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder?.txtID?.text = userList[position].name()
        holder?.txtTitle?.text = userList[position].gender()
        holder?.txtBody?.text = userList[position].area()

        Picasso.get()
            .load(userList[position].image())
            .into(holder.image, object :Callback{
                override fun onSuccess() {
                }

                override fun onError(e: Exception?) {
                }
            })
    }

    override fun onCreateViewHolder(parentView: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parentView?.context).inflate(R.layout.carview_user, parentView, false)
        return ViewHolder(v);
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val image = itemView.findViewById<ImageView>(R.id.image)
        val txtTitle = itemView.findViewById<TextView>(R.id.txt_title)
        val txtBody = itemView.findViewById<TextView>(R.id.txt_body)
        val txtID = itemView.findViewById<TextView>(R.id.txt_user_id)
    }

}