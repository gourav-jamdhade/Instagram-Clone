package com.example.instagramclone.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.instagramclone.Models.Reel
import com.example.instagramclone.databinding.MyReelRvDesignBinding

class MyReelRVAdapter(var context: Context, var reelList: ArrayList<Reel>) :
    RecyclerView.Adapter<MyReelRVAdapter.ViewHolder>() {


    interface OnReelLongPressListener {
        fun onLongPress(reel: Reel)
    }

    private var longPressListener: OnReelLongPressListener? = null

    fun setOnReelLongPressListener(listener: OnReelLongPressListener) {
        longPressListener = listener
    }


    inner class ViewHolder(var binding: MyReelRvDesignBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = MyReelRvDesignBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reelList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("onBindViewHolder Reels", "onBindViewHolder Reels")
        val reelUrl: String = reelList[position].reelUrl
        val reel: Reel = reelList[position]

        Glide.with(context)
            .load(reelUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.binding.postVideo)

        holder.itemView.setOnLongClickListener {
            longPressListener?.onLongPress(reel)
            true
        }
    }

//    fun updateData(newReelList: List<Reel>) {
//        reelList.clear()
//        reelList.addAll(newReelList)
//        notifyDataSetChanged()
//    }


}