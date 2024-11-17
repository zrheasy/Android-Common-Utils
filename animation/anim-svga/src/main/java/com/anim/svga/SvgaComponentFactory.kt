package com.anim.svga

import android.content.Context
import com.anim.core.AnimationComponent
import com.anim.core.AnimationComponentFactory
import com.opensource.svgaplayer.SVGASoundManager

class SvgaComponentFactory : AnimationComponentFactory {
    override fun createComponent(): AnimationComponent {
        return SvgaComponent()
    }

    override fun onInit(context: Context) {
        SVGASoundManager.init()
        SVGASoundManager.setVolume(1f)
    }

    override fun onRelease() {
        SVGASoundManager.release()
    }
}