package com.example.instagramclone

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.instagramclone.Models.User
import com.example.instagramclone.Utils.USER_NODE
import com.example.instagramclone.Utils.USER_PROFILE_FOLDER
import com.example.instagramclone.Utils.uploadImage
import com.example.instagramclone.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class EditProfileActivity : AppCompatActivity() {


    private val binding: ActivityEditProfileBinding by lazy {
        ActivityEditProfileBinding.inflate(layoutInflater)
    }


    private lateinit var currentUser: User
    private var selectedImageUri: Uri? = null

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                uploadImage(uri, USER_PROFILE_FOLDER) {
                    if (it != null) {
                        currentUser.image = it
                        // Invalidate cache
                        Picasso.get().invalidate(currentUser.image)
                        // Load new image
                        binding.profileImageEdit.setImageURI(uri)
                    }
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        //fetch the current user
        fetchCurrentUser()

        binding.buttonUpdate.setOnClickListener {
            saveChanges()
        }

        binding.editBtn.setOnClickListener {
            chooseImageFromGallery()
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }

    }

    private fun saveChanges() {
        val newName = binding.textFieldName.editText?.text.toString()
        val newEmail = binding.textFieldEmail.editText?.text.toString()


        val currentUserRef = Firebase.firestore.collection(USER_NODE)
            .document(Firebase.auth.currentUser!!.uid)
        if (newEmail != currentUser.email) {

            Firebase.firestore.collection(USER_NODE)
                .whereEqualTo("email", newEmail)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        currentUserRef.update(
                            mapOf(
                                "name" to newName,
                                "email" to newEmail,
                                "image" to currentUser.image
                            )
                        ).addOnSuccessListener {
                            Toast.makeText(
                                this@EditProfileActivity,
                                "Profile Updated Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }.addOnFailureListener { exception ->
                            Toast.makeText(
                                this@EditProfileActivity,
                                "Failed to update profile: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Email address already in use",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(
                        this@EditProfileActivity,
                        "Failed to check email availability: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            currentUserRef.update(
                mapOf(
                    "name" to newName,
                    "image" to currentUser.image
                )
            ).addOnSuccessListener {
                Toast.makeText(
                    this@EditProfileActivity,
                    "Profile Updated Successfully",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    this@EditProfileActivity,
                    "Failed to update profile: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun chooseImageFromGallery() {
        try {
            getContent.launch("image/*")
        } catch (e: SecurityException) {
            // Handle permission denied exception
            Toast.makeText(
                this,
                "Permission denied by user",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun fetchCurrentUser() {
        val currentUserRef =
            Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid)

        currentUserRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                currentUser = document.toObject<User>()!!

                binding.textFieldName.editText?.setText(currentUser.name)
                binding.textFieldEmail.editText?.setText(currentUser.email)


                if (currentUser.image != null)
                    Picasso.get().load(currentUser.image).into(binding.profileImageEdit)


            }

        }
    }

}
