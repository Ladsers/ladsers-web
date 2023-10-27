package com.ladsers.web.update

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL
import java.util.*

object Updater {
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

    fun getDownloadLink(
        product: String,
        platform: Platform,
        verTag: String? = null,
        channel: Channel = Channel.STABLE
    ): String? {

        val source = URL("""https://ladsers.com/updatelink/$product-${platform.key}/""")


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

        if (currentParsed.size != 3 || candidateParsed.size != 3) throw IllegalArgumentException()

        fun compare(current: Int, candidate: Int): Boolean? {
            return if (candidate == current) null
            else candidate > current
        }

        return compare(currentParsed[0], candidateParsed[0]) ?: compare(currentParsed[1], candidateParsed[1])
        ?: compare(currentParsed[2], candidateParsed[2]) ?: false
    }
}