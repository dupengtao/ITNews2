package com.dpt.itnews.data.po

import com.google.gson.annotations.SerializedName

/**
 * Created by dupengtao on 17/6/21.
 */
data class UpgradeInfo(@SerializedName("fileUrl") val fileUrl: String?, @SerializedName("description") val description: String?, @SerializedName("updatedAt") val updatedAt: String?, @SerializedName("createdAt") val createdAt: String?, @SerializedName("objectId") val objectId: String?, @SerializedName("fileMd5") val fileMd5: String?, @SerializedName("upgradeType") val upgradeType: Int?, @SerializedName("apkVersion") val apkVersion: Int?)