package com.dpt.itnews.data.po

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

/**
 * Created by dupengtao on 17/3/16.
 */

@Root(name = "feed", strict = false)
class NewsList {

    @set:ElementList(required = true, inline = true, entry = "entry")
    @get:ElementList(required = true, inline = true, entry = "entry")
    var newsEntryList: List<NewsEntry>? = null

    override fun toString(): String {
        return "NewsList(newsEntryList=$newsEntryList)"
    }


}
