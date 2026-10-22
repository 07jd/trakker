package com.jd.trakker.data.local

import android.util.Base64
import androidx.room.TypeConverter

class ListConverter {
    @TypeConverter
    fun fromStringList(list: List<String?>?): String {
        if (list.isNullOrEmpty()) return ""

        return list.filterNotNull().joinToString(",") {
            Base64.encodeToString(it.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        }
    }

    @TypeConverter
    fun toStringList(raw: String?): List<String> {
        if (raw.isNullOrEmpty()) return emptyList()

        return raw.split(",").map {
            String(Base64.decode(it, Base64.DEFAULT), Charsets.UTF_8)
        }
    }

    @TypeConverter
    fun fromIntString(list: List<Int?>?): String {
        if (list.isNullOrEmpty()) return ""

        return list.filterNotNull().joinToString(",")
    }

    @TypeConverter
    fun toIntString(raw: String?): List<Int> {
        if (raw.isNullOrEmpty()) return emptyList()

        return raw.split(",").toList().map { it.toInt() }
    }
}