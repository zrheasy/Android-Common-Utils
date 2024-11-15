package com.anim.gif

import com.anim.core.AnimationComponent
import com.anim.core.AnimationComponentFactory

class GifComponentFactory : AnimationComponentFactory {
    override fun createComponent(): AnimationComponent {
        return GifComponent()
    }
}