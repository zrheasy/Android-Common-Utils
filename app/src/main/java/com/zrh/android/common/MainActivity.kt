package com.zrh.android.common

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zrh.android.common.utils.databinding.ActivityMainBinding
import com.zrh.android.common.utils.onClick

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLanguage.onClick {
            startActivity(Intent(this, LanguageActivity::class.java))
        }
    }
}