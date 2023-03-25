package com.surajmanshal.bcapracticaladmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.surajmanshal.bcapracticaladmin.activity.AddBooksActivity
import com.surajmanshal.bcapracticaladmin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnAddBook.setOnClickListener {
            startActivity(Intent(this,AddBooksActivity::class.java))
        }
    }
}