package com.example.androidproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.androidproject.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var navBar=binding.navigationView as BottomNavigationView

        navBar.setOnNavigationItemSelectedListener {item ->
            when(item.itemId) {
                R.id.homeFragment -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.FreindFragment -> {
                    //replaceFragment(FriendFragment())
                    startActivity(Intent(this,FriendActivity::class.java))
                    true
                }
                R.id.SearchFragment -> {
                    replaceFragment(SearchFragment())
                    true
                }
                else -> false
            }
        }


        binding.createContent.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                startActivity(Intent(this, WritingActivity::class.java))
                println("Asdasd")
            }
        }

        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

    }
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment).commit()
    }

}