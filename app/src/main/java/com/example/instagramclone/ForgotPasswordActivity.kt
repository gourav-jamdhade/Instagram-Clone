@file:Suppress("DEPRECATION")

package com.example.instagramclone

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.instagramclone.Utils.USER_NODE
import com.example.instagramclone.databinding.ActivityForgotPasswordBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore


    val binding: ActivityForgotPasswordBinding by lazy {
        ActivityForgotPasswordBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        binding.buttonReset.setOnClickListener {
            val email = binding.textFieldEmail.editText?.text.toString()
            val emailConfirm = binding.textFieldEmailConfirm.editText?.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Please enter your email",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!email.equals(emailConfirm)) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Check your email address",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                isEmailRegistered(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val isRegistered = task.result
                        Log.d("isRegistered","isRegistered $isRegistered")
                        if (isRegistered) {
                            // Email is registered, proceed with sending reset email
                            sendPasswordResetEmail(email)
                        } else {
                            // Email is not registered
                            Toast.makeText(this@ForgotPasswordActivity, "This email is not registered", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Handle Firestore query failure
                        Toast.makeText(this@ForgotPasswordActivity, "Failed to check email registration", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun sendPasswordResetEmail(email: String) {
        // Send password reset email


        // Send password reset email
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful()) {
                    // Password reset email sent successfully
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Password reset email sent",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    // Failed to send password reset email
                    if (task.getException() is FirebaseAuthInvalidUserException) {
                        // Email address is not registered
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "This email is not registered",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Other error occurred
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Failed to send reset email. Please try again later.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun isEmailRegistered(email: String): Task<Boolean> {
        val query = Firebase.firestore.collection(USER_NODE)
            .whereEqualTo("email", email)
            .limit(1) // Limiting to 1 result as we only need to check if any document exists

        return query.get().continueWith { task ->
            if (task.isSuccessful) {
                val querySnapshot = task.result
                !querySnapshot.isEmpty
            } else {
                // Handle query failure
                false
            }
        }
    }
}

