package com.zrh.android.common

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.zrh.android.common.event.CounterEvent
import com.zrh.android.common.utils.*
import com.zrh.android.common.utils.databinding.ActivityMainBinding
import com.zrh.android.common.widgets.BindingActivity
import com.zrh.android.common.widgets.RainLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter

class MainActivity : BindingActivity<ActivityMainBinding>() {

    private lateinit var mRainLayout: RainLayout
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

        binding.btnPhysicsLayout.onClick {
            startActivity(Intent(this, PhysicsActivity::class.java))
        }

        binding.btnRainLayout.onClick {
            startActivity(Intent(this, RainActivity::class.java))
        }

        binding.btnMarqueeLayout.onClick {
            startActivity(Intent(this, MarqueeActivity::class.java))
        }

        binding.btnExpandText.onClick {
            startActivity(Intent(this, ExpandTextActivity::class.java))
        }

        binding.btnHeartView.onClick {
            startActivity(Intent(this, HeartViewActivity::class.java))
        }

        binding.btnWhatsapp.onClick {
            startActivity(Intent(this, WhatsappActivity::class.java))
        }

        binding.btnDownload.onClick {
            download()
        }

        AndroidBus.receiver(this) {
            subscribe(CounterEvent::class.java) {
                toast("收到事件通知：$it")
            }
        }

    }

    private fun download() {
        GlobalScope.launch(Dispatchers.IO){
            try {
                val file = File(cacheDir, "test.txt")
                val fileWriter = FileWriter(file)
                fileWriter.write("Hello World!")
                fileWriter.close()
                FileUtils.saveFileToDownload(applicationContext, file, "text/*", "Common")
                withContext(Dispatchers.Main){toast("Success")}
            } catch (e: Exception) {
                withContext(Dispatchers.Main){toast("Error: $e")}
            }
        }
    }
}