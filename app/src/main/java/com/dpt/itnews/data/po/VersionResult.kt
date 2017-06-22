package com.dpt.itnews.data.po

import com.google.gson.annotations.SerializedName

/**
 * Created by dupengtao on 17/6/21.
 */
data class VersionResult(@SerializedName("results") val results : List<UpgradeInfo> = arrayListOf())