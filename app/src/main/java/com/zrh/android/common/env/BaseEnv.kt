package com.zrh.android.common.env


/**
 *
 * @author zrh
 * @date 2024/4/8
 *
 */
abstract class BaseEnv {
    abstract fun getBaseApiUrl(): String
    abstract fun getBaseH5Url(): String
    abstract fun getPublicGroupNumber(): String
    abstract fun getOSSBucket(): String
    abstract fun getRtcAppId(): String
}