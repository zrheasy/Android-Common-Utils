package com.zrh.android.common.env

/**
 *
 * @author zrh
 * @date 2024/4/8
 *
 */
class DebugEnv:BaseEnv() {
    override fun getBaseApiUrl(): String {
        return "https://devapi2.2fun.org"
    }

    override fun getBaseH5Url(): String {
        return "https://devwebv2.2fun.org/"
    }

    override fun getPublicGroupNumber(): String {
        return "9632"
    }

    override fun getOSSBucket(): String {
        return "sugartime-dev"
    }

    override fun getRtcAppId(): String {
        return "19b9d2c5ebfb4f51a3cd948c46f8187f"
    }

}