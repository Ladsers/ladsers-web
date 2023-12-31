package com.ladsers.web.update

/**
 * Enumeration of application platforms.
 */
enum class Platform(internal val key: String) {
    ANDROID("android"),
    WEAROS("wearos"),
    JVM("jvm"),
    LINUX("linux"),
    MACOS("macos"),
    WINDOWS("windows"),
    OTHER("other")
}