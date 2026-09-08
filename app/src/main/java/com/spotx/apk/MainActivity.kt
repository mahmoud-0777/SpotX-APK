package com.spotx.apk

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spotx.apk.data.PatchId
import com.spotx.apk.data.PatchProfile
import com.spotx.apk.ui.MainUiState
import com.spotx.apk.ui.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val vm: MainViewModel = viewModel()
                val state by vm.uiState.collectAsState()
                MainScreen(
                    state = state,
                    onApkSelected = vm::onApkSelected,
                    onProfileSelected = vm::onProfileSelected,
                    onPatchToggled = vm::onPatchToggled,
                    onStart = vm::startPatching
                )
            }
        }
    }
}

@Composable
private fun MainScreen(
    state: MainUiState,
    onApkSelected: (Uri) -> Unit,
    onProfileSelected: (PatchProfile) -> Unit,
    onPatchToggled: (PatchId, Boolean) -> Unit,
    onStart: () -> Unit
) {
    var showHelp by remember { mutableStateOf(false) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) onApkSelected(uri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SpotX APK Patcher") },
                actions = {
                    TextButton(onClick = { showHelp = !showHelp }) { Text("Help") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { picker.launch(arrayOf("application/vnd.android.package-archive")) }) {
                        Text("Select Spotify APK")
                    }
                    Button(onClick = onStart, enabled = state.apkUri != null && !state.isRunning) {
                        Text(if (state.isRunning) "Patching..." else "Patch APK")
                    }
                }
            }
            item {
                val info = state.apkInfo
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("APK Information", fontWeight = FontWeight.Bold)
                        Text("Name: ${info?.displayName ?: "-"}")
                        Text("Package: ${info?.packageName ?: "-"}")
                        Text("Version: ${info?.versionName ?: "-"}")
                        Text("Size: ${if ((info?.sizeBytes ?: -1) >= 0) "${info?.sizeBytes} bytes" else "-"}")
                    }
                }
            }
            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Patch profile", fontWeight = FontWeight.Bold)
                        PatchProfile.entries.forEach { profile ->
                            Row {
                                RadioButton(
                                    selected = state.selectedProfile == profile,
                                    onClick = { onProfileSelected(profile) }
                                )
                                Text(profile.title, modifier = Modifier.padding(top = 12.dp))
                            }
                        }
                    }
                }
            }
            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Patch selection", fontWeight = FontWeight.Bold)
                        PatchId.entries.forEach { patch ->
                            Row {
                                Checkbox(
                                    checked = state.selectedPatches.contains(patch),
                                    onCheckedChange = { checked -> onPatchToggled(patch, checked) }
                                )
                                Text(patch.label, modifier = Modifier.padding(top = 12.dp))
                            }
                        }
                    }
                }
            }
            if (showHelp) {
                item {
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("How to use", fontWeight = FontWeight.Bold)
                            Text("1. Select Spotify APK")
                            Text("2. Choose profile or customize patches")
                            Text("3. Tap Patch APK and wait for notification")
                            Text("4. Find output in app external files/PatchedApks")
                            Text("Supported Spotify versions: ${state.supportedVersions.joinToString()}")
                        }
                    }
                }
            }
            item {
                Text("Status: ${state.status}")
                if (state.isRunning) {
                    LinearProgressIndicator(
                        progress = { state.progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                    )
                }
            }
            item {
                Text("History", fontWeight = FontWeight.Bold)
            }
            items(state.history) { item ->
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(10.dp)) {
                        Text(item.inputName)
                        Text(item.message)
                        Text("Profile: ${item.profile}")
                    }
                }
            }
        }
    }
}
