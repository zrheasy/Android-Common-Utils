package com.zrh.android.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zrh.android.common.utils.LocaleUtils
import com.zrh.android.common.utils.databinding.ActivityLanguageBinding
import com.zrh.android.common.utils.databinding.ActivityMainBinding
import com.zrh.android.common.utils.onClick
import com.zrh.android.common.widgets.BindingActivity
import java.util.*

class LanguageActivity : BindingActivity<ActivityLanguageBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnEnglish.onClick {
            LocaleUtils.setLocale(Locale.ENGLISH)
        }

        binding.btnChinese.onClick {
            LocaleUtils.setLocale(Locale.CHINESE)
        }

        binding.btnJapanese.onClick {
            LocaleUtils.setLocale(Locale.JAPANESE)
        }
    }
}