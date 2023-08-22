package com.zrh.android.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.telephony.TelephonyManager

/**
 *
 * @author zrh
 * @date 2023/8/21
 *
 */
@SuppressLint("StaticFieldLeak")
object NetUtils : ConnectivityManager.NetworkCallback() {
    const val NETWORK_TYPE_UNKNOWN = 0
    const val NETWORK_TYPE_WIFI = 1
    const val NETWORK_TYPE_LTE = 2
    const val NETWORK_TYPE_MOBILE = 3

    private lateinit var context: Context
    private val listeners = HashSet<NetworkListener>()
    private var networkType = NETWORK_TYPE_UNKNOWN

    fun init(context: Context) {
        NetUtils.context = context.applicationContext

        networkType = getNetworkType()

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(request, this)
    }

    fun addNetworkListener(listener: NetworkListener) {
        listeners.add(listener)
    }

    fun removeNetworkListener(listener: NetworkListener) {
        listeners.remove(listener)
    }

    fun isNetworkAvailable(): Boolean {
        return getNetworkType() != NETWORK_TYPE_UNKNOWN
    }

    fun isWifi(): Boolean {
        return getNetworkType() == NETWORK_TYPE_WIFI
    }

    fun is4G(): Boolean {
        return getNetworkType() == NETWORK_TYPE_LTE
    }

    fun is3G(): Boolean {
        return getNetworkType() == NETWORK_TYPE_MOBILE
    }

    fun getNetworkType(): Int {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val caps = connectivityManager.getNetworkCapabilities(network) ?: return NETWORK_TYPE_UNKNOWN
            if (!caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) return NETWORK_TYPE_UNKNOWN

            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return NETWORK_TYPE_WIFI
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return NETWORK_TYPE_LTE
            return NETWORK_TYPE_UNKNOWN
        }

        val networkInfo = connectivityManager.activeNetworkInfo ?: return NETWORK_TYPE_UNKNOWN
        val type = networkInfo.type
        if (type == ConnectivityManager.TYPE_WIFI) return NETWORK_TYPE_WIFI
        if (type == ConnectivityManager.TYPE_MOBILE) return NETWORK_TYPE_MOBILE
        if (type == TelephonyManager.NETWORK_TYPE_LTE) return NETWORK_TYPE_LTE
        return NETWORK_TYPE_UNKNOWN
    }

    /**
     * 有network在线会触发，但不代表之前的网络是离线的，当默认网络有变化时才进行通知
     */
    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        val type = getNetworkType()
        if (networkType != type) {
            networkType = type
            notifyNetworkAvailable(type)
        }
    }

    /**
     * 有network离线会触发，但并不代表默认网络离线，例如当前连接wifi和4G，4G离线但wifi在线
     */
    override fun onLost(network: Network) {
        val type = getNetworkType()
        if (type == NETWORK_TYPE_UNKNOWN){
            networkType = type
            notifyNetworkLost()
        }
    }

    private fun notifyNetworkLost() {
        listeners.forEach { it.onLost() }
    }

    private fun notifyNetworkAvailable(type: Int) {
        listeners.forEach { it.onAvailable(type) }
    }
}

interface NetworkListener {
    fun onLost()
    fun onAvailable(type: Int)
}