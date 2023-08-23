package com.zrh.android.common

import android.content.Intent
import android.os.Bundle
import com.zrh.android.common.event.CounterEvent
import com.zrh.android.common.utils.AndroidBus
import com.zrh.android.common.utils.databinding.ActivityMainBinding
import com.zrh.android.common.utils.onClick
import com.zrh.android.common.utils.toast
import com.zrh.android.common.widgets.BindingActivity

class MainActivity : BindingActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnLanguage.onClick {
            startActivity(Intent(this, LanguageActivity::class.java))
        }

        binding.btnList.onClick {
            startActivity(Intent(this, ListActivity::class.java))
        }

        binding.btnDialog.onClick {
            TextDialog(this).show()
        }

        binding.btnBottomDialog.onClick {
            BottomTextDialog(this).show()
        }

        binding.btnBus.onClick {
            startActivity(Intent(this, CounterActivity::class.java))
        }

        AndroidBus.receiver(this){
            subscribe(CounterEvent::class.java){
                toast("收到事件通知：$it")
            }
        }
    }
}