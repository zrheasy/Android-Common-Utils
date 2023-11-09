package com.zrh.android.common

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import com.zrh.android.common.utils.*
import com.zrh.android.common.utils.databinding.ActivityPhysicsLayoutBinding
import com.zrh.android.common.widgets.BindingActivity
import com.zrh.android.common.widgets.physicslayout.Physics
import com.zrh.android.common.widgets.physicslayout.PhysicsConfig
import com.zrh.android.common.widgets.physicslayout.Shape

/**
 *
 * @author zrh
 * @date 2023/11/9
 *
 */
class PhysicsActivity : BindingActivity<ActivityPhysicsLayoutBinding>(), SensorEventListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnAdd.onClick(interval = 0) {
            addBody()
        }

        addSensorListener(Sensor.TYPE_GRAVITY, this)
    }

    private fun addBody() {
        val size = dp2px(50f)
        val x = Math.random() * (getScreenSize()[0] - size)
        val view = ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(size, size)
            setImageResource(com.zrh.android.common.utils.R.mipmap.ic_launcher_round)
            translationX = x.toFloat()
        }

        Physics.setPhysicsConfig(view, PhysicsConfig().apply {
            shape = Shape.CIRCLE
            bodyDef.allowSleep = false
        })
        binding.physicsLayout.addView(view)
    }

    override fun onSensorChanged(event: SensorEvent) {
        binding.physicsLayout.physics.setGravity(-event.values[0], event.values[1])
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
        removeSensorListener(this)
    }
}