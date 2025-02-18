package com.example.instagramclone

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.instagramclone.Models.User
import com.example.instagramclone.Utils.USER_NODE
import com.example.instagramclone.Utils.USER_PROFILE_FOLDER
import com.example.instagramclone.Utils.uploadImage
import com.example.instagramclone.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.security.MessageDigest
import java.util.regex.Pattern


class SignUpActivity : AppCompatActivity() {


    val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    lateinit var user: User
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {

            uploadImage(uri, USER_PROFILE_FOLDER) {
                if (it != null) {
                    user.image = it
                    binding.profileImage.setImageURI(uri)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        user = User()



        binding.signUpButton.setOnClickListener {


            val name = binding.textFieldName.editText?.text.toString().trim()
            val email = binding.textFieldEmail.editText?.text.toString().trim()
            val password = binding.textFieldPassword.editText?.text.toString().trim()

            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this@SignUpActivity, "Please fill all info!", Toast.LENGTH_SHORT)
                    .show()
            } else if (!isValidPassword(password)) {
                val alertDialogBuilder = AlertDialog.Builder(this@SignUpActivity)
                alertDialogBuilder.setTitle("Invalid Password")
                alertDialogBuilder.setMessage("Please enter a valid password.\nPassword must contain 1 alphabet, 1 special character & 1 digit")
                alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { result ->
                        if (result.isSuccessful) {

                            user.name = name
                            user.password = hashPassword(password)
                            user.email = email
                            Firebase.firestore.collection(USER_NODE)
                                .document(Firebase.auth.currentUser!!.uid).set(user)
                                .addOnSuccessListener {
                                    startActivity(
                                        Intent(
                                            this@SignUpActivity,
                                            HomeActivity::class.java
                                        )
                                    )
                                    finish()
                                }
                        } else {
                            Toast.makeText(
                                this@SignUpActivity,
                                result.exception?.localizedMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }


        binding.addImage.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.login.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
            finish()
        }

        val editTextPassword = binding.textFieldPassword.editText
        val passwordToggleDrawable = binding.textFieldPassword.endIconDrawable

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
                            binding.textFieldPassword.setEndIconDrawable(R.drawable.baseline_visibility_24)
                        } else {
                            // Hide password
                            editTextPassword.transformationMethod =
                                PasswordTransformationMethod.getInstance()
                            editTextPassword.setSelection(editTextPassword.text.length)
                            // passwordToggleDrawable.setTint(ContextCompat.getColor(this, R.color.textColorPrimary))
                            binding.textFieldPassword.setEndIconDrawable(R.drawable.baseline_visibility_off_24)
                        }
                        return@setOnTouchListener true
                    }
                }
                return@setOnTouchListener false
            }
        }
    }


    fun isValidPassword(password: String): Boolean {

        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        val pattern: Pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher = pattern.matcher(password)

        return matcher.matches()
    }

    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return bytesToHex(digest)
    }

    fun bytesToHex(bytes: ByteArray): String {
        val hexArray = "0123456789ABCDEF".toCharArray()
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v: Int = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }


}