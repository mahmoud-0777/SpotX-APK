package com.spotx.apk.data

enum class PatchId(val label: String) {
    AD_BLOCKING("Ad-blocking"),
    HIDE_PODCASTS("Hide podcasts/episodes/audiobooks"),
    DISABLE_AUTO_UPDATES("Disable auto-updates"),
    ANALYTICS_BLOCKING("Analytics blocking"),
    EXPERIMENTAL_FEATURES("Experimental features")
}

enum class PatchProfile(val title: String, val patches: Set<PatchId>) {
    FULL(
        "Full",
        setOf(
            PatchId.AD_BLOCKING,
            PatchId.HIDE_PODCASTS,
            PatchId.DISABLE_AUTO_UPDATES,
            PatchId.ANALYTICS_BLOCKING,
            PatchId.EXPERIMENTAL_FEATURES
        )
    ),
    PREMIUM(
        "Premium",
        setOf(
            PatchId.AD_BLOCKING,
            PatchId.DISABLE_AUTO_UPDATES,
            PatchId.ANALYTICS_BLOCKING
        )
    ),
    MINIMAL("Minimal", setOf(PatchId.AD_BLOCKING))
}

data class ApkInfo(
    val displayName: String,
    val packageName: String,
    val versionName: String,
    val sizeBytes: Long
)

data class PatchHistoryItem(
    val inputName: String,
    val outputPath: String,
    val profile: String,
    val timestamp: Long,
    val success: Boolean,
    val message: String
)
