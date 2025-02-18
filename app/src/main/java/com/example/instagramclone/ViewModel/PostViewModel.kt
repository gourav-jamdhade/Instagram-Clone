package com.example.instagramclone.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PostViewModel:ViewModel() {

    val likedPosts: MutableLiveData<Set<String>> by lazy {
        MutableLiveData<Set<String>>()
    }
}