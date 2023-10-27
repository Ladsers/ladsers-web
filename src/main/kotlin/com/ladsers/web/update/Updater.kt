package com.ladsers.web.update

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL
import java.util.*

/**
 * Object responsible for receiving information for updating applications from the server.
 */
object Updater {
    /**
     * Get the new application version tag from the server.
     *
     * For various failures it should also return null, because in all apps, failure behavior is similar
     * to behavior when there is no new version.
     */
    fun getNewVersionTag(
        product: String,
        platform: Platform,
        currentVerTag: String? = null,
        channel: Channel = Channel.STABLE
    ): String? {

        val source = URL("""https://ladsers.com/checkupdate/$product-${platform.key}/""")

        /* Get data from file on the remote server */
        val jsonData: String
        try {
            val scanner = Scanner(source.openStream()).useDelimiter("\\Z")
            jsonData = scanner.next()
            scanner.close()
        } catch (e: Exception) {
            return null
        }

        /* Parse the received data */
        val appChannelData: AppChannelData
        try {
            val json = Json { ignoreUnknownKeys = true }
            appChannelData = json.decodeFromString<AppChannelData>(jsonData)
        } catch (e: Exception) {
            return null
        }

        /* Get the version tag */
        val candidateVerTag = when (channel) {
            Channel.STABLE -> appChannelData.stable
            Channel.CHECK -> appChannelData.check
        }

        /* Compare tags */
        return if (isVersionHigher(currentVerTag, candidateVerTag)) candidateVerTag else null
    }

    /**
     * Get link to download the application of the specified version (or the latest one) from the server.
     */
    fun getDownloadLink(
        product: String,
        platform: Platform,
        verTag: String? = null,
        channel: Channel = Channel.STABLE
    ): String? {

        val source = URL("""https://ladsers.com/updatelink/$product-${platform.key}/""")

        /* Get the tag if it is missing and required */
        val versionTag =
            if (channel == Channel.CHECK) null else verTag ?: getNewVersionTag(product, platform, null, channel)
            ?: return null

        /* Get data from file on the remote server */
        val jsonData: String
        try {
            val scanner = Scanner(source.openStream()).useDelimiter("\\Z")
            jsonData = scanner.next()
            scanner.close()
        } catch (e: Exception) {
            return null
        }

        /* Parse the received data */
        val appChannelData: AppChannelData
        try {
            val json = Json { ignoreUnknownKeys = true }
            appChannelData = json.decodeFromString<AppChannelData>(jsonData)
        } catch (e: Exception) {
            return null
        }

        /* Get the version tag */
        return when (channel) {
            Channel.STABLE -> appChannelData.stable?.replace("%version%", versionTag!!)
            Channel.CHECK -> appChannelData.check
        }
    }

    /**
     * Is the candidate tag higher than the current tag?
     * @return True, if higher. False, if equal or lower.
     */
    @Throws(NumberFormatException::class, IllegalArgumentException::class)
    fun isVersionHigher(
        currentVerTag: String?,
        candidateVerTag: String?
    ): Boolean {

        if (candidateVerTag == null) return false
        else if (currentVerTag == null) return true

        fun parseTag(tag: String) = tag.split('.').map(String::toInt)

        val currentParsed = parseTag(currentVerTag)
        val candidateParsed = parseTag(candidateVerTag)

        // Tags should have the following format: year.month.number
        if (currentParsed.size != 3 || candidateParsed.size != 3) throw IllegalArgumentException()

        fun compare(current: Int, candidate: Int): Boolean? {
            return if (candidate == current) null
            else candidate > current
        }

        return compare(currentParsed[0], candidateParsed[0]) ?: compare(currentParsed[1], candidateParsed[1])
        ?: compare(currentParsed[2], candidateParsed[2]) ?: false
    }
}