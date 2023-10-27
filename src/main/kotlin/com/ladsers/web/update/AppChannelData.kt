package com.ladsers.web.update

import kotlinx.serialization.Serializable

/**
 * Template class for JSON parsing.
 */
@Serializable
internal class AppChannelData(
    val stable: String? = null,
    val check: String? = null
)