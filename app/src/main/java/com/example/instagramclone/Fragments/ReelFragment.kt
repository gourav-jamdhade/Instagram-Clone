package com.example.instagramclone.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.instagramclone.Adapters.ReelAdapter
import com.example.instagramclone.Models.Reel
import com.example.instagramclone.Utils.REEL
import com.example.instagramclone.databinding.FragmentReelBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject


class ReelFragment : Fragment() {


    val binding by lazy {
        FragmentReelBinding.inflate(layoutInflater)
    }

    lateinit var adapter: ReelAdapter
    var reelList = ArrayList<Reel>()
    private var lastReelPosition = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        adapter = ReelAdapter(requireContext(), reelList)
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                Log.d("ViewPager","$position")
                lastReelPosition = position
                checkLastReel(position)
            }
        })

        binding.viewPager.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val lastReelIndex = reelList.size - 1
                if (lastReelPosition == lastReelIndex) {
                    // User touched down on the last reel
                    Toast.makeText(
                        requireContext(),
                        "No more REELS",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            false
        }

        Firebase.firestore.collection(REEL).get()
            .addOnSuccessListener {
                var tempList = ArrayList<Reel>()
                reelList.clear()
                for (i in it.documents) {
                    var reel = i.toObject<Reel>()!!
                    tempList.add(reel)
                }

                reelList.addAll(tempList)
                checkReelList()
                adapter.notifyDataSetChanged()


            }


        return binding.root
    }

    private fun checkLastReel(position: Int) {
        val lastReelIndex = reelList.size - 1
        if (position == lastReelIndex) {
            // User has reached the last reel
            lastReelPosition = position
        }
    }

    private fun checkReelList() {
        if (reelList.isEmpty()) {
            binding.textViewNoReels.visibility = View.VISIBLE
        } else {
            binding.textViewNoReels.visibility = View.GONE
        }
    }

    companion object {

    }

    override fun onResume() {
        Log.d("onResume Reel Fragement", "onResume Reel Fragement")
        super.onResume()

    }

    

}