package com.example.instagramclone

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.instagramclone.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var auth: FirebaseAuth
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.buttonLogin.setOnClickListener {
            val email = binding.textFieldNameLogin.editText?.text.toString().trim()
            val password = binding.textFieldpasswordLogin.editText?.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signInUser(email, password)
        }

        binding.signup.setOnClickListener{
            startActivity(Intent(this@LoginActivity,SignUpActivity::class.java))
            finish()
        }

        val editTextPassword = binding.textFieldpasswordLogin.editText
        val passwordToggleDrawable = binding.textFieldpasswordLogin.endIconDrawable

        passwordToggleDrawable?.let {
            editTextPassword?.setOnTouchListener { _, event ->
                val DRAWABLE_RIGHT = 2
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= editTextPassword.right - editTextPassword.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                        // Toggle password visibility
                        if (editTextPassword.transformationMethod == PasswordTransformationMethod.getInstance()) {
                            // Show password
                            editTextPassword.transformationMethod =
                                HideReturnsTransformationMethod.getInstance()
                            editTextPassword.setSelection(editTextPassword.text.length)
                            //passwordToggleDrawable.setTint(ContextCompat.getColor(this, R.color.colorAccent))
                            binding.textFieldpasswordLogin.setEndIconDrawable(R.drawable.baseline_visibility_24)
                        } else {
                            // Hide password
                            editTextPassword.transformationMethod =
                                PasswordTransformationMethod.getInstance()
                            editTextPassword.setSelection(editTextPassword.text.length)
                            // passwordToggleDrawable.setTint(ContextCompat.getColor(this, R.color.textColorPrimary))
                            binding.textFieldpasswordLogin.setEndIconDrawable(R.drawable.baseline_visibility_off_24)
                        }
                        return@setOnTouchListener true
                    }
                }
                return@setOnTouchListener false
            }
        }

        binding.forgotPassword.setOnClickListener{
            startActivity(Intent(this@LoginActivity,ForgotPasswordActivity::class.java))
        }


    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    // Navigate to main activity or next screen
                    // For example:
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    when (task.exception) {
                        is FirebaseAuthInvalidUserException -> {
                            // User does not exist
                            Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            // Invalid password
                            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            // Other error
                            Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

    }


}