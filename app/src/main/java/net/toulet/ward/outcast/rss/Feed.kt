package net.toulet.ward.outcast

import android.util.Log
import android.util.Xml
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.IllegalStateException

var client: OkHttpClient = OkHttpClient();

/**
 * Populate a feed from a uri
 */
suspend fun getFeed(uri: String): String {
    val response = client.newCall(Request.Builder().url(uri).build()).execute()
    val body = response.body?.string() ?: "No body";
    //Log.e("rss", body)

    // Using xml pull parser to extract data
    var feed = ChannelParser().parse(ByteArrayInputStream(body.encodeToByteArray()))

    Log.e("feed", feed.image.toString())

    return body
}

data class Image (
    val uri: String,
    val alt: String?,
)

data class Episode(
    var guid: String,
    var title: String,
    var author: String?,
    var descriptoin: String?,
    var audioUri: String,
)

data class RssFeed(
    var title: String,
    var episodes: List<Episode>,
    var image: Image?,
)

class ChannelParser {
    fun parse(input: InputStream): RssFeed {
       input.use { input ->
           val parser = Xml.newPullParser()
           parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
           parser.setInput(input, null)
           parser.nextTag()

           return readRss(parser)
       }
    }

    private fun readRss(parser: XmlPullParser): RssFeed {
        // Requires that the root is <rss> tag
        parser.require(XmlPullParser.START_TAG, null, "rss")
        parser.nextTag()

        // Inside the rss only channel is defined
        parser.require(XmlPullParser.START_TAG, null, "channel")
        return readChannel(parser)
    }

    private fun readChannel(parser: XmlPullParser): RssFeed {
        lateinit var title: String
        var image: Image? = null

        val episodes: MutableList<Episode> = mutableListOf()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "title" -> title = readTitle(parser)
                "image" -> image = readImage(parser)
                "item" -> episodes.add(readEpisode(parser))
                else -> skip(parser)
            }
        }

        return RssFeed(
            title = title,
            episodes = episodes,
            image = image,
        )
    }

    private fun readEpisode(parser: XmlPullParser): Episode {
        lateinit var guid: String
        lateinit var title: String
        lateinit var audioUri: String
        var author: String? = null
        var description: String? = null

        parser.require(XmlPullParser.START_TAG, null, "item")
        while (parser.next() != XmlPullParser.END_TAG) {
            if(parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "guid" -> guid = readGuid(parser)
                "title" -> title = readTitle(parser)
                "author" -> author = readAuthor(parser)
                "description" -> description = readDescription(parser)
                "enclosure" -> audioUri = readEnclosure(parser)
                else -> skip(parser)
            }
        }

        return Episode(guid, title, author, description, audioUri)
    }

    private fun readImage(parser: XmlPullParser): Image {
        lateinit var uri: String
        var alt: String? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            Log.d("rss, Inside channel", parser.name)
            when (parser.name) {
                "title" -> alt = readTitle(parser)
                "url" -> uri = readUrl(parser)
                else -> skip(parser)
            }
        }

        return Image(uri, alt)
    }

    private fun readGuid(parser: XmlPullParser): String {
        lateinit var guid: String
        parser.require(XmlPullParser.START_TAG, null, "guid")
        guid = readString(parser)
        parser.require(XmlPullParser.END_TAG, null, "guid")
        return guid
    }

    private fun readUrl(parser: XmlPullParser): String {
        lateinit var guid: String
        parser.require(XmlPullParser.START_TAG, null, "url")
        guid = readString(parser)
        parser.require(XmlPullParser.END_TAG, null, "url")
        return guid
    }

    private fun readDescription(parser: XmlPullParser): String {
        lateinit var guid: String
        parser.require(XmlPullParser.START_TAG, null, "description")
        guid = readString(parser)
        parser.require(XmlPullParser.END_TAG, null, "description")
        return guid
    }

    private fun readEnclosure(parser: XmlPullParser): String {
        lateinit var uri: String
        parser.require(XmlPullParser.START_TAG, null, "enclosure")
        uri = parser.getAttributeValue(null, "url")
        parser.require(XmlPullParser.START_TAG, null, "enclosure")
        parser.nextTag()
        return uri
    }

    private fun readTitle(parser: XmlPullParser): String {
        lateinit var title: String
        parser.require(XmlPullParser.START_TAG, null, "title")
        title = readString(parser)
        parser.require(XmlPullParser.END_TAG, null, "title")
        return title
    }

    private fun readAuthor(parser: XmlPullParser): String {
        lateinit var author: String
        parser.require(XmlPullParser.START_TAG, null, "author")
        author = readString(parser)
        parser.require(XmlPullParser.END_TAG, null, "author")
        return author
    }

    private fun readString(parser: XmlPullParser): String {
        lateinit var result: String

        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }

        return result
    }

    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}