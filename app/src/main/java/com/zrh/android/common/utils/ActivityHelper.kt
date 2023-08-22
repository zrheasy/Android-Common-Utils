package com.zrh.android.common.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 *
 * @author zrh
 * @date 2023/8/22
 *
 */
object ActivityHelper : Application.ActivityLifecycleCallbacks{
    interface OnForegroundListener{
        fun onForeground(isForeground: Boolean)
    }

    private val foregroundListeners = ArrayList<OnForegroundListener>()
    private val activities = ArrayList<Activity>()
    private var activeCount = 0

    fun init(app: Application) {
        app.registerActivityLifecycleCallbacks(this)
    }

    fun addOnForegroundListener(listener: OnForegroundListener){
        if (!foregroundListeners.contains(listener)){
            foregroundListeners.add(listener)
        }
    }

    fun removeForegroundListener(listener: OnForegroundListener){
        foregroundListeners.remove(listener)
    }

    private fun notifyForeground(isForeground: Boolean){
        val listeners = ArrayList(foregroundListeners)
        listeners.forEach {
            it.onForeground(isForeground)
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activities.add(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        if (activeCount == 0){
            notifyForeground(true)
        }
        activeCount++
    }


    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        if (activeCount == 1){
            notifyForeground(false)
        }
        activeCount--
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        activities.remove(activity)
    }

    /**
     * 结束所有已启动的activity
     */
    @JvmStatic
    fun finishAll() {
        val list = ArrayList(activities)
        list.forEach { it.finish() }
        activities.clear()
    }

    /**
     * 结束指定的activity
     * @see finishActivity(String)
     */
    @JvmStatic
    fun finishActivity(clazz: Class<*>) {
        var activity: Activity? = null
        activities.forEach {
            if (it::class.java == clazz) {
                activity = it
                return@forEach
            }
        }

        activity?.let {
            it.finish()
            activities.remove(it)
        }
    }

    /**
     * 获取顶部的activity
     */
    @JvmStatic
    fun getTop(): Activity? {
        return getActivity(1)
    }

    /**
     * 获取倒数第order位的activity
     */
    @JvmStatic
    fun getActivity(order: Int): Activity? {

        if (activities.size >= order) {
            return activities[activities.size - order]
        }

        return null
    }
}