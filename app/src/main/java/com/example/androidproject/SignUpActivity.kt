package com.example.androidproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase



class SignUpActivity : AppCompatActivity(){
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_sign_up)

            val signupbtn = findViewById<Button>(R.id.sign_up_button)
            signupbtn.setOnClickListener {
                signUp("a@a.com","123456")
            }
        }

        private fun signUp(userEmail: String, password: String) {
            Firebase.auth.createUserWithEmailAndPassword(userEmail, password)
                .addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        println("성공")
                    } else {
                        println("실패")
                    }
                }
        }
}