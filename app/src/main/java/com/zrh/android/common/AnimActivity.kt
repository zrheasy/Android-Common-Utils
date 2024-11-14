package com.zrh.android.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.opensource.svgaplayer.SVGACallback
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import com.tencent.qgame.animplayer.AnimConfig
import com.tencent.qgame.animplayer.inter.IAnimListener
import com.tencent.qgame.animplayer.inter.IFetchResource
import com.tencent.qgame.animplayer.mix.Resource
import com.tencent.qgame.animplayer.util.ScaleType
import com.zrh.android.common.glide.GlideApp
import com.zrh.android.common.utils.databinding.ActivityAnimBinding
import com.zrh.android.common.utils.onClick
import com.zrh.android.common.widgets.BindingActivity
import com.zrh.android.common.utils.R
import org.libpag.PAGImageView
import org.libpag.PAGImageView.PAGImageViewListener
import java.io.File
import java.io.FileInputStream

class AnimActivity : BindingActivity<ActivityAnimBinding>() {

    private var pagIndex = 0
    private val pagResources = arrayOf(
        "http://smvuqx8z8.hn-bkt.clouddn.com/excuse-1685519153531.pag.pag.pag?e=1731504439&token=7pyxNuVMvpNtJZyxNAHwKMuonQcvOAsEC3pwT_Y8:sWe32KWzkdeaWWLHF1GCBV7r-oQ=",
        "http://smvuqx8z8.hn-bkt.clouddn.com/OMG.pag.pag?e=1731503219&token=7pyxNuVMvpNtJZyxNAHwKMuonQcvOAsEC3pwT_Y8:Fc_EnPgo5HECZN2CeiimLIQHq90=",
    )
    private val pagListener = object : PAGImageViewListener {
        override fun onAnimationStart(view: PAGImageView?) {
            Log.d("PAG", "onAnimationStart ${Thread.currentThread().name}")
        }

        override fun onAnimationEnd(view: PAGImageView?) {
            Log.d("PAG", "onAnimationEnd ${Thread.currentThread().name}")
            runOnUiThread { view?.isVisible = false }
        }

        override fun onAnimationCancel(view: PAGImageView?) {
            Log.d("PAG", "onAnimationCancel ${Thread.currentThread().name}")
            runOnUiThread { view?.isVisible = false }
        }

        override fun onAnimationRepeat(view: PAGImageView?) {
            Log.d("PAG", "onAnimationRepeat ${Thread.currentThread().name}")
        }

        override fun onAnimationUpdate(view: PAGImageView?) {
            Log.d("PAG", "onAnimationUpdate ${Thread.currentThread().name}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initVapView()
        initPagView()
        initSvgaView()

        binding.btnVap.onClick {
            playVap()
        }

        binding.btnPag.onClick {
            playPag()
        }

        binding.btnSvga.onClick {
            playSvga()
        }

    }

    private fun playSvga() {
        val url = "https://img.sugartimeapp.com/gifts/manager-a0c9bbee-0a21-499f-a833-f0c136834969.svga"
        download(url){
            SVGAParser.shareParser().decodeFromInputStream(FileInputStream(it), url, callback = object : SVGAParser.ParseCompletion{
                override fun onComplete(videoItem: SVGAVideoEntity) {
                    binding.svgaView.setVideoItem(videoItem)
                    binding.svgaView.startAnimation()
                }

                override fun onError() {
                    Log.d("SVGA", "onFailed ${Thread.currentThread().name}")
                }
            }, closeInputStream = true)
        }
    }

    private fun initSvgaView() {
        SVGAParser.shareParser().init(this)
        binding.svgaView.clearsAfterDetached = true
        binding.svgaView.loops = 1
        binding.svgaView.fillMode = SVGAImageView.FillMode.Clear
        binding.svgaView.scaleType = ImageView.ScaleType.CENTER_CROP
        binding.svgaView.callback = object : SVGACallback{
            override fun onFinished() {
                Log.d("SVGA", "onFinished ${Thread.currentThread().name}")
            }

            override fun onPause() {
                Log.d("SVGA", "onPause ${Thread.currentThread().name}")
            }

            override fun onRepeat() {

            }

            override fun onStep(frame: Int, percentage: Double) {

            }

        }
    }

    private fun playPag() {
        val url = pagResources[pagIndex++ % pagResources.size]
        download(url) {
            binding.pagView.setPath(it.path)
            binding.pagView.play()
            binding.pagView.isVisible = true
        }
    }

    private fun initPagView() {
        binding.pagView.setRepeatCount(1)
        binding.pagView.addListener(pagListener)
    }

    private fun initVapView() {
        binding.vapView.setScaleType(ScaleType.CENTER_CROP)

        // 回调在VAP子线程中
        binding.vapView.setAnimListener(object : IAnimListener {
            override fun onFailed(errorType: Int, errorMsg: String?) {
                Log.d("VAP", "onFailed: $errorType $errorMsg ${Thread.currentThread().name}")
            }

            override fun onVideoComplete() {
                Log.d("VAP", "onVideoComplete ${Thread.currentThread().name}")
            }

            override fun onVideoDestroy() {
                Log.d("VAP", "onVideoDestroy ${Thread.currentThread().name}")
            }

            override fun onVideoRender(frameIndex: Int, config: AnimConfig?) {

            }

            override fun onVideoStart() {
                Log.d("VAP", "onVideoStart  ${Thread.currentThread().name}")
            }

        })
        binding.vapView.setFetchResource(object : IFetchResource {
            override fun fetchImage(resource: Resource, result: (Bitmap?) -> Unit) {
                if (resource.tag.isNotEmpty()) {
                    val options = BitmapFactory.Options()
                    options.inScaled = false
                    result(BitmapFactory.decodeResource(resources, R.mipmap.head, options))
                } else {
                    result.invoke(null)
                }
                Log.d("VAP", "fetchImage: ${resource.tag}  ${Thread.currentThread().name}")
            }

            override fun fetchText(resource: Resource, result: (String?) -> Unit) {
                result.invoke("恭喜King成功升级！")
                Log.d("VAP", "fetchText: ${resource.tag}  ${Thread.currentThread().name}")
            }

            override fun releaseResource(resources: List<Resource>) {
                resources.forEach {
                    it.bitmap?.recycle()
                }
            }
        })
    }

    private fun playVap() {
        val url =
            "http://smvuqx8z8.hn-bkt.clouddn.com/devvapx.mp4?e=1731490855&token=7pyxNuVMvpNtJZyxNAHwKMuonQcvOAsEC3pwT_Y8:bYWi58FUuEN3qOXfTlKkU3ADOhw="
        download(url) {
            binding.vapView.startPlay(it)
        }
    }

    private fun download(url: String, onSuccess: (file: File) -> Unit) {
        GlideApp.with(this).asFile()
            .listener(object : RequestListener<File> {
                override fun onLoadFailed(
                    p0: GlideException?,
                    p1: Any?,
                    p2: Target<File>,
                    p3: Boolean
                ): Boolean {
                    Log.d("Glide", "onLoadFailed ${Thread.currentThread().name}")
                    return false
                }

                override fun onResourceReady(
                    file: File,
                    p1: Any,
                    p2: Target<File>?,
                    p3: DataSource,
                    p4: Boolean
                ): Boolean {
                    Log.d("Glide", "onResourceReady:${file.path} ${Thread.currentThread().name}")
                    runOnUiThread { onSuccess(file) }
                    return false
                }
            })
            .load(url)
            .submit()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.pagView.removeListener(pagListener)
    }
}