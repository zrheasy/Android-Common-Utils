package com.zrh.android.common.env

/**
 *
 * @author zrh
 * @date 2024/4/8
 *
 */
class ReleaseEnv:BaseEnv() {
    override fun getBaseApiUrl(): String {
        return "https://api2.2fun.org"
    }

    override fun getBaseH5Url(): String {
        return "https://webv2.2fun.org/"
    }

    override fun getPublicGroupNumber(): String {
        return "9631"
    }

    override fun getOSSBucket(): String {
        return "sugartime-pord"
    }

    override fun getRtcAppId(): String {
        return "02f989a849b949119b7fab7857d278ee"
    }

}