package com.zrh.android.common

import android.os.Bundle
import com.zrh.android.common.event.CounterEvent
import com.zrh.android.common.utils.AndroidBus
import com.zrh.android.common.utils.databinding.ActivityCounterBinding
import com.zrh.android.common.utils.onClick
import com.zrh.android.common.widgets.BindingActivity

/**
 *
 * @author zrh
 * @date 2023/8/23
 *
 */
class CounterActivity:BindingActivity<ActivityCounterBinding>() {

    private var num = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnAdd.onClick {
            num++
            AndroidBus.publish(CounterEvent(num))
        }

        binding.btnSubtract.onClick {
            num--
            AndroidBus.publish(CounterEvent(num))
        }

        AndroidBus.receiver(this){
            subscribe(CounterEvent::class.java){
                binding.tvCount.text = it.num.toString()
            }
        }
    }

}