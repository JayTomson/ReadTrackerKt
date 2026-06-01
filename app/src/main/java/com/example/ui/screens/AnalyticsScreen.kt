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
import com.example.viewmodel.ReadTrackerViewModel
import com.example.ui.theme.AccentOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: ReadTrackerViewModel,
    onNavigateToSettings: () -> Unit
) {
    val books by viewModel.books.collectAsState()
    val shortenNumbers by viewModel.shortenNumbers.collectAsState()
    val stackedStats by viewModel.stackedStats.collectAsState()
    val showWebInStats by viewModel.showWebInStats.collectAsState()

    // Calculating Metrics
    val completedSeriesCount = books.count { it.status == 3 && !it.isWeb }
    val completedWebCount = books.count { it.status == 3 && it.isWeb }
    
    // Sum of all effective volumes read
    val totalVolumesRead = books.sumOf { if (it.countVolumes && !it.isWeb) it.effectiveVolumes else 0 }
    val hasBooksWithVolumes = books.any { it.countVolumes && !it.isWeb }

    val totalWordsRead = books.sumOf { it.effectiveWords }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Аналитика", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
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
            if (stackedStats) {
                // Stacked mode (vertical stack)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(
                        count = completedSeriesCount.toString(),
                        label = "Завершено серий",
                        icon = Icons.Rounded.EmojiEvents,
                        color = Color(0xFF34D399) // Green
                    )
                    if (showWebInStats) {
                        StatCard(
                            count = completedWebCount.toString(),
                            label = "Завершено веб",
                            icon = Icons.Rounded.Language,
                            color = Color(0xFFA78BFA) // Violet
                        )
                    }
                    if (hasBooksWithVolumes) {
                        StatCard(
                            count = totalVolumesRead.toString(),
                            label = "Прочитано томов",
                            icon = Icons.Rounded.Layers,
                            color = Color(0xFF60A5FA) // Blue
                        )
                    }
                }
            } else {
                // Inline mode (horizontal row with equal height)
                val enabledCardsNum = 1 + (if (showWebInStats) 1 else 0) + (if (hasBooksWithVolumes) 1 else 0)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            count = completedSeriesCount.toString(),
                            label = "Завершено\nсерий",
                            icon = Icons.Rounded.EmojiEvents,
                            color = Color(0xFF34D399)
                        )
                    }
                    if (showWebInStats) {
                        Box(modifier = Modifier.weight(1f)) {
                            StatCard(
                                count = completedWebCount.toString(),
                                label = "Завершено\nвеб",
                                icon = Icons.Rounded.Language,
                                color = Color(0xFFA78BFA)
                            )
                        }
                    }
                    if (hasBooksWithVolumes) {
                        Box(modifier = Modifier.weight(1f)) {
                            StatCard(
                                count = totalVolumesRead.toString(),
                                label = "Прочитано\nтомов",
                                icon = Icons.Rounded.Layers,
                                color = Color(0xFF60A5FA)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Total Words - always full width row
            StatCard(
                count = formatNumber(totalWordsRead, shortenNumbers),
                label = "Прочитано слов за всё время",
                icon = Icons.Rounded.TextFields,
                color = AccentOrange,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // STATUS PROGRESS BARS BLOCK
            CategoryHeader("По статусам")
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
                        text = "Настройки",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Управление функциями, тема, экспорт",
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
