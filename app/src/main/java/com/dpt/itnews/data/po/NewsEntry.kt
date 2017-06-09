package com.dpt.itnews.data.po

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

/**
 * Created by dupengtao on 17/3/16.
 */
@Root(name = "entry", strict = false)
class NewsEntry {


    @set:Element(name = "id")
    @get:Element(name = "id")
    var id: Int = 0

    @set:Element(name = "title", required = false)
    @get:Element(name = "title", required = false)
    var title: String? = null

    @get:Element(name = "summary", required = false)
    @set:Element(name = "summary", required = false)
    var summary: String? = null

    @set:Element(name = "published", required = false)
    @get:Element(name = "published", required = false)
    var published: String? = null

    @set:Element(name = "topicIcon", required = false)
    @get:Element(name = "topicIcon", required = false)
    var topicIcon: String? = null

    @set:Element(name = "sourceName", required = false)
    @get:Element(name = "sourceName", required = false)
    var sourceName: String? = null

    @set:Element(name = "views", required = false)
    @get:Element(name = "views", required = false)
    var views: String? = null

    override fun toString(): String {
        return "NewsEntry(id=$id, title=$title, summary=$summary, published=$published, topicIcon=$topicIcon, sourceName=$sourceName, views=$views)"
    }


}
