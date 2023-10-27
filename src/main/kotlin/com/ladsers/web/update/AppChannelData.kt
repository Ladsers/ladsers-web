package com.ladsers.web.update

import kotlinx.serialization.Serializable

@Serializable
internal class AppChannelData(
    val stable: String? = null,
    val check: String? = null
)