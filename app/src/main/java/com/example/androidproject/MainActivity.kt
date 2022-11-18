package com.example.androidproject


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.androidproject.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val login_button=binding.loginButton
        val sign_up_button=binding.signUpButton
        login_button.setOnClickListener {
            val id=binding.editTextTextPersonName.text.toString()//findViewById<EditText>(R.id.editTextTextPersonName)
            val password=binding.editTextTextPassword.text.toString()//findViewById<EditText>(R.id.editTextTextPassword)
            Firebase.auth.signInWithEmailAndPassword(id,password)
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                       //메인화면으로 이동
                    }
                    else {

//
                    }
                }
        }
        sign_up_button.setOnClickListener {
            val intent=Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
