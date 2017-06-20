package com.dpt.itnews.data.po

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by dupengtao on 17/6/20.
 */
class VersionUpgradeInfo {

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("fileUrl")
    @Expose
    var fileUrl: String? = null

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

    @SerializedName("objectId")
    @Expose
    var objectId: String? = null

    @SerializedName("fileMd5")
    @Expose
    var fileMd5: String? = null

    @SerializedName("upgradeType")
    @Expose
    var upgradeType: Int? = null

    @SerializedName("apkVersion")
    @Expose
    var apkVersion: Int? = null

    override fun toString(): String {
        return "VersionUpgradeInfo(description=$description, fileUrl=$fileUrl, updatedAt=$updatedAt, createdAt=$createdAt, objectId=$objectId, fileMd5=$fileMd5, upgradeType=$upgradeType, apkVersion=$apkVersion)"
    }


}



//{
//    "results": [
//    {
//        "description": "版本测试3",
//        "upgradeType": 1,
//        "fileUrl": "https://www.baidu.com/",
//        "updatedAt": "2017-06-16T13:41:38.964Z",
//        "objectId": "5943dfc1ac502e5490c1a336",
//        "createdAt": "2017-06-16T13:40:17.295Z",
//        "fileMd5": "文件md5",
//        "apkVersion": 3
//    }
//    ]
//}