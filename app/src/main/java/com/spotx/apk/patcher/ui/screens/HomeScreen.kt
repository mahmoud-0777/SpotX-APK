package com.spotx.apk.patcher.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var selectedFile by remember { mutableStateOf<Uri?>(null) }
    var patchingProgress by remember { mutableStateOf(0) }
    var isPatching by remember { mutableStateOf(false) }
    var selectedPatches by remember { mutableStateOf(setOf("ad_blocking", "hide_podcasts", "disable_updates")) }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedFile = uri
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "SpotX-APK Patcher",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Spotify Android APK Modifier",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Divider(modifier = Modifier.fillMaxWidth())

        // APK Selection
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Step 1: Select Spotify APK",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Button(
                    onClick = { fileLauncher.launch("application/vnd.android.package-archive") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isPatching
                ) {
                    Icon(
                        imageVector = Icons.Filled.FileOpen,
                        contentDescription = "Select APK",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Choose APK File")
                }

                if (selectedFile != null) {
                    Text(
                        text = "Selected: ${selectedFile?.lastPathSegment}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Patch Selection
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Step 2: Select Patches",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                PatchCheckbox(
                    label = "🚫 Block Ads",
                    description = "Remove all banner, video, and audio ads",
                    isChecked = selectedPatches.contains("ad_blocking"),
                    onCheckedChange = { checked ->
                        selectedPatches = if (checked) {
                            selectedPatches + "ad_blocking"
                        } else {
                            selectedPatches - "ad_blocking"
                        }
                    },
                    enabled = !isPatching
                )

                PatchCheckbox(
                    label = "📻 Hide Podcasts",
                    description = "Remove podcasts, episodes, and audiobooks",
                    isChecked = selectedPatches.contains("hide_podcasts"),
                    onCheckedChange = { checked ->
                        selectedPatches = if (checked) {
                            selectedPatches + "hide_podcasts"
                        } else {
                            selectedPatches - "hide_podcasts"
                        }
                    },
                    enabled = !isPatching
                )

                PatchCheckbox(
                    label = "🔄 Disable Updates",
                    description = "Stop automatic Spotify updates",
                    isChecked = selectedPatches.contains("disable_updates"),
                    onCheckedChange = { checked ->
                        selectedPatches = if (checked) {
                            selectedPatches + "disable_updates"
                        } else {
                            selectedPatches - "disable_updates"
                        }
                    },
                    enabled = !isPatching
                )

                PatchCheckbox(
                    label = "📊 Block Analytics",
                    description = "Disable usage tracking and analytics",
                    isChecked = selectedPatches.contains("analytics"),
                    onCheckedChange = { checked ->
                        selectedPatches = if (checked) {
                            selectedPatches + "analytics"
                        } else {
                            selectedPatches - "analytics"
                        }
                    },
                    enabled = !isPatching
                )
            }
        }

        // Patching Progress
        if (isPatching) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Patching APK...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    LinearProgressIndicator(
                        progress = { patchingProgress / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "$patchingProgress% Complete",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { /* TODO: Start patching */ },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                enabled = selectedFile != null && !isPatching
            ) {
                Text("Patch APK")
            }

            OutlinedButton(
                onClick = { /* TODO: Show info */ },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Info",
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text("Help")
            }
        }

        // Info Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "ℹ️ Information",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Text(
                    text = "• Place your Spotify APK in the Download folder\n" +
                            "• Select patches you want to apply\n" +
                            "• Wait for patching to complete\n" +
                            "• Install the patched APK\n" +
                            "• Uninstall original Spotify first",
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun PatchCheckbox(
    label: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
        Column {
            Text(
                text = label,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}
