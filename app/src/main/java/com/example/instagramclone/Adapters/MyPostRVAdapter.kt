package com.example.instagramclone.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.Models.Post
import com.example.instagramclone.databinding.MyPostRvDesignBinding
import com.squareup.picasso.Picasso

class MyPostRVAdapter(var context: Context, var postList: ArrayList<Post>) :
    RecyclerView.Adapter<MyPostRVAdapter.ViewHolder>() {



    interface OnPostLongPressListener {
        fun onLongPress(post: Post)
    }

    private var longPressListener: OnPostLongPressListener? = null

    fun setOnPostLongPressListener(listener: OnPostLongPressListener) {
        longPressListener = listener
    }


    inner class ViewHolder(var binding: MyPostRvDesignBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = MyPostRvDesignBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.d("onBindViewHolder","onBindViewHolder")
        val imageUrl = postList[position].postUrl
        val post = postList[position]
        Picasso.get().load(imageUrl).into(holder.binding.postImage)

        holder.itemView.setOnLongClickListener {
            longPressListener?.onLongPress(post)
            true
        }
    }


}