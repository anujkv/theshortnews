package com.a3solution.theshortnews.data

import android.content.Context
import java.io.File

class SearchHistoryManager(private val context: Context) {
    private val cacheFile: File
        get() = File(context.cacheDir, "search_history.txt")

    fun saveSearch(query: String) {
        val history = getHistory().toMutableList()
        if (history.contains(query)) {
            history.remove(query)
        }
        history.add(0, query)
        if (history.size > 10) {
            history.removeAt(history.size - 1)
        }
        try {
            cacheFile.writeText(history.joinToString(","))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getHistory(): List<String> {
        return try {
            if (cacheFile.exists()) {
                val text = cacheFile.readText()
                if (text.isEmpty()) emptyList() else text.split(",")
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearHistory() {
        if (cacheFile.exists()) {
            cacheFile.delete()
        }
    }
}
