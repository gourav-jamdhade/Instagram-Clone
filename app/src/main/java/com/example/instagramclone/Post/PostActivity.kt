package com.example.instagramclone.Post

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.instagramclone.HomeActivity
import com.example.instagramclone.Models.Post
import com.example.instagramclone.Models.User
import com.example.instagramclone.Utils.POST
import com.example.instagramclone.Utils.POST_Folder
import com.example.instagramclone.Utils.USER_NODE
import com.example.instagramclone.Utils.uploadImage
import com.example.instagramclone.databinding.ActivityPostBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class PostActivity : AppCompatActivity() {

    private val binding: ActivityPostBinding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }

    lateinit var user: User
    private var imageUrl: String? = null
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {

            uploadImage(uri, POST_Folder) { url ->
                if (url != null) {
                    binding.uploadImage.setImageURI(uri)
                    imageUrl = url
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setSupportActionBar(binding.materialToolbar);


        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
        setContentView(binding.root)



        binding.uploadImage.setOnClickListener {
            launcher.launch("image/*")
        }



        binding.buttonPost.setOnClickListener {

            Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    var user = it.toObject<User>()!!

                    if (imageUrl != null) {
                        val post: Post = Post(
                            postUrl = imageUrl!!,
                            caption = binding.textFieldCaption.editText?.text.toString(),
                            uid = Firebase.auth.currentUser!!.uid.toString(),
                            time = System.currentTimeMillis().toString()
                        )

                        Firebase.firestore.collection(POST).document().set(post)
                            .addOnSuccessListener {
                                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid)
                                    .document().set(post).addOnSuccessListener {
                                        startActivity(
                                            Intent(
                                                this@PostActivity, HomeActivity::class.java
                                            )
                                        )
                                        finish()
                                    }
                            }
                    } else {
                        Toast.makeText(
                            this@PostActivity,
                            "Please select an image before posting",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }


        // Set up onBackPressedCallback
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate back to the parent activity
                startActivity(Intent(this@PostActivity, HomeActivity::class.java))
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }


}