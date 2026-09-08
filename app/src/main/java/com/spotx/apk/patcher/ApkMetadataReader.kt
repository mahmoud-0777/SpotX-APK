package com.spotx.apk.patcher

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.spotx.apk.data.ApkInfo

class ApkMetadataReader(private val context: Context) {
    fun read(uri: Uri): ApkInfo {
        val resolver = context.contentResolver
        var displayName = "spotify.apk"
        var size = -1L
        resolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst()) {
                if (nameIndex >= 0) displayName = cursor.getString(nameIndex)
                if (sizeIndex >= 0) size = cursor.getLong(sizeIndex)
            }
        }

        val tempFile = kotlin.runCatching {
            val file = kotlin.io.path.createTempFile(suffix = ".apk").toFile()
            resolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }
            file
        }.getOrNull()

        val archiveInfo = tempFile?.let {
            context.packageManager.getPackageArchiveInfo(it.absolutePath, 0)
        }

        val packageName = archiveInfo?.packageName ?: "Unknown"
        val version = archiveInfo?.versionName ?: "Unknown"
        tempFile?.delete()

        return ApkInfo(displayName, packageName, version, size)
    }
}
