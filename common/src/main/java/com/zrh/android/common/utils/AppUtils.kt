package com.zrh.android.common.utils

import android.app.ActivityManager
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Process
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 *
 * @author zrh
 * @date 2023/8/22
 *
 */
object AppUtils {

    private lateinit var application: Application

    fun init(application: Application){
        AppUtils.application = application
    }

    fun isMainProcess(): Boolean {
        return application.packageName.equals(getCurrentProcessName())
    }

    fun getCurrentProcessName(): String? {
        var name = getCurrentProcessNameByFile()
        if (!name.isNullOrEmpty()) return name
        name = getCurrentProcessNameByAms()
        if (!name.isNullOrEmpty()) return name
        name = getCurrentProcessNameByReflect()
        return name
    }

    private fun getCurrentProcessNameByFile(): String? {
        return try {
            val file = File("/proc/" + Process.myPid() + "/" + "cmdline")
            val mBufferedReader = BufferedReader(FileReader(file))
            val processName = mBufferedReader.readLine().trim { it <= ' ' }
            mBufferedReader.close()
            processName
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun getCurrentProcessNameByAms(): String? {
        try {
            val am = application.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val info = am.runningAppProcesses
            if (info == null || info.size == 0) return ""
            val pid = Process.myPid()
            for (aInfo in info) {
                if (aInfo.pid == pid) {
                    if (aInfo.processName != null) {
                        return aInfo.processName
                    }
                }
            }
        } catch (_: Exception) {
        }
        return ""
    }

    private fun getCurrentProcessNameByReflect(): String {
        var processName = ""
        try {
            val loadedApkField = application.javaClass.getField("mLoadedApk")
            loadedApkField.isAccessible = true
            val loadedApk = loadedApkField[application]
            val activityThreadField = loadedApk.javaClass.getDeclaredField("mActivityThread")
            activityThreadField.isAccessible = true
            val activityThread = activityThreadField[loadedApk]
            val getProcessName = activityThread.javaClass.getDeclaredMethod("getProcessName")
            processName = getProcessName.invoke(activityThread) as String
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return processName
    }

    fun openApp(packageName: String): Boolean {
        val manager = application.packageManager
        try {
            val intent = manager.getLaunchIntentForPackage(packageName) ?: return false
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            application.startActivity(intent)
            return true
        } catch (_: Exception) { }
        return false
    }

    fun openMarket(packageName: String) {
        try {
            application.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: Exception) {
            application.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
    }
}