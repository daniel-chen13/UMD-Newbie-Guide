package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterUser : AppCompatActivity() {

    lateinit var email: EditText
    lateinit var confPass: EditText
    private lateinit var pass: EditText
    private lateinit var signUp: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        email = findViewById(R.id.email)
        confPass = findViewById(R.id.confirmpassword)
        pass = findViewById(R.id.password)
        signUp = findViewById(R.id.register)

        auth = Firebase.auth

        signUp.setOnClickListener {
            signUpUser()
        }
    }

    private fun signUpUser() {
        val email = email.text.toString()
        val pass = pass.text.toString()
        val confirmPassword = confPass.text.toString()

        if (email.isBlank() || pass.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }
        if (pass != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (pass.length < 6) {
            Toast.makeText(this, "Passwords must be 6 or more characters", Toast.LENGTH_SHORT).show()
            return
        }
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully Signed Up", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoggedInSuccessfullyHomepage::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Sign Up Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}