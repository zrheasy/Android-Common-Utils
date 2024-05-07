package com.zrh.android.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

/**
 *
 * @author zrh
 * @date 2024/4/8
 *
 */
abstract class EnvManager<T> {

    private val KEY_ENV_NAME = "KEY_ENV_NAME"

    private lateinit var mSharedPreferences: SharedPreferences
    private val mEnvSet = mutableMapOf<String, T>()

    fun init(context: Context, envSet: Map<String, T>) {
        mSharedPreferences = context.getSharedPreferences("EnvManager", Context.MODE_PRIVATE)
        mEnvSet.putAll(envSet)
    }

    @SuppressLint("ApplySharedPref")
    fun switchEnv(env: String) {
        mSharedPreferences.edit().putString(KEY_ENV_NAME, env).commit()
    }

    fun getEnvName(): String {
        val env = mSharedPreferences.getString(KEY_ENV_NAME, null)
        if (env == null) {
            val defaultEnv = getDefaultEnv()
            switchEnv(defaultEnv)
            return defaultEnv
        }
        return env
    }

    fun getEnv(): T {
        val env = getEnvName()
        return mEnvSet[env]!!
    }

    abstract fun getDefaultEnv(): String
}