package com.example.instagramclone.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramclone.Models.User
import com.example.instagramclone.R
import com.example.instagramclone.Utils.FOLLOW
import com.example.instagramclone.databinding.SearchRvBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchAdapter(var context: Context, var userList: ArrayList<User>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {


    inner class ViewHolder(var binding: SearchRvBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = SearchRvBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(userList[position].image)
            .placeholder(R.drawable.user_profile_add_img).into(holder.binding.profileImage)
        holder.binding.textViewName.text = userList[position].name

        val currentUserUid = Firebase.auth.currentUser!!.uid

        val followingRef = Firebase.firestore.collection(currentUserUid + FOLLOW)

        followingRef.whereEqualTo("email", userList[position].email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // User is not being followed
                    holder.binding.buttonFollow.text = "Follow"
                } else {
                    // User is being followed
                    holder.binding.buttonFollow.text = "Unfollow"
                }
            }


        holder.binding.buttonFollow.setOnClickListener {
            // Check if the button text is "Follow"
            val isFollowing = holder.binding.buttonFollow.text == "Follow"

            if (isFollowing) {
                // Add the user to the following list
                followingRef.add(userList[position])
                    .addOnSuccessListener {
                        holder.binding.buttonFollow.text = "Unfollow"
                    }
                    .addOnFailureListener { exception ->
                        Log.e("SearchAdapter", "Error following user: ${exception.message}")
                    }
            } else {
                // Unfollow the user
                followingRef.whereEqualTo("email", userList[position].email)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot.documents) {
                            // Delete the document representing the user being followed
                            followingRef.document(document.id).delete()
                                .addOnSuccessListener {
                                    holder.binding.buttonFollow.text = "Follow"
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("SearchAdapter", "Error unfollowing user: ${exception.message}")
                                }
                        }
                    }
            }
        }
    }
}