package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.ReadTrackerViewModel
import com.example.ui.theme.AccentOrange
import com.example.ui.Locales

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorSettingsScreen(
    viewModel: ReadTrackerViewModel,
    onNavigateBack: () -> Unit
) {
    val language by viewModel.language.collectAsState()
    var showResetConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column {
                Spacer(modifier = Modifier.height(getAdaptiveStatusBarPadding()))
                TopAppBar(
                    title = { Text(Locales.getString("color_customization", language), fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = if (language == "en") "Back" else "Назад",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showResetConfirmation = true }) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = Locales.getString("reset_all", language),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    windowInsets = WindowInsets(0, 0, 0, 0),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                     )
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            InfoBanner(language)
            Spacer(modifier = Modifier.height(16.dp))

            InterfaceColorGroup(viewModel, language)
            Spacer(modifier = Modifier.height(20.dp))

            FormatTypesColorGroup(viewModel, language)
            Spacer(modifier = Modifier.height(20.dp))

            StatusesColorGroup(viewModel, language)
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { showResetConfirmation = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(Locales.getString("reset_to_default", language), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = { Text(Locales.getString("color_reset_title", language), fontWeight = FontWeight.Bold, fontSize = 17.sp) },
            text = {
                Text(
                    text = Locales.getString("color_reset_confirm", language),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetColorsToDefault()
                        showResetConfirmation = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(Locales.getString("reset", language), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetConfirmation = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                ) {
                    Text(Locales.getString("cancel", language), fontWeight = FontWeight.SemiBold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun InterfaceColorGroup(viewModel: ReadTrackerViewModel, language: String) {
    val colorAccentHex by viewModel.colorAccent.collectAsState()

    CategoryHeader(Locales.getString("interface_colors", language))
    CardGroup {
        ColorConfigRow(
            label = Locales.getString("accent_and_buttons", language),
            hexValue = colorAccentHex,
            onValueChange = { viewModel.setColorAccent(it) }
        )
    }
}

@Composable
fun FormatTypesColorGroup(viewModel: ReadTrackerViewModel, language: String) {
    val colorFormatHybridHex by viewModel.colorFormatHybrid.collectAsState()
    val colorFormatSeriesHex by viewModel.colorFormatSeries.collectAsState()
    val colorFormatWebHex by viewModel.colorFormatWeb.collectAsState()
    val colorFormatSingleHex by viewModel.colorFormatSingle.collectAsState()

    CategoryHeader(Locales.getString("edition_types", language))
    CardGroup {
        ColorConfigRow(
            label = "LN+WN Hybrid",
            hexValue = colorFormatHybridHex,
            onValueChange = { viewModel.setColorFormatHybrid(it) }
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.08f), modifier = Modifier.padding(horizontal = 16.dp))
        ColorConfigRow(
            label = if (language == "en") "Series (Volumes)" else "Серия томов",
            hexValue = colorFormatSeriesHex,
            onValueChange = { viewModel.setColorFormatSeries(it) }
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.08f), modifier = Modifier.padding(horizontal = 16.dp))
        ColorConfigRow(
            label = if (language == "en") "Web Novel (Web)" else "Веб-новелла (Web)",
            hexValue = colorFormatWebHex,
            onValueChange = { viewModel.setColorFormatWeb(it) }
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.08f), modifier = Modifier.padding(horizontal = 16.dp))
        ColorConfigRow(
            label = if (language == "en") "Single (One-shot)" else "Сингл (Одиночная книга)",
            hexValue = colorFormatSingleHex,
            onValueChange = { viewModel.setColorFormatSingle(it) }
        )
    }
}

@Composable
fun StatusesColorGroup(viewModel: ReadTrackerViewModel, language: String) {
    val colorStatusPlannedHex by viewModel.colorStatusPlanned.collectAsState()
    val colorStatusReadingHex by viewModel.colorStatusReading.collectAsState()
    val colorStatusPausedHex by viewModel.colorStatusPaused.collectAsState()
    val colorStatusCompletedHex by viewModel.colorStatusCompleted.collectAsState()
    val colorStatusDroppedHex by viewModel.colorStatusDropped.collectAsState()

    CategoryHeader(Locales.getString("reading_statuses", language))
    CardGroup {
        ColorConfigRow(
            label = Locales.getString("planned", language),
            hexValue = colorStatusPlannedHex,
            onValueChange = { viewModel.setColorStatusPlanned(it) }
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.08f), modifier = Modifier.padding(horizontal = 16.dp))
        ColorConfigRow(
            label = Locales.getString("reading", language),
            hexValue = colorStatusReadingHex,
            onValueChange = { viewModel.setColorStatusReading(it) }
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.08f), modifier = Modifier.padding(horizontal = 16.dp))
        ColorConfigRow(
            label = Locales.getString("paused", language),
            hexValue = colorStatusPausedHex,
            onValueChange = { viewModel.setColorStatusPaused(it) }
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.08f), modifier = Modifier.padding(horizontal = 16.dp))
        ColorConfigRow(
            label = Locales.getString("completed", language),
            hexValue = colorStatusCompletedHex,
            onValueChange = { viewModel.setColorStatusCompleted(it) }
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.08f), modifier = Modifier.padding(horizontal = 16.dp))
        ColorConfigRow(
            label = Locales.getString("dropped", language),
            hexValue = colorStatusDroppedHex,
            onValueChange = { viewModel.setColorStatusDropped(it) }
        )
    }
}

@Composable
fun InfoBanner(language: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Palette,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = Locales.getString("flexible_customization", language),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = Locales.getString("customization_sub", language),
                fontSize = 11.sp,
                color = Color.Gray,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun ColorConfigRow(
    label: String,
    hexValue: String,
    onValueChange: (String) -> Unit
) {
    val viewModel: ReadTrackerViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val language by viewModel.language.collectAsState()
    var textState by remember(hexValue) { mutableStateOf(hexValue) }
    val displayColor = remember(hexValue) { parseHexColor(hexValue, AccentOrange) }
    var showPickerDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Color Circle Preview
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(displayColor)
                .clickable { showPickerDialog = true }
        )
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )

        // Hex input field
        OutlinedTextField(
            value = textState,
            onValueChange = { newValue ->
                val cleaned = newValue.trim()
                textState = cleaned
                if (cleaned.length in 6..9) {
                    onValueChange(cleaned)
                }
            },
            singleLine = true,
            modifier = Modifier.width(100.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontFamily = FontFamily.Monospace),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
    }

    if (showPickerDialog) {
        PresetColorDialog(
            language = language,
            onDismiss = { showPickerDialog = false },
            onSelectColor = { pickedHex ->
                textState = pickedHex
                onValueChange(pickedHex)
            }
        )
    }
}

@Composable
fun PresetColorDialog(
    language: String,
    onDismiss: () -> Unit,
    onSelectColor: (String) -> Unit
) {
    val presetRows = listOf(
        listOf("#FF9F0A", "#34D399", "#60A5FA", "#A78BFA", "#FBBF24", "#F87171"),
        listOf("#F472B6", "#22D3EE", "#2DD4BF", "#FDA4AF", "#94A3B8", "#C084FC")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(Locales.getString("pick_color", language), fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column {
                Text(Locales.getString("pick_from_palette", language), fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 12.dp))
                presetRows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        row.forEach { hex ->
                            val c = remember(hex) { parseHexColor(hex, Color.Gray) }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(c)
                                    .clickable {
                                        onSelectColor(hex)
                                        onDismiss()
                                    }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(Locales.getString("close", language), fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

