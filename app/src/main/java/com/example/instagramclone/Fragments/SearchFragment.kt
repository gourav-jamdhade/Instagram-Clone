package com.example.instagramclone.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagramclone.Adapters.SearchAdapter
import com.example.instagramclone.Models.User
import com.example.instagramclone.R
import com.example.instagramclone.Utils.USER_NODE
import com.example.instagramclone.databinding.FragmentSearchBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.util.Locale


class SearchFragment : Fragment() {

    val binding: FragmentSearchBinding by lazy {
        FragmentSearchBinding.inflate(layoutInflater)
    }

    lateinit var adapter: SearchAdapter
    var userList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("DefaultLocale")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = SearchAdapter(requireContext(), userList)
        binding.rv.adapter = adapter

        Firebase.firestore.collection(USER_NODE).get().addOnSuccessListener {
            var tempList = ArrayList<User>()

            userList.clear()
            for (i in it.documents) {

                if (i.id != Firebase.auth.currentUser!!.uid) {
                    var user = i.toObject<User>()!!
                    tempList.add(user)
                }


            }

            userList.addAll(tempList)
            adapter.notifyDataSetChanged()

        }



        binding.imageButtonSearch.setOnClickListener {


            val searchText = binding.searchView2.text.toString()
                .lowercase(Locale.getDefault()) // Convert search query to lowercase

            Firebase.firestore.collection(USER_NODE)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val tempList = ArrayList<User>()
                    userList.clear()

                    for (document in querySnapshot.documents) {
                        val user = document.toObject<User>()

                        // Convert the name in the document to lowercase for case-insensitive comparison
                        val name = user?.name?.toLowerCase()

                        if (name != null && name.contains(searchText)) { // Case-insensitive comparison
                            if (!document.id.equals(Firebase.auth.currentUser!!.uid)) {
                                tempList.add(user)
                            }
                        }
                    }

                    if (tempList.isEmpty()) {
                        binding.rv.setBackgroundResource(R.color.white)
                        binding.textViewRv.visibility = View.VISIBLE
                    } else {
                        userList.addAll(tempList)
                        adapter.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        requireContext(),
                        "Error searching for users: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }



        return binding.root
    }

    companion object {

    }

}

