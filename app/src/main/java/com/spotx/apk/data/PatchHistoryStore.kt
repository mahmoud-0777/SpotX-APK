package com.spotx.apk.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class PatchHistoryStore(context: Context) {
    private val prefs = context.getSharedPreferences("patch_history", Context.MODE_PRIVATE)

    fun save(item: PatchHistoryItem) {
        val history = getAll().toMutableList()
        history.add(0, item)
        val array = JSONArray()
        history.take(20).forEach {
            array.put(
                JSONObject()
                    .put("inputName", it.inputName)
                    .put("outputPath", it.outputPath)
                    .put("profile", it.profile)
                    .put("timestamp", it.timestamp)
                    .put("success", it.success)
                    .put("message", it.message)
            )
        }
        prefs.edit().putString("items", array.toString()).apply()
    }

    fun getAll(): List<PatchHistoryItem> {
        val raw = prefs.getString("items", null) ?: return emptyList()
        val array = JSONArray(raw)
        return buildList {
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                add(
                    PatchHistoryItem(
                        inputName = o.optString("inputName"),
                        outputPath = o.optString("outputPath"),
                        profile = o.optString("profile"),
                        timestamp = o.optLong("timestamp"),
                        success = o.optBoolean("success"),
                        message = o.optString("message")
                    )
                )
            }
        }
    }
}
