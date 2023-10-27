package com.ladsers.web.update

/**
 * Enumeration of distribution channels.
 */
enum class Channel {
    /**
     * The main channel. It is used to get stable versions of applications.
     */
    STABLE,

    /**
     * Channel intended for checking the update system.
     */
    CHECK
}