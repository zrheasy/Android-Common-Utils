package com.anim.core

/**
 * assets: file:///android_asset/image.jpg
 */
data class AnimResource(
    val type: AnimationType,
    val resourceUrl: String,
    val elements: List<AnimElement>? = null
) {
}

data class AnimElement(
    val type: ElementType,
    val key: String,
    val value: String
)
