package com.zrh.android.common

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import com.zrh.android.common.env.AppEnvManager
import com.zrh.android.common.utils.LocaleUtils
import com.zrh.android.common.utils.setSystemBar

/**
 *
 * @author zrh
 * @date 2023/8/22
 *
 */
class App : Application() ,ActivityLifecycleCallbacks {

    override fun onCreate() {
        super.onCreate()

        LocaleUtils.init(this, getSharedPreferences("setting", Context.MODE_PRIVATE))

        registerActivityLifecycleCallbacks(this)

        AppEnvManager.init(this, BuildConfig.DEBUG)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activity.setSystemBar()
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}