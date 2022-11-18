package com.example.androidproject


import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidproject.databinding.ActivitySignUpBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val signupbtn = binding.signUpButton
        signupbtn.setOnClickListener {
            val id=binding.signUpEmailAddress.text.toString();
            val password=binding.signUpPassword1.text.toString();
            val password2=binding.signUpPassword2.text.toString();
            if(!Patterns.EMAIL_ADDRESS.matcher(id).matches()) {
                Toast.makeText(this, "올바른 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show()
            }
            else if(password.length<6){
                Toast.makeText(this, "비밀번호는 6자 이상", Toast.LENGTH_SHORT).show()
                binding.signUpPassword1.requestFocus();
            }else if(!password.equals(password2)){
                Toast.makeText(this, "비밀번호를 확인해주세요!", Toast.LENGTH_SHORT).show()
                binding.signUpPassword2.requestFocus();
            }
            else {
                signUp(id, password)
            }
        }
    }

    private fun signUp(userEmail: String, password: String)  {
        Firebase.auth.createUserWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    println("성공")
                } else {
                    Toast.makeText(this, "올바른 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show()
                    println("실패!")
                }
            }

    }
}