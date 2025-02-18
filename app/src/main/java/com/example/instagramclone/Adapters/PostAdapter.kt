package com.example.instagramclone.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramclone.Models.Post
import com.example.instagramclone.Models.User
import com.example.instagramclone.R
import com.example.instagramclone.Utils.POST
import com.example.instagramclone.Utils.USER_NODE
import com.example.instagramclone.databinding.PostRvBinding
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class PostAdapter(var context: Context, var postList: ArrayList<Post>) :
    RecyclerView.Adapter<PostAdapter.MyHolder>() {


    inner class MyHolder(var binding: PostRvBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        var binding = PostRvBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {


        val postId = postList[position].uid
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        Firebase.firestore.collection(USER_NODE).document(postList.get(position).uid).get()
            .addOnSuccessListener {
                var user = it.toObject<User>()
                Glide.with(context).load(user!!.image).placeholder(R.drawable.user_profile_signup)
                    .into(holder.binding.imageViewProfile)
                holder.binding.textViewProfileName.text = user.name
            }

        val likedRef = Firebase.firestore.collection(POST).document(postId).collection("likes")
            .document(currentUserId)
        likedRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                // User has liked this post
                holder.binding.like.setImageResource(R.drawable.after_like_icon)
            } else {
                // User has not liked this post
                holder.binding.like.setImageResource(R.drawable.before_like_icon)
            }
        }

        var timeToShow = postList.get(position).time.toLong()

        Glide.with(context).load(postList.get(position).postUrl).placeholder(R.drawable.loading)
            .into(holder.binding.postImage)
        holder.binding.textViewTime.text = TimeAgo.using(timeToShow)
        holder.binding.caption.text = postList.get(position).caption

        holder.binding.share.setOnClickListener {
            var intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, postList.get(position).postUrl)
            context.startActivity(intent)

        }

        holder.binding.like.setOnClickListener {
            likedRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // User has already liked this post, so unlike it
                    documentSnapshot.reference.delete()
                    holder.binding.like.setImageResource(R.drawable.before_like_icon)
                } else {
                    // User has not liked this post, so like it
                    documentSnapshot.reference.set(mapOf("liked" to true))
                    holder.binding.like.setImageResource(R.drawable.after_like_icon)
                }
            }
        }


    }


}