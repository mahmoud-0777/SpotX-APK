package com.spotx.apk.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.spotx.apk.data.ApkInfo
import com.spotx.apk.data.PatchHistoryItem
import com.spotx.apk.data.PatchHistoryStore
import com.spotx.apk.data.PatchId
import com.spotx.apk.data.PatchProfile
import com.spotx.apk.patcher.ApkMetadataReader
import com.spotx.apk.patcher.PatchWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MainUiState(
    val apkUri: Uri? = null,
    val apkInfo: ApkInfo? = null,
    val selectedProfile: PatchProfile = PatchProfile.FULL,
    val selectedPatches: Set<PatchId> = PatchProfile.FULL.patches,
    val status: String = "Pick a Spotify APK to start",
    val isRunning: Boolean = false,
    val progress: Int = 0,
    val history: List<PatchHistoryItem> = emptyList(),
    val supportedVersions: List<String> = emptyList()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val workManager = WorkManager.getInstance(application)
    private val metadataReader = ApkMetadataReader(application)
    private val historyStore = PatchHistoryStore(application)

    private val state = MutableStateFlow(MainUiState())
    private val workState = MutableStateFlow<WorkInfo?>(null)

    val uiState: StateFlow<MainUiState> = combine(state, workState) { current, work ->
        when (work?.state) {
            WorkInfo.State.RUNNING -> current.copy(
                isRunning = true,
                progress = work.progress.getInt(PatchWorker.KEY_PROGRESS, 0),
                status = work.progress.getString(PatchWorker.KEY_STATUS) ?: "Patching in progress..."
            )
            WorkInfo.State.SUCCEEDED -> current.copy(
                isRunning = false,
                progress = 100,
                status = work.outputData.getString(PatchWorker.KEY_STATUS).orEmpty(),
                history = historyStore.getAll()
            )
            WorkInfo.State.FAILED -> current.copy(
                isRunning = false,
                progress = 0,
                status = work.outputData.getString(PatchWorker.KEY_STATUS).orEmpty(),
                history = historyStore.getAll()
            )
            else -> current
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), state.value)

    init {
        state.value = state.value.copy(
            history = historyStore.getAll(),
            supportedVersions = com.spotx.apk.patcher.SupportedVersions.known
        )
    }

    fun onApkSelected(uri: Uri) {
        val info = metadataReader.read(uri)
        state.value = state.value.copy(apkUri = uri, apkInfo = info, status = "APK ready: ${info.displayName}")
    }

    fun onProfileSelected(profile: PatchProfile) {
        state.value = state.value.copy(selectedProfile = profile, selectedPatches = profile.patches)
    }

    fun onPatchToggled(id: PatchId, enabled: Boolean) {
        val mutable = state.value.selectedPatches.toMutableSet()
        if (enabled) mutable.add(id) else mutable.remove(id)
        state.value = state.value.copy(selectedPatches = mutable)
    }

    fun startPatching() {
        val current = state.value
        val uri = current.apkUri ?: return
        val request = OneTimeWorkRequestBuilder<PatchWorker>()
            .setInputData(
                workDataOf(
                    PatchWorker.KEY_INPUT_URI to uri.toString(),
                    PatchWorker.KEY_PROFILE to current.selectedProfile.title,
                    PatchWorker.KEY_PATCHES to current.selectedPatches.joinToString(",") { it.name }
                )
            )
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.NOT_REQUIRED).build()
            )
            .build()

        workManager.enqueueUniqueWork("patch_spotify", ExistingWorkPolicy.REPLACE, request)

        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(request.id).collect { info ->
                workState.value = info
            }
        }
    }
}
