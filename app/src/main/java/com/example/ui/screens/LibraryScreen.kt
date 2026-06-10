@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import com.example.model.Book
import com.example.ui.Locales
import com.example.viewmodel.ReadTrackerViewModel
import com.example.ui.theme.AccentOrange

private fun lerpColor(start: Color, stop: Color, fraction: Float): Color {
    val f = fraction.coerceIn(0f, 1f)
    return Color(
        red = start.red + (stop.red - start.red) * f,
        green = start.green + (stop.green - start.green) * f,
        blue = start.blue + (stop.blue - start.blue) * f,
        alpha = start.alpha + (stop.alpha - start.alpha) * f
    )
}

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun LibraryScreen(
    viewModel: ReadTrackerViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onOpenShareSheet: () -> Unit
) {
    val books by viewModel.books.collectAsState()
    val showCovers by viewModel.showCovers.collectAsState()
    val showShareButton by viewModel.showShareButton.collectAsState()
    val showBookmarks by viewModel.showBookmarks.collectAsState()
    val bookmarkPosition by viewModel.bookmarkPosition.collectAsState()
    val enableRating by viewModel.enableRating.collectAsState()
    val ratingScale by viewModel.ratingScale.collectAsState()
    val badgeLayoutMode by viewModel.badgeLayoutMode.collectAsState()
    val enableAdaptationStart by viewModel.enableAdaptationStart.collectAsState()
    val showWebChapters by viewModel.showWebChapters.collectAsState()
    val filterSpacing by viewModel.filterSpacing.collectAsState()
    val cardSpacing by viewModel.cardSpacing.collectAsState()
    val titleFontSize by viewModel.titleFontSize.collectAsState()
    val libraryTitleFontSize by viewModel.libraryTitleFontSize.collectAsState()
    val language by viewModel.language.collectAsState()
    
    val savedTabIndex by viewModel.savedTabIndex.collectAsState()

    // Dynamic colors state collection
    val colorAccentHex by viewModel.colorAccent.collectAsState()
    val colorFormatHybridHex by viewModel.colorFormatHybrid.collectAsState()
    val colorFormatSeriesHex by viewModel.colorFormatSeries.collectAsState()
    val colorFormatWebHex by viewModel.colorFormatWeb.collectAsState()
    val colorFormatSingleHex by viewModel.colorFormatSingle.collectAsState()
    val colorStatusPlannedHex by viewModel.colorStatusPlanned.collectAsState()
    val colorStatusReadingHex by viewModel.colorStatusReading.collectAsState()
    val colorStatusPausedHex by viewModel.colorStatusPaused.collectAsState()
    val colorStatusCompletedHex by viewModel.colorStatusCompleted.collectAsState()
    val colorStatusDroppedHex by viewModel.colorStatusDropped.collectAsState()
    
    var bookToDelete by remember { mutableStateOf<Book?>(null) }

    // Categories tabs: All=0, Reading=1, Planned=2, Completed=3, Paused=4, Dropped=5
    // Database statuses match: 0=Planned, 1=Reading, 2=Paused, 3=Completed, 4=Dropped
    // Let's create category map to UI status
    val tabNames = listOf(
        if (language == "en") "All" else "Все",
        Locales.getString("reading", language),
        Locales.getString("planned", language),
        Locales.getString("completed", language),
        Locales.getString("paused", language),
        Locales.getString("dropped", language)
    )
    val currentTab = savedTabIndex

    // Set up PagerState with bidirectional synchronization to savedTabIndex StateFlow
    val pagerState = rememberPagerState(initialPage = savedTabIndex) { tabNames.size }

    // Sync from ViewModel to PagerState (e.g. initial load or external update)
    LaunchedEffect(savedTabIndex) {
        if (pagerState.currentPage != savedTabIndex) {
            pagerState.animateScrollToPage(savedTabIndex)
        }
    }

    // Sync from PagerState to ViewModel (on swipe/drag selection)
    LaunchedEffect(pagerState.currentPage) {
        if (savedTabIndex != pagerState.currentPage) {
            viewModel.setSavedTabIndex(pagerState.currentPage)
        }
    }

    // Filter books helper
    val getFilteredBooksForTab = remember(books) {
        { pageIndex: Int ->
            if (pageIndex == 0) {
                books
            } else {
                val targetStatus = when (pageIndex) {
                    1 -> 1 // Reading
                    2 -> 0 // Planned
                    3 -> 3 // Completed
                    4 -> 2 // Paused
                    5 -> 4 // Dropped
                    else -> 0
                }
                books.filter { it.status == targetStatus }
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Добавить",
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = getAdaptiveStatusBarPadding())
        ) {
            // Unified header Row for precise spacing close to status bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 12.dp, top = 2.dp, bottom = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = Locales.getString("library", language),
                    fontSize = libraryTitleFontSize.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    modifier = Modifier.wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onNavigateToAnalytics,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Analytics,
                            contentDescription = Locales.getString("analytics", language),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    if (showShareButton) {
                        IconButton(
                            onClick = onOpenShareSheet,
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.IosShare,
                                contentDescription = if (language == "en") "Share" else "Поделиться",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(filterSpacing.dp))

            val coroutineScope = rememberCoroutineScope()
            val lazyListState = rememberLazyListState()

            LaunchedEffect(pagerState.currentPage) {
                val currentPage = pagerState.currentPage
                // Keep the tab bar scrolled to 0 for early tabs ( Все, Читаю, Планирую, Завершено)
                // This keeps them visible and avoids abrupt hiding or sharp scrolling transitions.
                // We only perform smooth, single-step offsets of 1 item when swiping to the very ending tabs.
                when (currentPage) {
                    4 -> lazyListState.animateScrollToItem(1)
                    5 -> lazyListState.animateScrollToItem(2)
                    else -> lazyListState.animateScrollToItem(0)
                }
            }

            // Custom horizontal filters with close spacing and underline indicator
            LazyRow(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(tabNames) { index, name ->
                    // Calculate how "active" this tab is based on pager scrolling
                    val pageOffset = pagerState.currentPage - index + pagerState.currentPageOffsetFraction
                    val selectionProgress = (1f - Math.abs(pageOffset)).coerceIn(0f, 1f)

                    val textColor = lerpColor(
                        Color.Gray,
                        MaterialTheme.colorScheme.primary,
                        selectionProgress
                    )
                    
                    val indicatorWidth = (22 * selectionProgress).dp
                    val indicatorAlpha = selectionProgress

                    Column(
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                indication = null
                            ) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                            .padding(bottom = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = name,
                            fontSize = 14.sp,
                            fontWeight = if (selectionProgress > 0.5f) FontWeight.W800 else FontWeight.W500,
                            color = textColor,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        // Underline indicator with dynamic size and alpha transitions
                        Box(
                            modifier = Modifier
                                .height(2.5.dp)
                                .width(indicatorWidth)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = indicatorAlpha),
                                    shape = RoundedCornerShape(1.25.dp)
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f)
            ) { pageIndex ->
                val booksForPage = remember(books, pageIndex) {
                    getFilteredBooksForTab(pageIndex)
                }

                if (booksForPage.isEmpty()) {
                    // Empty Library state
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.MenuBook,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = Locales.getString("no_books", language),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == "en") "Tap + to add a title" else "Нажмите + чтобы добавить тайтл",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    // Loaded Books listing
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(booksForPage, key = { it.id }) { book ->
                            BookRowItem(
                                book = book,
                                language = language,
                                showCovers = showCovers,
                                showBookmarks = showBookmarks,
                                bookmarkPosition = bookmarkPosition,
                                enableRating = enableRating,
                                ratingScale = ratingScale,
                                badgeLayoutMode = badgeLayoutMode,
                                enableAdaptationStart = enableAdaptationStart,
                                showWebChapters = showWebChapters,
                                cardSpacing = cardSpacing,
                                titleFontSize = titleFontSize,
                                onClick = { onNavigateToEdit(book.id) },
                                onLongClick = { bookToDelete = book },
                                accentHex = colorAccentHex,
                                hybridHex = colorFormatHybridHex,
                                seriesHex = colorFormatSeriesHex,
                                webHex = colorFormatWebHex,
                                singleHex = colorFormatSingleHex,
                                plannedHex = colorStatusPlannedHex,
                                readingHex = colorStatusReadingHex,
                                pausedHex = colorStatusPausedHex,
                                completedHex = colorStatusCompletedHex,
                                droppedHex = colorStatusDroppedHex
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete confirm dialogue
    bookToDelete?.let { book ->
        AlertDialog(
            onDismissRequest = { bookToDelete = null },
            title = {
                Text(
                    text = if (language == "en") "Delete title?" else "Удалить тайтл?",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            text = {
                Text(
                    text = if (language == "en") "«${book.title}» will be deleted permanently." else "«${book.title}» будет удалён без возможности восстановления.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteBook(book.id)
                        bookToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFF87171))
                ) {
                    Text(Locales.getString("delete", language), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { bookToDelete = null },
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookRowItem(
    book: Book,
    language: String,
    showCovers: Boolean,
    showBookmarks: Boolean,
    bookmarkPosition: Int, // 0 = Bottom, 1 = Inline
    enableRating: Boolean,
    ratingScale: Int,
    badgeLayoutMode: Int,
    enableAdaptationStart: Boolean,
    showWebChapters: Boolean,
    cardSpacing: Float,
    titleFontSize: Float,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    accentHex: String = "#FF9F0A",
    hybridHex: String = "#FF9F0A",
    seriesHex: String = "#A78BFA",
    webHex: String = "#FBBF24",
    singleHex: String = "#FF9F0A",
    plannedHex: String = "#60A5FA",
    readingHex: String = "#34D399",
    pausedHex: String = "#FBBF24",
    completedHex: String = "#A78BFA",
    droppedHex: String = "#F87171"
) {
    val statusColor = getStatusColor(book.status, plannedHex, readingHex, pausedHex, completedHex, droppedHex, accentHex)
    val cardBackground = MaterialTheme.colorScheme.surface
    val shorten = false // Standard formatting inside rows
    val parsedAccentColor = remember(accentHex) { parseHexColor(accentHex, AccentOrange) }
    val parsedReadingColor = remember(readingHex) { parseHexColor(readingHex, Color(0xFF34D399)) }

    if (showCovers) {
        // Detailed row with cover thumbnail
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 2.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(cardBackground)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
                .padding(horizontal = 10.dp, vertical = (8f + cardSpacing).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Cover
            Box(
                modifier = Modifier
                    .size(width = 38.dp, height = 52.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(book.coverColor).copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                if (!book.coverUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = book.coverUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Image,
                        contentDescription = null,
                        tint = Color.Gray.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // 2. Middle summary area (Expanded)
            Column(
                modifier = Modifier.weight(1.0f),
                verticalArrangement = Arrangement.spacedBy(cardSpacing.dp)
            ) {
                // Line 1: Title only (takes full available width)
                Text(
                    text = book.title,
                    fontSize = titleFontSize.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Line 2: Status Indicator only
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = getStatusText(book.status, language),
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Line 3: Progress Data
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Words count
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.TextFields,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${formatNumber(book.effectiveWords, shorten)} ${if (language == "en") "w." else "сл."}",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }

                    // Volume progress
                    if (book.countVolumes && !book.isWeb) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Layers,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = book.volumeLabel(),
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Chapters Progress
                    if (showWebChapters && (book.isWeb || book.isHybridFormat)) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.FormatListNumbered,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = book.chapterLabel(),
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Bookmark - inline mode
                    if (showBookmarks && bookmarkPosition == 1 && !book.currentBookmark.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1.0f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Bookmark,
                                contentDescription = null,
                                tint = AccentOrange,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = book.currentBookmark,
                                color = AccentOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Line 4: Bookmark - bottom mode
                if (showBookmarks && bookmarkPosition == 0 && !book.currentBookmark.isNullOrBlank()) {
                     Row(
                         modifier = Modifier.fillMaxWidth(),
                         verticalAlignment = Alignment.CenterVertically
                     ) {
                         Icon(
                             imageVector = Icons.Rounded.Bookmark,
                             contentDescription = null,
                             tint = AccentOrange,
                             modifier = Modifier.size(11.dp)
                         )
                         Spacer(modifier = Modifier.width(3.dp))
                         Text(
                             text = book.currentBookmark,
                             color = AccentOrange,
                             fontSize = 11.sp,
                             fontWeight = FontWeight.SemiBold,
                             maxLines = 1,
                             overflow = TextOverflow.Ellipsis
                         )
                     }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 3. Right side: Badges and Chevron
            if (badgeLayoutMode == 1) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (enableRating && book.rating != null) {
                        BookBadge(
                            text = book.getRatingDisplay(ratingScale),
                            color = parsedAccentColor
                        )
                    }

                    val badgeColor = getFormatColor(book.isHybridFormat, book.isWeb, book.isSingle, book.isSeries, hybridHex, seriesHex, webHex, singleHex, accentHex)
                    when {
                        book.isHybridFormat -> BookBadge("LN+WN", badgeColor)
                        book.isWeb -> BookBadge(if (language == "en") "Web" else "Веб", badgeColor)
                        book.isSingle -> BookBadge(if (language == "en") "Single" else "Сингл", badgeColor)
                        book.isSeries -> BookBadge(if (language == "en") "Series" else "Серия", badgeColor)
                    }
                    if (book.isOngoing) {
                        BookBadge(if (language == "en") "Ong." else "Онг.", parsedReadingColor)
                    }

                    if (enableAdaptationStart) {
                        if (book.isSeries && book.startVolume != null) {
                            BookBadge("${if (language == "en") "Start: v." else "Старт: т."} ${book.startVolume}", parsedReadingColor)
                        } else if ((book.isWeb || book.isHybridFormat) && book.startChapter != null) {
                            BookBadge("${if (language == "en") "Start: ch." else "Старт: гл."} ${book.startChapter}", parsedReadingColor)
                        }
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (enableRating && book.rating != null) {
                        BookBadge(
                            text = book.getRatingDisplay(ratingScale),
                            color = parsedAccentColor
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val badgeColor = getFormatColor(book.isHybridFormat, book.isWeb, book.isSingle, book.isSeries, hybridHex, seriesHex, webHex, singleHex, accentHex)
                        when {
                            book.isHybridFormat -> BookBadge("LN+WN", badgeColor)
                            book.isWeb -> BookBadge(if (language == "en") "Web" else "Веб", badgeColor)
                            book.isSingle -> BookBadge(if (language == "en") "Single" else "Сингл", badgeColor)
                            book.isSeries -> BookBadge(if (language == "en") "Series" else "Серия", badgeColor)
                        }
                        if (book.isOngoing) {
                            BookBadge(if (language == "en") "Ong." else "Онг.", parsedReadingColor)
                        }
                    }

                    if (enableAdaptationStart) {
                        if (book.isSeries && book.startVolume != null) {
                            BookBadge("${if (language == "en") "Start: v." else "Старт: т."} ${book.startVolume}", parsedReadingColor)
                        } else if ((book.isWeb || book.isHybridFormat) && book.startChapter != null) {
                            BookBadge("${if (language == "en") "Start: ch." else "Старт: гл."} ${book.startChapter}", parsedReadingColor)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // 4. Arrow chevron
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    } else {
        // Compact row item (Without cover thumbnails)
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 2.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(cardBackground)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
                .padding(horizontal = 12.dp, vertical = (8f + cardSpacing).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status bar strip on extreme left
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height((28f + cardSpacing * 1.5f + (titleFontSize - 14f)).dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(statusColor)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1.0f),
                verticalArrangement = Arrangement.spacedBy(cardSpacing.dp)
            ) {
                // Line 1: Title only
                Text(
                    text = book.title,
                    fontSize = titleFontSize.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Line 2: Progress indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Words count
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.TextFields,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${formatNumber(book.effectiveWords, shorten)} ${if (language == "en") "w." else "сл."}",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }

                    // Volume progress
                    if (book.countVolumes && !book.isWeb) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Layers,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = book.volumeLabel(),
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Chapters Progress
                    if (showWebChapters && (book.isWeb || book.isHybridFormat)) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.FormatListNumbered,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = book.chapterLabel(),
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Bookmark - inline mode
                    if (showBookmarks && bookmarkPosition == 1 && !book.currentBookmark.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1.0f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Bookmark,
                                contentDescription = null,
                                tint = AccentOrange,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = book.currentBookmark,
                                color = AccentOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Line 3: Bookmark - bottom mode
                if (showBookmarks && bookmarkPosition == 0 && !book.currentBookmark.isNullOrBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Bookmark,
                            contentDescription = null,
                            tint = AccentOrange,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = book.currentBookmark,
                            color = AccentOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right side: Badges Column and Chevron
            if (badgeLayoutMode == 1) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (enableRating && book.rating != null) {
                        BookBadge(
                            text = book.getRatingDisplay(ratingScale),
                            color = parsedAccentColor
                        )
                    }

                    val badgeColor = getFormatColor(book.isHybridFormat, book.isWeb, book.isSingle, book.isSeries, hybridHex, seriesHex, webHex, singleHex, accentHex)
                    when {
                        book.isHybridFormat -> BookBadge("LN+WN", badgeColor)
                        book.isWeb -> BookBadge(if (language == "en") "Web" else "Веб", badgeColor)
                        book.isSingle -> BookBadge(if (language == "en") "Single" else "Сингл", badgeColor)
                        book.isSeries -> BookBadge(if (language == "en") "Series" else "Серия", badgeColor)
                    }
                    if (book.isOngoing) {
                        BookBadge(if (language == "en") "Ong." else "Онг.", parsedReadingColor)
                    }

                    if (enableAdaptationStart) {
                        if (book.isSeries && book.startVolume != null) {
                            BookBadge("${if (language == "en") "Start: v." else "Старт: т."} ${book.startVolume}", parsedReadingColor)
                        } else if ((book.isWeb || book.isHybridFormat) && book.startChapter != null) {
                            BookBadge("${if (language == "en") "Start: ch." else "Старт: гл."} ${book.startChapter}", parsedReadingColor)
                        }
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (enableRating && book.rating != null) {
                        BookBadge(
                            text = book.getRatingDisplay(ratingScale),
                            color = parsedAccentColor
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val badgeColor = getFormatColor(book.isHybridFormat, book.isWeb, book.isSingle, book.isSeries, hybridHex, seriesHex, webHex, singleHex, accentHex)
                        when {
                            book.isHybridFormat -> BookBadge("LN+WN", badgeColor)
                            book.isWeb -> BookBadge(if (language == "en") "Web" else "Веб", badgeColor)
                            book.isSingle -> BookBadge(if (language == "en") "Single" else "Сингл", badgeColor)
                            book.isSeries -> BookBadge(if (language == "en") "Series" else "Серия", badgeColor)
                        }
                        if (book.isOngoing) {
                            BookBadge(if (language == "en") "Ong." else "Онг.", parsedReadingColor)
                        }
                    }

                    if (enableAdaptationStart) {
                        if (book.isSeries && book.startVolume != null) {
                            BookBadge("${if (language == "en") "Start: v." else "Старт: т."} ${book.startVolume}", parsedReadingColor)
                        } else if ((book.isWeb || book.isHybridFormat) && book.startChapter != null) {
                            BookBadge("${if (language == "en") "Start: ch." else "Старт: гл."} ${book.startChapter}", parsedReadingColor)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Arrow chevron
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
