package com.zrh.android.common

import android.os.Bundle
import com.anim.core.AnimResource
import com.anim.core.AnimationQueue
import com.anim.core.AnimationType
import com.zrh.android.common.utils.databinding.ActivityAnimBinding
import com.zrh.android.common.utils.onClick
import com.zrh.android.common.widgets.BindingActivity

class AnimActivity : BindingActivity<ActivityAnimBinding>() {
    
    private val mAnimQueue = AnimationQueue()

    private var pagIndex = 0
    private val pagResources = arrayOf(
        "http://smvuqx8z8.hn-bkt.clouddn.com/excuse-1685519153531.pag.pag.pag?e=1731504439&token=7pyxNuVMvpNtJZyxNAHwKMuonQcvOAsEC3pwT_Y8:sWe32KWzkdeaWWLHF1GCBV7r-oQ=",
        "http://smvuqx8z8.hn-bkt.clouddn.com/OMG.pag.pag?e=1731503219&token=7pyxNuVMvpNtJZyxNAHwKMuonQcvOAsEC3pwT_Y8:Fc_EnPgo5HECZN2CeiimLIQHq90=",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initAnimView()

        binding.btnVap.onClick {
            playVap()
        }

        binding.btnPag.onClick {
            playPag()
        }

        binding.btnSvga.onClick {
            playSvga()
        }

        binding.btnGif.onClick {
            playGif()
        }

    }

    private fun initAnimView() {
        mAnimQueue.setView(binding.animView)
    }

    private fun playGif() {
        val url = "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExY2N0MHY0OGY1dGh5ZDFsbWZoNXlvczRyb3hoOHMwc2pjc2pveGdiaSZlcD12MV9naWZzX3NlYXJjaCZjdD1n/37nPfslihaoussRbcC/giphy.gif"
        mAnimQueue.enqueue(AnimResource(AnimationType.GIF, url))
    }

    private fun playSvga() {
        val url = "https://img.sugartimeapp.com/gifts/manager-a0c9bbee-0a21-499f-a833-f0c136834969.svga"
        mAnimQueue.enqueue(AnimResource(AnimationType.SVGA, url))
    }


    private fun playPag() {
        val url = pagResources[pagIndex++ % pagResources.size]
        mAnimQueue.enqueue(AnimResource(AnimationType.PAG, url))
    }

    private fun playVap() {
//        val url = "http://smvuqx8z8.hn-bkt.clouddn.com/devvapx.mp4?e=1731490855&token=7pyxNuVMvpNtJZyxNAHwKMuonQcvOAsEC3pwT_Y8:bYWi58FUuEN3qOXfTlKkU3ADOhw="
        val url = "file:///android_asset/vapx.mp4"
        mAnimQueue.enqueue(AnimResource(AnimationType.VAP, url))
    }


    override fun onDestroy() {
        super.onDestroy()
        mAnimQueue.destroy()
    }
}