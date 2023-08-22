package com.zrh.android.common.utils

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.*

/**
 *
 * @author zrh
 * @date 2023/8/22
 *
 */
object LocaleUtils {
    private const val LOCALE_KEY = "app_locale"

    private lateinit var application: Application
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var locale: Locale

    fun init(application: Application, sharedPreferences: SharedPreferences) {
        this.application = application
        this.sharedPreferences = sharedPreferences
        this.locale = getStoredLocale()
        refreshLocale()
    }

    private fun refreshLocale() {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))
    }

    private fun getStoredLocale(): Locale {
        val tag = sharedPreferences.getString(LOCALE_KEY, null)
        if (tag != null) {
            try {
                return Locale.forLanguageTag(tag)
            } catch (_: Exception) {
            }
        }
        return Locale.getDefault()
    }

    fun getLocale(): Locale {
        return locale
    }

    fun setLocale(locale: Locale) {
        this.locale = locale
        sharedPreferences.edit().putString(LOCALE_KEY, locale.toLanguageTag()).apply()
        refreshLocale()
    }

    fun followSystem() {
        this.locale = Locale.getDefault()
        sharedPreferences.edit().remove(LOCALE_KEY).apply()
        refreshLocale()
    }

    fun getSystemLocale() = Locale.getDefault()
}