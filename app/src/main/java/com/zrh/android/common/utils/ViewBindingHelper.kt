package com.zrh.android.common.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 *
 * @author zrh
 * @date 2023/8/20
 *
 */
object ViewBindingHelper {

    /**
     * 根据泛型参数自动创建ViewBinding
     * 可用于BaseActivity、BaseFragment、BaseAdapter等基类的封装
     */
    fun <VB : ViewBinding> binding(
        target:Any,
        layoutInflater: LayoutInflater,
        parent: ViewGroup? = null,
        attachToParent: Boolean = false
    ): VB {
        var genericSuperclass = target.javaClass.genericSuperclass
        var superclass = target.javaClass.superclass
        while (superclass != null) {
            if (genericSuperclass is ParameterizedType) {
                genericSuperclass.actualTypeArguments.forEach {
                    try {
                        val clazz = it as Class<VB>
                        val method = clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
                        return method.invoke(null, layoutInflater, parent, attachToParent) as VB
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            genericSuperclass = superclass.genericSuperclass
            superclass = superclass.superclass
        }
        throw IllegalArgumentException("ViewBinding not found! -->$target")
    }
}