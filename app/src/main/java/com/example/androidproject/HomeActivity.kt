package com.example.androidproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
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
                    replaceFragment(FreindFragment())
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
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment).commit()
    }

}