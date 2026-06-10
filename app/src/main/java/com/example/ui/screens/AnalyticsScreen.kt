package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Book
import com.example.ui.Locales
import com.example.viewmodel.ReadTrackerViewModel
import com.example.ui.theme.AccentOrange

private data class AnalyticsMetrics(
    val completedSeries: Int,
    val completedSingles: Int,
    val completedHybrids: Int,
    val completedWeb: Int,
    val totalVolumes: Int,
    val hasVolumes: Boolean,
    val totalWords: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: ReadTrackerViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val books by viewModel.books.collectAsState()
    val shortenNumbers by viewModel.shortenNumbers.collectAsState()
    val stackedStats by viewModel.stackedStats.collectAsState()
    val analyticsShowMode by viewModel.analyticsShowMode.collectAsState()
    val language by viewModel.language.collectAsState()

    // Calculating Metrics with optimized remember block
    val metrics = remember(books) {
        var completedSeries = 0
        var completedSingles = 0
        var completedHybrids = 0
        var completedWeb = 0
        var totalVolumes = 0
        var hasVolumes = false
        var totalWords = 0

        for (book in books) {
            val isCompleted = book.status == 3
            if (isCompleted) {
                if (book.isSeries) completedSeries++
                if (book.isSingle) completedSingles++
                if (book.isHybridFormat) completedHybrids++
                if (book.isWeb) completedWeb++
            }
            if (book.countVolumes && !book.isWeb) {
                totalVolumes += book.effectiveVolumes
                hasVolumes = true
            }
            totalWords += book.effectiveWords
        }

        AnalyticsMetrics(
            completedSeries = completedSeries,
            completedSingles = completedSingles,
            completedHybrids = completedHybrids,
            completedWeb = completedWeb,
            totalVolumes = totalVolumes,
            hasVolumes = hasVolumes,
            totalWords = totalWords
        )
    }

    val completedSeriesCount = metrics.completedSeries
    val completedSinglesCount = metrics.completedSingles
    val completedHybridsCount = metrics.completedHybrids
    val completedWebCount = metrics.completedWeb

    val totalVolumesRead = metrics.totalVolumes
    val hasBooksWithVolumes = metrics.hasVolumes
    val totalWordsRead = metrics.totalWords

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column {
                Spacer(modifier = Modifier.height(getAdaptiveStatusBarPadding()))
                TopAppBar(
                    title = { Text(Locales.getString("analytics", language), fontWeight = FontWeight.Bold) },
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
            // METRICS CARD SECTION
            val statsList = remember(completedSeriesCount, completedSinglesCount, completedHybridsCount, completedWebCount, analyticsShowMode, hasBooksWithVolumes, totalVolumesRead, language) {
                val showSingles = analyticsShowMode == 0 || analyticsShowMode == 1
                val showWeb = analyticsShowMode == 0 || analyticsShowMode == 2
                mutableListOf<@Composable () -> Unit>().apply {
                    add {
                        StatCard(
                            count = completedSeriesCount.toString(),
                            label = if (language == "en") "Completed Series" else "Завершено серий",
                            icon = Icons.Rounded.EmojiEvents,
                            color = Color(0xFF34D399) // Green
                        )
                    }

                    if (showSingles && completedSinglesCount > 0) {
                        add {
                            StatCard(
                                count = completedSinglesCount.toString(),
                                label = if (language == "en") "Completed Singles" else "Завершено синглов",
                                icon = Icons.Rounded.ContentCopy,
                                color = Color(0xFF06B6D4) // Cyan
                            )
                        }
                    }

                    if (completedHybridsCount > 0) {
                        add {
                            StatCard(
                                count = completedHybridsCount.toString(),
                                label = if (language == "en") "Completed LN+WN" else "Завершено LN+WN",
                                icon = Icons.Rounded.AutoStories,
                                color = Color(0xFFFBBF24) // Yellow
                            )
                        }
                    }

                    if (showWeb) {
                        add {
                            StatCard(
                                count = completedWebCount.toString(),
                                label = if (language == "en") "Completed Web" else "Завершено веб",
                                icon = Icons.Rounded.Language,
                                color = Color(0xFFA78BFA) // Violet
                            )
                        }
                    }

                    if (hasBooksWithVolumes) {
                        add {
                            StatCard(
                                count = totalVolumesRead.toString(),
                                label = if (language == "en") "Volumes Read" else "Прочитано томов",
                                icon = Icons.Rounded.Layers,
                                color = Color(0xFF60A5FA) // Blue
                            )
                        }
                    }
                }
            }

            if (stackedStats) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    statsList.forEach { card -> card() }
                }
            } else {
                // Inline mode: Dynamic, responsive grid layout that adapts based on the total number of metric cards
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    when (statsList.size) {
                        1 -> {
                            statsList[0]()
                        }
                        2 -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) { statsList[0]() }
                                Box(modifier = Modifier.weight(1f)) { statsList[1]() }
                            }
                        }
                        3 -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) { statsList[0]() }
                                Box(modifier = Modifier.weight(1f)) { statsList[1]() }
                                Box(modifier = Modifier.weight(1f)) { statsList[2]() }
                            }
                        }
                        4 -> {
                            // Two equal rows of 2 for balanced structure
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) { statsList[0]() }
                                Box(modifier = Modifier.weight(1f)) { statsList[1]() }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) { statsList[2]() }
                                Box(modifier = Modifier.weight(1f)) { statsList[3]() }
                            }
                        }
                        else -> {
                            // 5 or more: Row of 3 on top, and remaining in the second row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) { statsList[0]() }
                                Box(modifier = Modifier.weight(1f)) { statsList[1]() }
                                Box(modifier = Modifier.weight(1f)) { statsList[2]() }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                for (j in 3 until statsList.size) {
                                    Box(modifier = Modifier.weight(1f)) { statsList[j]() }
                                }
                                val remaining = 3 - (statsList.size - 3)
                                if (remaining > 0) {
                                    repeat(remaining) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Total Words - always full width row
            StatCard(
                count = formatNumber(totalWordsRead, shortenNumbers),
                label = if (language == "en") "Total words read" else "Прочитано слов за всё время",
                icon = Icons.Rounded.TextFields,
                color = AccentOrange,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // STATUS PROGRESS BARS BLOCK
            CategoryHeader(if (language == "en") "By Status" else "По статусам")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 4.dp)
            ) {
                // List states: Planned=0, Reading=1, Paused=2, Completed=3, Dropped=4
                val statuses = listOf(1, 0, 3, 2, 4) // Order of displaying: reading, planned, completed, paused, dropped
                val totalBooks = books.size.coerceAtLeast(1)

                statuses.forEachIndexed { index, st ->
                    val statusCount = books.count { it.status == st }
                    val ratio = statusCount.toFloat() / totalBooks.toFloat()
                    val statusColor = getStatusColor(st)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(9.dp)
                                        .clip(CircleShape)
                                        .background(statusColor)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = getStatusText(st),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            // Badge with count
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(statusColor.copy(alpha = 0.12f))
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = statusCount.toString(),
                                    color = statusColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress line indicator
                        LinearProgressIndicator(
                            progress = { ratio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = statusColor,
                            trackColor = statusColor.copy(alpha = 0.10f)
                        )
                    }

                    if (index < statuses.size - 1) {
                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Settings Tile Row Shortcut links to SettingsScreen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(onClick = onNavigateToSettings)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentOrange.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = null,
                        tint = AccentOrange,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1.0f)) {
                    Text(
                        text = Locales.getString("settings", language),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (language == "en") "Manage features, theme, export" else "Управление функциями, тема, экспорт",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun StatCard(
    count: String,
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
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

        Spacer(modifier = Modifier.height(10.dp))

        // Big numeric value text
        Text(
            text = count,
            fontSize = 28.sp,
            fontWeight = FontWeight.W800,
            lineHeight = 31.sp,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Description label
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            lineHeight = 15.sp
        )
    }
}
