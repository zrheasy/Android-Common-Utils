package com.anim.pag

import com.anim.core.AnimationComponent
import com.anim.core.AnimationComponentFactory

class PagComponentFactory : AnimationComponentFactory {
    override fun createComponent(): AnimationComponent {
        return PagComponent()
    }
}