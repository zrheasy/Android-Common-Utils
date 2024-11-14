package com.zrh.android.common.anim

/**
 * assets: file:///android_asset/image.jpg
 */
data class AnimResource(
    val type: String,
    val resourceUrl: String,
    val elements: List<AnimElement>? = null
) {
    companion object {
        const val TYPE_NONE = "NONE"
        const val TYPE_GIF = "GIF"
        const val TYPE_SVGA = "SVGA"
        const val TYPE_PAG = "PAG"
        const val TYPE_VAP = "VAP"

        const val ELEMENT_TEXT = "TEXT"
        const val ELEMENT_IMAGE = "IMAGE"

    }
}

data class AnimElement(
    val type: String,
    val key: String,
    val value: String
)
