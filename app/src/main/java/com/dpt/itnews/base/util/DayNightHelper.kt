package com.dpt.itnews.base.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * Created by dupengtao on 17/6/19.
 */
class DayNightHelper(context: Context) {


    private val sharedPreferences: SharedPreferences

    init {
        this.sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    }

    companion object {

        private val FILE_NAME = "settings"
        private val MODE = "day_night_mode"
    }

    fun setMode(type: DayNight): Boolean {
        return sharedPreferences.edit().putString(MODE, type.name).commit()
    }

    fun isNight(): Boolean {
        val mode = sharedPreferences.getString(MODE, DayNight.DAY.name)

        return DayNight.Night.name == mode
    }

    fun isDay(): Boolean {
        val mode = sharedPreferences.getString(MODE, DayNight.DAY.name)

        return DayNight.DAY.name == mode
    }
}