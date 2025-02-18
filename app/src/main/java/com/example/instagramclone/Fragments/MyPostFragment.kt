package com.example.instagramclone.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.instagramclone.Adapters.MyPostRVAdapter
import com.example.instagramclone.Models.Post
import com.example.instagramclone.R
import com.example.instagramclone.Utils.POST
import com.example.instagramclone.databinding.FragmentMyPostBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase


class MyPostFragment : Fragment() {

    val postList = ArrayList<Post>()
    lateinit var adapter: MyPostRVAdapter
    private val binding: FragmentMyPostBinding by lazy {
        FragmentMyPostBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("OnCreate", "onCreate")
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        adapter = MyPostRVAdapter(requireContext(), postList)

        binding.rv.layoutManager =
            StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_between_items)
        binding.rv.addItemDecoration(VerticalSpaceItemDecoration(spacingInPixels))
        binding.rv.adapter = adapter
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
            var tempList = arrayListOf<Post>()
            for (i in it.documents) {
                var post: Post = i.toObject<Post>()!!
                tempList.add(post)
            }

            postList.clear()
            postList.addAll(tempList)
            adapter.notifyDataSetChanged()
        }

        adapter.setOnPostLongPressListener(object : MyPostRVAdapter.OnPostLongPressListener {
            override fun onLongPress(post: Post) {
                showLongPressDialog(post)
            }
        })


        // Inflate the layout for this fragment
        return binding.root
    }

    private fun showLongPressDialog(post: Post) {
        val options = arrayOf("Delete", "Share")

        AlertDialog.Builder(requireContext()).setTitle("Choose Action")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showDeleteConfirmationDialog(post)
                    1 -> sharePost(post)
                }
            }.show()
    }

    private fun showDeleteConfirmationDialog(post: Post) {
        AlertDialog.Builder(requireContext()).setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("Yes") { _, _ ->
                deletePost(post.postUrl)
            }.setNegativeButton("No", null).show()
    }

    private fun sharePost(post: Post) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, "${post.caption}\n${post.postUrl}")
        startActivity(Intent.createChooser(shareIntent, "Share post via"))
    }

    private fun deletePost(postUrl: String) {
        val query = Firebase.firestore.collection(Firebase.auth.currentUser!!.uid)
            .whereEqualTo("postUrl", postUrl).get()


        val query2 = Firebase.firestore.collection(POST).whereEqualTo("postUrl", postUrl).get()

        query.addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                document.reference.delete().addOnSuccessListener {
                    val post = postList.find { it.postUrl == postUrl }
                    post?.let {
                        postList.remove(it)
                        adapter.notifyDataSetChanged()
                    }
                }.addOnFailureListener { exception ->
                    Log.e("TAG", "Error deleting post", exception)
                    Toast.makeText(
                        requireContext(),
                        "Failed to delete post: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("TAG", "Error getting documents", exception)
            Toast.makeText(
                requireContext(), "Failed to delete post: ${exception.message}", Toast.LENGTH_SHORT
            ).show()
        }


        query2.addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                document.reference.delete().addOnSuccessListener {
                    val post = postList.find { it.postUrl == postUrl }
                    post?.let {
                        postList.remove(it)
                        adapter.notifyDataSetChanged()
                    }
                }.addOnFailureListener { exception ->
                    Log.e("TAG", "Error deleting post", exception)
                    Toast.makeText(
                        requireContext(),
                        "Failed to delete post: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("TAG", "Error getting documents", exception)
            Toast.makeText(
                requireContext(), "Failed to delete post: ${exception.message}", Toast.LENGTH_SHORT
            ).show()
        }


    }


    companion object
}