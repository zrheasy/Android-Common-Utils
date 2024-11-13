package com.zrh.android.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
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
import java.io.File

class AnimActivity : BindingActivity<ActivityAnimBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initVapView()

        binding.btnVap.onClick {
            playVap()
        }

    }

    private fun initVapView() {
        binding.vapView.setScaleType(ScaleType.CENTER_CROP)

        // 回调在VAP子线程中
        binding.vapView.setAnimListener(object : IAnimListener{
            override fun onFailed(errorType: Int, errorMsg: String?) {
                Log.d("VAP", "onFailed: $errorType $errorMsg ${Thread.currentThread()}")
            }

            override fun onVideoComplete() {
                Log.d("VAP", "onVideoComplete ${Thread.currentThread()}")
            }

            override fun onVideoDestroy() {
                Log.d("VAP", "onVideoDestroy ${Thread.currentThread()}")
            }

            override fun onVideoRender(frameIndex: Int, config: AnimConfig?) {

            }

            override fun onVideoStart() {
                Log.d("VAP", "onVideoStart  ${Thread.currentThread()}")
            }

        })
        binding.vapView.setFetchResource(object : IFetchResource{
            override fun fetchImage(resource: Resource, result: (Bitmap?) -> Unit) {
                if (resource.tag.isNotEmpty()){
                    val options = BitmapFactory.Options()
                    options.inScaled = false
                    result(BitmapFactory.decodeResource(resources, R.mipmap.head, options))
                }else{
                    result.invoke(null)
                }
                Log.d("VAP", "fetchImage: ${resource.tag}  ${Thread.currentThread()}")
            }

            override fun fetchText(resource: Resource, result: (String?) -> Unit) {
                result.invoke("恭喜King成功升级！")
                Log.d("VAP", "fetchText: ${resource.tag}  ${Thread.currentThread()}")
            }

            override fun releaseResource(resources: List<Resource>) {
                resources.forEach {
                    it.bitmap?.recycle()
                }
            }
        })
    }

    private fun playVap() {
//        binding.vapView.startPlay(assetManager = assets, "vapx.mp4")


        val url = "http://smvuqx8z8.hn-bkt.clouddn.com/devvapx.mp4?e=1731490855&token=7pyxNuVMvpNtJZyxNAHwKMuonQcvOAsEC3pwT_Y8:bYWi58FUuEN3qOXfTlKkU3ADOhw="
        GlideApp.with(this).asFile()
            .listener(object : RequestListener<File>{
                override fun onLoadFailed(
                    p0: GlideException?,
                    p1: Any?,
                    p2: Target<File>,
                    p3: Boolean
                ): Boolean {
                    Log.d("Glide", "onLoadFailed")
                    return false
                }

                override fun onResourceReady(
                    file: File,
                    p1: Any,
                    p2: Target<File>?,
                    p3: DataSource,
                    p4: Boolean
                ): Boolean {
                    Log.d("Glide", "onResourceReady:${file.path}")
                    binding.vapView.startPlay(file)
                    return false
                }
            })
            .load(url)
            .submit()
    }
}