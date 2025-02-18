package com.example.instagramclone.Post

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.instagramclone.HomeActivity
import com.example.instagramclone.Models.Reel
import com.example.instagramclone.Models.User
import com.example.instagramclone.Utils.REEL
import com.example.instagramclone.Utils.REEL_Folder
import com.example.instagramclone.Utils.USER_NODE
import com.example.instagramclone.Utils.uploadVideo
import com.example.instagramclone.databinding.ActivityReelsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class ReelsActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityReelsBinding.inflate(layoutInflater)
    }

    private lateinit var videoUrl: String
    lateinit var progressDialog: ProgressDialog
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {

            uploadVideo(uri, REEL_Folder, progressDialog) { url ->
                if (url != null) {

                    videoUrl = url
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        binding.materialToolbar.setNavigationOnClickListener {
            startActivity(Intent(this@ReelsActivity, HomeActivity::class.java))
            finish()
        }

        binding.buttonCancel.setOnClickListener {
            startActivity(Intent(this@ReelsActivity, HomeActivity::class.java))
            finish()
        }

        binding.uploadReel.setOnClickListener {
            launcher.launch("video/*")
        }

        binding.buttonPost.setOnClickListener {
            if (videoUrl != null) {

                Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid)
                    .get().addOnSuccessListener {

                        Toast.makeText(this@ReelsActivity, "Reel Uploaded", Toast.LENGTH_SHORT).show()
                        var user: User = it.toObject<User>()!!
                        val reel =
                            Reel(videoUrl, binding.textFieldCaption.editText?.text.toString(),user.image.toString())

                        Firebase.firestore.collection(REEL).document().set(reel)
                            .addOnSuccessListener {
                                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + REEL)
                                    .document()
                                    .set(reel)
                                    .addOnSuccessListener {
                                        startActivity(
                                            Intent(
                                                this@ReelsActivity,
                                                HomeActivity::class.java
                                            )
                                        )
                                        finish()
                                    }
                            }
                    }
            }else {
                Toast.makeText(
                    this@ReelsActivity,
                    "Please upload a reel before posting",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }


}
