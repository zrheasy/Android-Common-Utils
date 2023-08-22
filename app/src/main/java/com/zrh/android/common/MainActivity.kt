package com.zrh.android.common

import android.content.Intent
import android.os.Bundle
import com.zrh.android.common.utils.databinding.ActivityMainBinding
import com.zrh.android.common.utils.onClick
import com.zrh.android.common.widgets.BindingActivity

class MainActivity : BindingActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnLanguage.onClick {
            startActivity(Intent(this, LanguageActivity::class.java))
        }
    }
}