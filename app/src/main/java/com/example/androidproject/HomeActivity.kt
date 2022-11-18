package com.example.androidproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.androidproject.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var navBar=binding.navigationView as BottomNavigationView
        supportFragmentManager.beginTransaction().add(R.id.navigationView, HomeFragment()).commit()

        navBar.setOnNavigationItemSelectedListener {item ->
            when(item.itemId) {
                R.id.homeFragment -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.FreindFragment -> {
                    replaceFragment(FriendFragment())
                    true
                }
                R.id.SearchFragment -> {
                    replaceFragment(SearchFragment())
                    true
                }
                else -> false
            }
        }
    }
}
