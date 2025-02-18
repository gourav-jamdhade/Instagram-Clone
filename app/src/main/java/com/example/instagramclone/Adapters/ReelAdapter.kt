package com.example.instagramclone.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.Models.Reel
import com.example.instagramclone.R
import com.example.instagramclone.Utils.USER_NODE
import com.example.instagramclone.databinding.ReelPageDesignBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class ReelAdapter(var context: Context, var reelList: ArrayList<Reel>) :
    RecyclerView.Adapter<ReelAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: ReelPageDesignBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ReelPageDesignBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reelList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentReel = reelList[position]
        val userId = Firebase.auth.currentUser?.uid
        val userRef = userId?.let { Firebase.firestore.collection(USER_NODE).document(it) }


        holder.binding.videoView.setOnClickListener {
            if (holder.binding.videoView.isPlaying) {
                holder.binding.videoView.pause()
                holder.binding.playPauseImageView.setImageResource(R.drawable.play_icon)
                holder.binding.playPauseImageView.visibility = View.VISIBLE
            } else {
                holder.binding.videoView.start()
                holder.binding.playPauseImageView.visibility = View.INVISIBLE

            }
        }
        // Load reel details
        loadReelDetails(holder, currentReel, userRef)
    }

    private fun loadReelDetails(
        holder: ViewHolder,
        currentReel: Reel,
        userRef: DocumentReference?
    ) {
        // Load reel details
        Picasso.get().load(currentReel.profile_link).placeholder(R.drawable.user_profile_add_img).into(holder.binding.profileImage)
        holder.binding.caption.text = currentReel.caption
        holder.binding.videoView.setVideoPath(currentReel.reelUrl)
        holder.binding.videoView.setOnPreparedListener {
            holder.binding.progressBar.visibility = View.GONE
            holder.binding.videoView.start()
            //holder.binding.menuLayout.visibility = View.VISIBLE
        }

    }
}
