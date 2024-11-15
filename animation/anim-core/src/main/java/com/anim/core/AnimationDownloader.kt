package com.anim.core

import android.content.Context
import java.io.File

interface AnimationDownloader {
    fun download(
        context: Context,
        url: String,
        onError: ((code: Int, msg: String) -> Unit)? = null,
        onSuccess: (file: File) -> Unit
    )
}