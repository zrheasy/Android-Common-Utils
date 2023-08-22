package com.zrh.android.common

import android.app.Application
import android.content.Context
import com.zrh.android.common.utils.LocaleUtils

/**
 *
 * @author zrh
 * @date 2023/8/22
 *
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        LocaleUtils.init(this, getSharedPreferences("setting", Context.MODE_PRIVATE))
    }
}