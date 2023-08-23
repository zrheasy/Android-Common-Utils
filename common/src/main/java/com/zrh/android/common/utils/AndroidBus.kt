package com.zrh.android.common.utils

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 *
 * @author zrh
 * @date 2023/8/23
 *
 */
object AndroidBus {

    private val registry = HashMap<Class<*>, MutableList<EventSubscriber<*>>>()

    @Synchronized
    internal fun <T> subscribe(eventClass: Class<T>, subscriber: EventSubscriber<T>) {
        val subscribers: MutableList<EventSubscriber<*>>
        if (registry.containsKey(eventClass)) {
            subscribers = registry[eventClass]!!
        } else {
            subscribers = ArrayList()
            registry[eventClass] = subscribers
        }
        if (!subscribers.contains(subscriber))
            subscribers.add(subscriber)
    }

    @Synchronized
    internal fun unsubscribe(eventClass: Class<*>, subscriber: EventSubscriber<*>) {
        registry[eventClass]?.remove(subscriber)
    }

    @Synchronized
    fun <T> publish(event: T) {
        registry[event!!::class.java]?.forEach {
            (it as EventSubscriber<T>).onEvent(event)
        }
    }

    fun receiver(lifecycleOwner: LifecycleOwner, block: Receiver.() -> Unit) {
        Receiver(lifecycleOwner).block()
    }

    class Receiver(lifecycleOwner: LifecycleOwner): LifecycleEventObserver {

        init {
            lifecycleOwner.lifecycle.addObserver(this)
        }

        private val subscribers = HashMap<Class<*>, EventSubscriber<*>>()
        private val mainHandler = Handler(Looper.getMainLooper())

        fun <T> subscribe(eventClass: Class<T>, onEvent: (T) -> Unit) {
            val subscriber = object : EventSubscriber<T> {
                override fun onEvent(event: T) {
                    if (Looper.myLooper() == Looper.getMainLooper()){
                        onEvent(event)
                    }else{
                        mainHandler.post { onEvent(event) }
                    }
                }
            }
            subscribe(eventClass, subscriber)
            subscribers[eventClass] = subscriber
        }

        private fun clean() {
            subscribers.forEach {
                unsubscribe(it.key, it.value)
            }
            subscribers.clear()
            mainHandler.removeCallbacksAndMessages(null)
        }

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                source.lifecycle.removeObserver(this)
                clean()
            }
        }
    }

    interface EventSubscriber<T> {
        fun onEvent(event: T)
    }
}

