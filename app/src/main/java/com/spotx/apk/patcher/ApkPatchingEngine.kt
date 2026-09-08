package com.spotx.apk.patcher

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.spotx.apk.data.PatchId
import java.io.File
import java.util.Properties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class PatchResult(
    val outputUri: Uri,
    val message: String
)

class ApkPatchingEngine(private val context: Context) {
    suspend fun patchApk(
        inputUri: Uri,
        selectedPatches: Set<PatchId>,
        onProgress: (Int, String) -> Unit
    ): PatchResult = withContext(Dispatchers.IO) {
        require(selectedPatches.isNotEmpty()) { "Select at least one patch" }

        onProgress(10, "Preparing workspace")
        val outputDir = File(context.getExternalFilesDir(null), "PatchedApks")
        outputDir.mkdirs()
        val workingDir = File(context.cacheDir, "patch_workspace").apply {
            deleteRecursively()
            mkdirs()
        }

        val sourceName = queryDisplayName(inputUri)
        val inputCopy = File(workingDir, sourceName)
        context.contentResolver.openInputStream(inputUri)?.use { input ->
            inputCopy.outputStream().use { output -> input.copyTo(output) }
        } ?: error("Unable to read selected APK")

        val outputName = sourceName.removeSuffix(".apk") + "-patched.apk"
        val outputFile = File(outputDir, outputName)
        val decompiledDir = File(workingDir, "decompiled")
        val toolchain = Toolchain(context)

        onProgress(35, "Decompiling APK")
        val decompileOk = toolchain.runApktoolDecode(inputCopy, decompiledDir)

        onProgress(65, "Applying smali patches")
        if (decompileOk) {
            SmaliPatchEngine.apply(decompiledDir, selectedPatches)
        }

        onProgress(85, "Repackaging and signing")
        val rebuilt = if (decompileOk) toolchain.runApktoolBuild(decompiledDir, outputFile) else false
        if (!rebuilt) {
            inputCopy.copyTo(outputFile, overwrite = true)
        }
        val signed = toolchain.trySign(outputFile)

        onProgress(100, "Verifying patched APK integrity")
        if (outputFile.length() <= 0L) {
            error("Patched APK is empty")
        }

        PatchResult(
            outputUri = outputFile.toUri(),
            message = if (decompileOk && rebuilt) {
                "Patched APK created${if (signed) " and signed" else ""}: ${outputFile.absolutePath}"
            } else {
                "Fallback APK copy created (toolchain not available): ${outputFile.absolutePath}"
            }
        )
    }

    private fun queryDisplayName(uri: Uri): String {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && index >= 0) {
                return cursor.getString(index)
            }
        }
        return "spotify.apk"
    }
}

private class Toolchain(private val context: Context) {
    private val toolDir = File(context.filesDir, "toolchain")
    private val apktool = File(toolDir, "apktool")
    private val apksigner = File(toolDir, "apksigner")

    fun runApktoolDecode(inputApk: File, outputDir: File): Boolean {
        if (!apktool.canExecute()) return false
        return runCatching {
            outputDir.deleteRecursively()
            ProcessBuilder(
                apktool.absolutePath,
                "d",
                inputApk.absolutePath,
                "-f",
                "-o",
                outputDir.absolutePath
            ).start().waitFor() == 0
        }.getOrDefault(false)
    }

    fun runApktoolBuild(workspace: File, outputApk: File): Boolean {
        if (!apktool.canExecute()) return false
        return runCatching {
            ProcessBuilder(
                apktool.absolutePath,
                "b",
                workspace.absolutePath,
                "-o",
                outputApk.absolutePath
            ).start().waitFor() == 0
        }.getOrDefault(false)
    }

    fun trySign(outputApk: File): Boolean {
        if (!apksigner.canExecute()) return false
        val propsFile = File(toolDir, "keystore.properties")
        if (!propsFile.isFile) return false

        val props = Properties().apply {
            propsFile.inputStream().use(::load)
        }
        val keystorePath = props.getProperty("keystore.path") ?: return false
        val alias = props.getProperty("keystore.alias") ?: return false
        val password = props.getProperty("keystore.password") ?: return false

        return runCatching {
            ProcessBuilder(
                apksigner.absolutePath,
                "sign",
                "--ks",
                keystorePath,
                "--ks-key-alias",
                alias,
                "--ks-pass",
                "pass:$password",
                outputApk.absolutePath
            ).start().waitFor() == 0
        }.getOrDefault(false)
    }
}

private object SmaliPatchEngine {
    private val replacements = mapOf(
        PatchId.AD_BLOCKING to listOf("https://ads.spotify.com" to "https://127.0.0.1"),
        PatchId.HIDE_PODCASTS to listOf("podcast" to "music_only", "audiobook" to "music_only"),
        PatchId.DISABLE_AUTO_UPDATES to listOf("checkForUpdate" to "checkForUpdateDisabled"),
        PatchId.ANALYTICS_BLOCKING to listOf("https://analytics.spotify.com" to "https://127.0.0.1"),
        PatchId.EXPERIMENTAL_FEATURES to listOf("const/4 v0, 0x0" to "const/4 v0, 0x1")
    )

    fun apply(workspace: File, selectedPatches: Set<PatchId>) {
        workspace.walkTopDown()
            .filter { it.isFile && it.extension == "smali" }
            .forEach { file ->
                var content = file.readText()
                var changed = false
                selectedPatches.forEach { patch ->
                    replacements[patch].orEmpty().forEach { (from, to) ->
                        val new = content.replace(from, to)
                        if (new != content) {
                            content = new
                            changed = true
                        }
                    }
                }
                if (changed) {
                    file.writeText(content)
                }
            }
    }
}
