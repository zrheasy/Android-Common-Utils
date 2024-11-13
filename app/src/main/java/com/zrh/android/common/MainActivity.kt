package com.zrh.android.common

import android.content.Intent
import android.os.Bundle
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.kit.performance.PerformanceDokitView
import com.didichuxing.doraemonkit.kit.performance.PerformanceDokitViewManager
import com.didichuxing.doraemonkit.kit.performance.PerformanceFragment
import com.didichuxing.doraemonkit.kit.toolpanel.ToolPanelDoKitView
import com.zrh.android.common.env.AppEnvManager
import com.zrh.android.common.event.CounterEvent
import com.zrh.android.common.utils.*
import com.zrh.android.common.utils.databinding.ActivityMainBinding
import com.zrh.android.common.widgets.BindingActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter

class MainActivity : BindingActivity<ActivityMainBinding>() {

    private val mDialogQueueManager by lazy { DialogQueueManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidBus.receiver(this) {
            subscribe(CounterEvent::class.java) {
                toast("收到事件通知：$it")
            }
        }

        val env = AppEnvManager.getEnv()
        val envType = if (AppEnvManager.isDebugEnv()) "debug" else "release"
        binding.tvEnv.text =
            "env: $envType\n" + "apiBaseUrl: ${env.getBaseApiUrl()}\n" + "h5BaseUrl: ${env.getBaseH5Url()}"

        binding.btnPerformance.onClick {
            DoKit.showToolPanel()
        }

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

        binding.btnScrollView.onClick {
            startActivity(Intent(this, ScrollViewActivity::class.java))
        }

        binding.btnSwitchEnv.onClick {
            switchEnv()
        }

        binding.btnDialogQueue.onClick {
            showDialogQueue()
        }

        binding.btnVerticalPager.onClick {
            startActivity(Intent(this, VerticalPagerActivity::class.java))
        }

        binding.btnOutline.onClick {
            startActivity(Intent(this, OutlineActivity::class.java))
        }

        binding.btnBanner.onClick {
            startActivity(Intent(this, BannerActivity::class.java))
        }

        binding.btnAnim.onClick {
            startActivity(Intent(this, AnimActivity::class.java))
        }
    }

    private fun showDialogQueue() {
        mDialogQueueManager.showDialog(1, TestDialog().apply { count = 1 })
        mDialogQueueManager.showDialog(3, TestDialog().apply { count = 3 })
        mDialogQueueManager.showDialog(2, TestDialog().apply { count = 2 })
        mDialogQueueManager.showDialog(4, TestDialog().apply { count = 4 })
    }

    private fun switchEnv() {
        if (AppEnvManager.isDebugEnv()) {
            AppEnvManager.switchReleaseEnv()
        } else {
            AppEnvManager.switchDebugEnv()
        }
        AppUtils.restart(this, MainActivity::class.java)
    }

    private fun download() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val file = File(cacheDir, "test.txt")
                val fileWriter = FileWriter(file)
                fileWriter.write("Hello World!")
                fileWriter.close()
                FileUtils.saveFileToDownload(applicationContext, file, "text/*", "Common")
                withContext(Dispatchers.Main) { toast("Success") }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { toast("Error: $e") }
            }
        }
    }
}