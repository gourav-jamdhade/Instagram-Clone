package com.example.instagramclone.Fragments


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instagramclone.Post.PostActivity
import com.example.instagramclone.Post.ReelsActivity
import com.example.instagramclone.R
import com.example.instagramclone.databinding.FragmentAddBinding
import com.example.instagramclone.databinding.FragmentProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AddFragment : BottomSheetDialogFragment() {
    private val binding:FragmentAddBinding by lazy {
        FragmentAddBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private var isDialogVisible = false

    override fun onDestroyView() {
        super.onDestroyView()
        if (isDialogVisible) {
            activity?.window?.decorView?.rootView?.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    override fun onStart() {
        super.onStart()
        isDialogVisible = true
    }

    override fun onStop() {
        super.onStop()
        isDialogVisible = false
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding.addPost.setOnClickListener{
            activity?.startActivity(Intent(requireContext(),PostActivity::class.java))
            activity?.finish()

        }

        binding.uploadReels.setOnClickListener {
            activity?.startActivity(Intent(requireContext(),ReelsActivity::class.java))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isDialogVisible) {
            activity?.window?.decorView?.rootView?.setBackgroundColor(Color.parseColor("#80000000"))
        }
    }



    companion object {

    }
}