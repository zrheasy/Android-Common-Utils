package com.anim.vap

import com.anim.core.AnimationComponent
import com.anim.core.AnimationComponentFactory

class VapComponentFactory : AnimationComponentFactory {
    override fun createComponent(): AnimationComponent {
        return VapComponent()
    }

}