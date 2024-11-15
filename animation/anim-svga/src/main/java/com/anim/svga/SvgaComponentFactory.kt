package com.anim.svga

import com.anim.core.AnimationComponent
import com.anim.core.AnimationComponentFactory

class SvgaComponentFactory:AnimationComponentFactory {
    override fun createComponent(): AnimationComponent {
        return SvgaComponent()
    }
}