package com.spotx.apk.patcher

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.spotx.apk.data.PatchHistoryItem
import com.spotx.apk.data.PatchHistoryStore
import com.spotx.apk.data.PatchId

class PatchWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    private val notification = PatchNotificationHelper(appContext)

    override suspend fun doWork(): Result {
        val inputUri = inputData.getString(KEY_INPUT_URI)?.let(Uri::parse) ?: return Result.failure()
        val profile = inputData.getString(KEY_PROFILE).orEmpty()
        val patchListRaw = inputData.getString(KEY_PATCHES).orEmpty()
        val selected = patchListRaw.split(",").mapNotNull {
            runCatching { PatchId.valueOf(it) }.getOrNull()
        }.toSet()

        return try {
            val engine = ApkPatchingEngine(applicationContext)
            val result = engine.patchApk(inputUri, selected) { progress, message ->
                setForegroundAsync(createForegroundInfo(progress, message))
                setProgress(workDataOf(KEY_PROGRESS to progress, KEY_STATUS to message))
            }

            PatchHistoryStore(applicationContext).save(
                PatchHistoryItem(
                    inputName = inputUri.lastPathSegment ?: inputUri.toString(),
                    outputPath = result.outputUri.toString(),
                    profile = profile,
                    timestamp = System.currentTimeMillis(),
                    success = true,
                    message = result.message
                )
            )
            Result.success(
                androidx.work.Data.Builder()
                    .putString(KEY_OUTPUT_URI, result.outputUri.toString())
                    .putString(KEY_STATUS, result.message)
                    .build()
            )
        } catch (e: Exception) {
            PatchHistoryStore(applicationContext).save(
                PatchHistoryItem(
                    inputName = inputUri.lastPathSegment ?: inputUri.toString(),
                    outputPath = "",
                    profile = profile,
                    timestamp = System.currentTimeMillis(),
                    success = false,
                    message = e.message ?: "Patching failed"
                )
            )
            Result.failure(
                androidx.work.Data.Builder()
                    .putString(KEY_STATUS, e.message ?: "Patching failed")
                    .build()
            )
        }
    }

    private fun createForegroundInfo(progress: Int, message: String): ForegroundInfo {
        return ForegroundInfo(NOTIFICATION_ID, notification.progress(message, progress))
    }

    companion object {
        const val KEY_INPUT_URI = "input_uri"
        const val KEY_PATCHES = "patches"
        const val KEY_PROFILE = "profile"
        const val KEY_OUTPUT_URI = "output_uri"
        const val KEY_STATUS = "status"
        const val KEY_PROGRESS = "progress"
        const val NOTIFICATION_ID = 7001
    }
}
