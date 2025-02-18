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
import com.example.instagramclone.Adapters.MyReelRVAdapter
import com.example.instagramclone.Models.Reel
import com.example.instagramclone.R
import com.example.instagramclone.Utils.REEL
import com.example.instagramclone.databinding.FragmentMyReelsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class MyReelsFragment : Fragment() {

    val reelList = ArrayList<Reel>()
    private lateinit var adapter: MyReelRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private val binding by lazy {
        FragmentMyReelsBinding.inflate(layoutInflater)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment


        Log.d("MY REELS", "FRAGMENT")
        adapter = MyReelRVAdapter(requireContext(), reelList)
        fetchReels()


        binding.rv.layoutManager =
            StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_between_items)
        binding.rv.addItemDecoration(VerticalSpaceItemDecoration(spacingInPixels))
        binding.rv.adapter = adapter

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+REEL).get().addOnSuccessListener {
            var tempList = arrayListOf<Reel>()
            for (i in it.documents) {
                var reel: Reel = i.toObject<Reel>()!!
                tempList.add(reel)
            }

            reelList.clear()
            reelList.addAll(tempList)
            adapter.notifyDataSetChanged()
        }

        adapter.setOnReelLongPressListener(object : MyReelRVAdapter.OnReelLongPressListener {
            override fun onLongPress(reel: Reel) {
                showLongPressDialog(reel)
            }
        })

        return binding.root

    }

    private fun showLongPressDialog(reel: Reel) {
        val options = arrayOf("Delete", "Share")

        AlertDialog.Builder(requireContext()).setTitle("Choose Action")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showDeleteConfirmationDialog(reel)
                    1 -> shareReel(reel)
                }
            }
            .show()
    }

    private fun showDeleteConfirmationDialog(reel: Reel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Reel")
            .setMessage("Are you sure you want to delete this reel?")
            .setPositiveButton("Yes") { _, _ ->
                deleteReel(reel.reelUrl)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun shareReel(reel: Reel) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, "${reel.caption}\n${reel.reelUrl}")
// Check if there's an activity that can handle the shareIntent
        if (shareIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(Intent.createChooser(shareIntent, "Share reel via").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Add this flag
            })
        } else {
            // Handle the case where there's no activity to handle the shareIntent
            Toast.makeText(requireContext(), "No app found to handle sharing", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun deleteReel(reelUrl: String) {



        val query = Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + REEL)
            .whereEqualTo("reelUrl", reelUrl)
            .get()

        val query2 = Firebase.firestore.collection(REEL)
            .whereEqualTo("reelUrl", reelUrl)
            .get()
        query.addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                document.reference.delete().addOnSuccessListener {
                    val reel = reelList.find { it.reelUrl == reelUrl }
                    reel?.let {
                        reelList.remove(it)
                        adapter.notifyDataSetChanged()
                    }
                }.addOnFailureListener { exception ->
                    Log.e("TAG", "Error deleting reel", exception)
                    Toast.makeText(
                        requireContext(),
                        "Failed to delete reel: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("TAG", "Error getting documents", exception)
            Toast.makeText(
                requireContext(),
                "Failed to delete reel: ${exception.message}",
                Toast.LENGTH_SHORT
            ).show()
        }

        query2.addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                document.reference.delete().addOnSuccessListener {
                    Toast.makeText(requireContext(), "Reel Deleted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onPause() {
        Log.d("onPause", "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d("onStop", "onStop")
        super.onStop()
    }


    override fun onStart() {
        Log.d("onStart", "onStart")
        fetchReels()
        adapter.setOnReelLongPressListener(object : MyReelRVAdapter.OnReelLongPressListener {
            override fun onLongPress(reel: Reel) {
                showLongPressDialog(reel)
            }
        })
        super.onStart()


    }

    override fun onResume() {
        Log.d("onResume", "onResume")
        fetchReels()
        adapter.notifyDataSetChanged()



        adapter.setOnReelLongPressListener(object : MyReelRVAdapter.OnReelLongPressListener {
            override fun onLongPress(reel: Reel) {
                showLongPressDialog(reel)
            }
        })
        super.onResume()
    }

    private fun fetchReels() {
        Log.d("onResume", "onResume")



        binding.rv.layoutManager =
            StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_between_items)
        binding.rv.addItemDecoration(VerticalSpaceItemDecoration(spacingInPixels))
        binding.rv.adapter = adapter
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + REEL).get()
            .addOnSuccessListener {
                Log.d("FireStore onResume", it.documents.toString())
                var tempList = arrayListOf<Reel>()
                for (i in it.documents) {
                    var reel: Reel = i.toObject<Reel>()!!
                    tempList.add(reel)
                }

                reelList.clear()
                reelList.addAll(tempList)
                adapter.notifyDataSetChanged()


            }
    }


    companion object {

    }


}
