package com.ladsers.web.update

object Updater {
    fun getNewVersionTag(
        product: String,
        platform: Platform,
        currentVerTag: String? = null,
        channel: Channel = Channel.STABLE
    ): String? {
        return null //todo
    }

    fun getDownloadLink(
        product: String,
        platform: Platform,
        verTag: String? = null,
        channel: Channel = Channel.STABLE
    ): String? {

        val versionTag = verTag ?: getNewVersionTag(product, platform, null, channel) ?: return null

        return null //todo
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