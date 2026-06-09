package com.example.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Locales
import com.example.viewmodel.ReadTrackerViewModel
import com.example.ui.theme.AccentOrange
import androidx.compose.ui.text.font.FontFamily
import com.example.model.SettingsData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ReadTrackerViewModel,
    onNavigateToColorSettings: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val language by viewModel.language.collectAsState()
    val context = LocalContext.current

    val pendingImportBooks by viewModel.pendingImportBooks.collectAsState()
    val pendingImportSettings by viewModel.pendingImportSettings.collectAsState()

    // File Picker Activity contract launcher for importing Json (Library)
    val importFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.handleImportUri(context, uri)
        }
    }

    // File Picker Activity contract launcher for importing Json (Settings)
    val importSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.handleImportSettingsUri(context, uri)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column {
                Spacer(modifier = Modifier.height(getAdaptiveStatusBarPadding()))
                TopAppBar(
                    title = { Text(Locales.getString("settings", language), fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = if (language == "en") "Back" else "Назад",
                                tint = MaterialTheme.colorScheme.onBackground
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
            
            // THEME GROUP
            ThemeSettingsGroup(viewModel)
            Spacer(modifier = Modifier.height(20.dp))

            // ADVANCED FUNCTIONS GROUP
            AdvancedSettingsGroup(viewModel)
            Spacer(modifier = Modifier.height(20.dp))

            // APPEARANCE GROUPS
            AppearanceSettingsGroup(viewModel)
            Spacer(modifier = Modifier.height(20.dp))

            // COLOR SETTINGS
            CategoryHeader(if (language == "en") "Color Settings" else "Настройки цвета")
            CardGroup {
                ActionTile(
                    title = if (language == "en") "Color Customization" else "Кастомизация цветов",
                    subtitle = if (language == "en") "Configure UI, types and status colors" else "Настроить цвета интерфейса, типов и статусов",
                    icon = Icons.Rounded.Palette,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = onNavigateToColorSettings
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            // FILE MANAGEMENT DATA ACTIONS GROUP
            DataSettingsGroup(viewModel, context, importFileLauncher, importSettingsLauncher)
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Confirmation Import dialogues
    pendingImportBooks?.let { booksToImport ->
        AlertDialog(
            onDismissRequest = { viewModel.cancelImport() },
            title = { Text(Locales.getString("import_library", language), fontWeight = FontWeight.Bold, fontSize = 17.sp) },
            text = {
                val msg = if (language == "en") 
                    "Will be loaded ${booksToImport.size} titles. Current library will be completely replaced." 
                    else "Будет загружено ${booksToImport.size} тайтлов. Текущая библиотека будет полностью заменена."
                Text(
                    text = msg,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.confirmImport() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(if (language == "en") "Replace" else "Заменить", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.cancelImport() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                ) {
                    Text(Locales.getString("cancel", language), fontWeight = FontWeight.SemiBold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    pendingImportSettings?.let { _ ->
        AlertDialog(
            onDismissRequest = { viewModel.cancelImport() },
            title = { Text(Locales.getString("import_settings", language), fontWeight = FontWeight.Bold, fontSize = 17.sp) },
            text = {
                Text(
                    text = Locales.getString("confirm_import_settings", language),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.confirmImportSettings() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(Locales.getString("import", language), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.cancelImport() },
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
fun ThemeSettingsGroup(viewModel: ReadTrackerViewModel) {
    val language by viewModel.language.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()

    CategoryHeader(Locales.getString("theme", language))
    CardGroup {
        listOf(
            Triple(0, if (language == "en") "AMOLED" else "AMOLED", Icons.Rounded.DarkMode),
            Triple(1, Locales.getString("theme_dark", language), Icons.Rounded.Brightness2),
            Triple(2, Locales.getString("theme_light", language), Icons.Rounded.WbSunny)
        ).forEachIndexed { i, theme ->
            val optionMode = theme.first
            val isActive = themeMode == optionMode

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setThemeMode(optionMode) }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = theme.third,
                    contentDescription = null,
                    tint = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = theme.second,
                    fontSize = 15.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                    color = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier.weight(1.0f)
                )
                Icon(
                    imageVector = if (isActive) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (i < 2) {
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))
            }
        }
    }
}

@Composable
fun AdvancedSettingsGroup(viewModel: ReadTrackerViewModel) {
    val language by viewModel.language.collectAsState()
    val showBookmarks by viewModel.showBookmarks.collectAsState()
    val bookmarkPosition by viewModel.bookmarkPosition.collectAsState()
    val enableAdaptationStart by viewModel.enableAdaptationStart.collectAsState()
    val enableHybrid by viewModel.enableHybrid.collectAsState()
    val enableRating by viewModel.enableRating.collectAsState()
    val ratingScale by viewModel.ratingScale.collectAsState()
    val badgeLayoutMode by viewModel.badgeLayoutMode.collectAsState()

    CategoryHeader(Locales.getString("advanced_functions", language))
    CardGroup {
        // Bookmarks
        SwitchRow(
            title = Locales.getString("bookmarks", language),
            subtitle = if (showBookmarks) Locales.getString("bookmarks_sub_on", language) else Locales.getString("bookmarks_sub_off", language),
            checked = showBookmarks,
            onCheckedChange = { viewModel.setShowBookmarks(it) }
        )

        if (showBookmarks) {
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))
            // Bookmark placement select row
            DropdownRow(
                title = Locales.getString("bookmark_position", language),
                subtitle = Locales.getString("bookmark_position_sub", language),
                options = listOf(
                    Locales.getString("bottom", language) to 0, 
                    Locales.getString("inline", language) to 1
                ),
                selectedValue = bookmarkPosition,
                onValueChange = { viewModel.setBookmarkPosition(it) }
            )
        }

        HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))

        // Adaptations
        SwitchRow(
            title = Locales.getString("read_after_adaptation", language),
            subtitle = if (enableAdaptationStart) Locales.getString("read_after_adaptation_sub_on", language) else Locales.getString("read_after_adaptation_sub_off", language),
            checked = enableAdaptationStart,
            onCheckedChange = { viewModel.setEnableAdaptationStart(it) }
        )

        HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))

        // Hybrid mode
        SwitchRow(
            title = Locales.getString("hybrid_format", language),
            subtitle = if (enableHybrid) Locales.getString("hybrid_format_sub_on", language) else Locales.getString("hybrid_format_sub_off", language),
            checked = enableHybrid,
            onCheckedChange = { viewModel.setEnableHybrid(it) }
        )

        HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))

        // Ratings toggle
        SwitchRow(
            title = Locales.getString("title_rating", language),
            subtitle = if (enableRating) Locales.getString("title_rating_sub_on", language) else Locales.getString("title_rating_sub_off", language),
            checked = enableRating,
            onCheckedChange = { viewModel.setEnableRating(it) }
        )

        if (enableRating) {
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))
            DropdownRow(
                title = Locales.getString("rating_scale", language),
                subtitle = Locales.getString("rating_scale_sub", language),
                options = listOf(
                    Locales.getString("stars_5", language) to 5, 
                    Locales.getString("stars_10", language) to 10
                ),
                selectedValue = ratingScale,
                onValueChange = { viewModel.setRatingScale(it) }
            )
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))
            DropdownRow(
                title = Locales.getString("rating_placement", language),
                subtitle = Locales.getString("rating_placement_sub", language),
                options = listOf(
                    Locales.getString("column", language) to 0, 
                    Locales.getString("row", language) to 1
                ),
                selectedValue = badgeLayoutMode,
                onValueChange = { viewModel.setBadgeLayoutMode(it) }
            )
        }
    }
}

@Composable
fun AppearanceSettingsGroup(viewModel: ReadTrackerViewModel) {
    val analyticsShowMode by viewModel.analyticsShowMode.collectAsState()
    val showCovers by viewModel.showCovers.collectAsState()
    val shortenNumbers by viewModel.shortenNumbers.collectAsState()
    val stackedStats by viewModel.stackedStats.collectAsState()
    val showShareButton by viewModel.showShareButton.collectAsState()
    val showWebChapters by viewModel.showWebChapters.collectAsState()
    val disableAnimations by viewModel.disableAnimations.collectAsState()
    val filterSpacing by viewModel.filterSpacing.collectAsState()
    val cardSpacing by viewModel.cardSpacing.collectAsState()
    val titleFontSize by viewModel.titleFontSize.collectAsState()
    val libraryTitleFontSize by viewModel.libraryTitleFontSize.collectAsState()
    val language by viewModel.language.collectAsState()

    CategoryHeader(Locales.getString("application", language))
    CardGroup {
        LanguageRow(
            language = language,
            currentLanguage = language,
            onLanguageChange = { viewModel.setLanguage(it) }
        )
    }

    CategoryHeader(Locales.getString("appearance", language))
    CardGroup {
        DropdownRow(
            title = Locales.getString("show_in_analytics", language),
            subtitle = Locales.getString("show_in_analytics_sub", language),
            options = listOf(
                Locales.getString("singles_and_web", language) to 0,
                Locales.getString("only_singles", language) to 1,
                Locales.getString("only_web", language) to 2,
                Locales.getString("hidden", language) to 3
            ),
            selectedValue = analyticsShowMode,
            onValueChange = { viewModel.setAnalyticsShowMode(it) }
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))
        SwitchRow(
            title = Locales.getString("title_covers", language),
            subtitle = if (showCovers) Locales.getString("title_covers_sub_on", language) else Locales.getString("title_covers_sub_off", language),
            checked = showCovers,
            onCheckedChange = { viewModel.setShowCovers(it) }
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))
        SwitchRow(
            title = Locales.getString("shorten_numbers", language),
            subtitle = Locales.getString("shorten_numbers_sub", language),
            checked = shortenNumbers,
            onCheckedChange = { viewModel.setShortenNumbers(it) }
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))
        SwitchRow(
            title = Locales.getString("stacked_analytics", language),
            subtitle = Locales.getString("stacked_analytics_sub", language),
            checked = stackedStats,
            onCheckedChange = { viewModel.setStackedStats(it) }
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))
        SwitchRow(
            title = Locales.getString("share_button", language),
            subtitle = Locales.getString("share_button_sub", language),
            checked = showShareButton,
            onCheckedChange = { viewModel.setShowShareButton(it) }
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))
        SwitchRow(
            title = Locales.getString("chapters_for_web", language),
            subtitle = if (showWebChapters) Locales.getString("chapters_for_web_sub_on", language) else Locales.getString("chapters_for_web_sub_off", language),
            checked = showWebChapters,
            onCheckedChange = { viewModel.setShowWebChapters(it) }
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))
        SwitchRow(
            title = Locales.getString("disable_animations", language),
            subtitle = if (disableAnimations) Locales.getString("disable_animations_sub_on", language) else Locales.getString("disable_animations_sub_off", language),
            checked = disableAnimations,
            onCheckedChange = { viewModel.setDisableAnimations(it) }
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))
        SliderRow(
            title = Locales.getString("header_spacing", language),
            subtitle = Locales.getString("header_spacing_sub", language),
            value = filterSpacing,
            valueRange = 0.0f..30.0f,
            onValueChange = { viewModel.setFilterSpacing(it) }
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))
        SliderRow(
            title = Locales.getString("card_spacing", language),
            subtitle = Locales.getString("card_spacing_sub", language),
            value = cardSpacing,
            valueRange = 0.0f..10.0f,
            onValueChange = { viewModel.setCardSpacing(it) }
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))
        SliderRow(
            title = Locales.getString("title_font_size", language),
            subtitle = Locales.getString("title_font_size_sub", language),
            value = titleFontSize,
            valueRange = 10.0f..22.0f,
            valueSuffix = " sp",
            onValueChange = { viewModel.setTitleFontSize(it) }
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))
        SliderRow(
            title = Locales.getString("font_size_library", language),
            subtitle = Locales.getString("font_size_library_sub", language),
            value = libraryTitleFontSize,
            valueRange = 16.0f..32.0f,
            valueSuffix = " sp",
            onValueChange = { viewModel.setLibraryTitleFontSize(it) }
        )
    }
}

@Composable
fun DataSettingsGroup(
    viewModel: ReadTrackerViewModel,
    context: Context,
    importFileLauncher: ManagedActivityResultLauncher<String, Uri?>,
    importSettingsLauncher: ManagedActivityResultLauncher<String, Uri?>
) {
    val language by viewModel.language.collectAsState()

    CategoryHeader(Locales.getString("library", language))
    CardGroup {
        ActionTile(
            title = Locales.getString("export_library", language),
            subtitle = Locales.getString("save_to_json", language),
            icon = Icons.Rounded.UploadFile,
            color = Color(0xFF34D399),
            onClick = { viewModel.exportLibrary(context) }
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))
        ActionTile(
            title = Locales.getString("import_library", language),
            subtitle = Locales.getString("load_from_json", language),
            icon = Icons.Rounded.DownloadForOffline,
            color = Color(0xFF60A5FA),
            onClick = { importFileLauncher.launch("application/json") }
        )
    }

    Spacer(modifier = Modifier.height(16.dp))
    CategoryHeader(Locales.getString("configuration", language))
    CardGroup {
        ActionTile(
            title = Locales.getString("export_settings", language),
            subtitle = Locales.getString("save_configuration", language),
            icon = Icons.Rounded.SettingsBackupRestore,
            color = Color(0xFFA78BFA),
            onClick = { viewModel.exportSettings(context) }
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))
        ActionTile(
            title = Locales.getString("import_settings", language),
            subtitle = Locales.getString("load_configuration", language),
            icon = Icons.Rounded.Restore,
            color = Color(0xFFFBBF24),
            onClick = { importSettingsLauncher.launch("application/json") }
        )
    }
}

@Composable
fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.0f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
            Text(subtitle, fontSize = 11.sp, color = Color.Gray, lineHeight = 14.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.Gray.copy(alpha = 0.25f)
            )
        )
    }
}

@Composable
fun DropdownRow(
    title: String,
    subtitle: String,
    options: List<Pair<String, Int>>,
    selectedValue: Int,
    onValueChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currentSelectedName = options.find { it.second == selectedValue }?.first ?: ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.0f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
            Text(subtitle, fontSize = 11.sp, color = Color.Gray)
        }
        
        Box {
            Text(
                text = currentSelectedName,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier
                    .clickable { expanded = true }
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            )
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.first, fontWeight = FontWeight.Medium) },
                        onClick = {
                            onValueChange(option.second)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1.0f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun LanguageRow(
    language: String,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = Locales.getString("language", language),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "English / Русский",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(4.dp)
        ) {
            LanguageButton(
                label = "EN",
                isSelected = currentLanguage == "en",
                onClick = { onLanguageChange("en") }
            )
            LanguageButton(
                label = "RU",
                isSelected = currentLanguage == "ru",
                onClick = { onLanguageChange("ru") }
            )
        }
    }
}

@Composable
fun LanguageButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SliderRow(
    title: String,
    subtitle: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    valueSuffix: String = "",
    onValueChange: (Float) -> Unit
) {
    var sliderValue by remember(value) { androidx.compose.runtime.mutableFloatStateOf(value) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1.0f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
                Text(subtitle, fontSize = 11.sp, color = Color.Gray)
            }
            Text(
                text = "${sliderValue.toInt()}$valueSuffix",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = { onValueChange(sliderValue) },
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = Color.Gray.copy(alpha = 0.24f)
            )
        )
    }
}

