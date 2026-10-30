package com.jd.trakker.data.local

import android.util.Base64
import androidx.room.TypeConverter
import kotlin.text.toByteArray

class ListConverter {
    @TypeConverter
    fun fromStringList(list: List<String>): String {
        if (list.isEmpty()) return ""

        return list.joinToString(",") {
            Base64.encodeToString(it.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        }
    }

    @TypeConverter
    fun toStringList(raw: String): List<String> {
        if (raw.isEmpty()) return emptyList()

        return raw.split(",").map {
            String(Base64.decode(it, Base64.DEFAULT), Charsets.UTF_8)
        }
    }

    @TypeConverter
    fun fromIntString(list: List<Int>): String {
        if (list.isEmpty()) return ""

        return list.joinToString(",")
    }

    @TypeConverter
    fun toIntString(raw: String): List<Int> {
        if (raw.isEmpty()) return emptyList()

        return raw.split(",").toList().map { it.toInt() }
    }

    @TypeConverter
    fun fromStringPairList(list: List<Pair<String, String>>): String {
        if (list.isEmpty()) return ""

        return list.joinToString(";") {
            "${Base64.encodeToString(it.first.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)}," +
            "${Base64.encodeToString(it.second.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)}"
        }
    }

    @TypeConverter
    fun toStringPairList(raw: String): List<Pair<String, String>> {
        if(raw.isEmpty() || raw.isBlank()) return emptyList()

        return raw.split(";").map {
            val parts = it.split(",")

            String(Base64.decode(parts[0], Base64.DEFAULT), Charsets.UTF_8) to
            String(Base64.decode(parts[1], Base64.DEFAULT), Charsets.UTF_8)
        }
    }
}