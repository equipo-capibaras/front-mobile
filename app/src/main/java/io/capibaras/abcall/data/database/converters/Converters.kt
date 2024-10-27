package io.capibaras.abcall.data.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.capibaras.abcall.data.database.models.History

class Converters {
    @TypeConverter
    fun fromHistoryList(history: List<History>): String {
        return Gson().toJson(history)
    }

    @TypeConverter
    fun toHistoryList(historyString: String): List<History> {
        val listType = object : TypeToken<List<History>>() {}.type
        return Gson().fromJson(historyString, listType)
    }
}
