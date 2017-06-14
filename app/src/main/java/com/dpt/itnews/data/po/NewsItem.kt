package com.dpt.itnews.data.po

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import retrofit2.http.GET

/**
 * Created by dupengtao on 17/6/13.
 */
@Root(name = "NewsBody", strict = false)
class NewsItem {

    //    @set:Element(name = "title", required = false)
    //    @get:Element(name = "title", required = false)
    //    var title: String? = null

    @get:Element(name = "Title")
    @set:Element(name = "Title")
    var title: String? = null

    @get:Element(name = "SourceName")
    @set:Element(name = "SourceName")
    var sourceName: String? = null

    @get:Element(name = "SubmitDate")
    @set:Element(name = "SubmitDate")
    var submitDate: String? = null

    @get:Element(name = "Content")
    @set:Element(name = "Content")
    var content: String? = null

    @get:Element(name = "PrevNews")
    @set:Element(name = "PrevNews")
    var prevNews: Int? = null

    @get:Element(name = "NextNews")
    @set:Element(name = "NextNews")
    var nextNews: Int? = null

    @get:Element(name = "CommentCount")
    @set:Element(name = "CommentCount")
    var commentCount: Int? = null

    override fun toString(): String {
        return "NewsItem(title=$title, sourceName=$sourceName, submitDate=$submitDate, content=$content, prevNews=$prevNews, nextNews=$nextNews, commentCount=$commentCount)"
    }


}