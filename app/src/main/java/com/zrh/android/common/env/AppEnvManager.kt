package com.zrh.android.common.env

import android.content.Context
import com.zrh.android.common.utils.EnvManager

/**
 *
 * @author zrh
 * @date 2024/4/8
 *
 */
object AppEnvManager : EnvManager<BaseEnv>() {
    private var isDebug = false
    private const val ENV_DEBUG = "ENV_DEBUG"
    private const val ENV_RELEASE = "ENV_RELEASE"

    fun init(context: Context, isDebug: Boolean) {
        this.isDebug = isDebug

        val envSet = mutableMapOf<String, BaseEnv>()
        envSet[ENV_DEBUG] = DebugEnv()
        envSet[ENV_RELEASE] = ReleaseEnv()
        init(context, envSet)
    }

    fun switchDebugEnv() {
        switchEnv(ENV_DEBUG)
    }

    fun switchReleaseEnv() {
        switchEnv(ENV_RELEASE)
    }

    fun isDebugEnv(): Boolean = getEnvName() == ENV_DEBUG

    override fun getDefaultEnv(): String {
        return if (isDebug) ENV_DEBUG else ENV_RELEASE
    }
}