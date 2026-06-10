package com.example.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class VolumeEntry(
    val v: Double = 0.0, // Volume number
    val w: Int = 0       // Word count
)

data class Book(
    val id: String = "",
    val title: String = "",
    val status: Int = 0, // 0=planned, 1=reading, 2=paused, 3=completed, 4=dropped
    val isSeries: Boolean = true,
    val isWeb: Boolean = false,
    val isSingle: Boolean = false,
    val countVolumes: Boolean = true,
    val words: Int = 0,
    val volumes: Int = 0,
    val totalVolumesInSeries: Int? = null,
    val isOngoing: Boolean = false,
    val coverColor: Int = -0x1E1E1F, // Default dark cover gray
    val coverUrl: String? = null,
    val localImagePath: String? = null,
    val useDetailedVolumes: Boolean = false,
    val volumeEntries: List<VolumeEntry> = emptyList(),
    val webChapters: Int? = null,
    val totalWebChapters: Int? = null,
    val currentBookmark: String? = null,
    val isHybridFormat: Boolean = false,
    val hybridWebChapters: Int? = null,
    val hybridTotalWebChapters: Int? = null,
    val rating: Int? = null, // Stored 1 to 10
    val startVolume: Int? = null,
    val startChapter: Int? = null
) {
    val effectiveWords: Int
        get() = if (useDetailedVolumes) volumeEntries.sumOf { it.w } else words

    val effectiveVolumes: Int
        get() = if (useDetailedVolumes) volumeEntries.size else volumes

    fun volumeLabel(): String {
        val count = effectiveVolumes
        return when {
            isOngoing -> "$count/? т."
            totalVolumesInSeries != null -> "$count/$totalVolumesInSeries т."
            else -> "$count т."
        }
    }

    fun chapterLabel(showTotal: Boolean = true): String {
        val current = if (isHybridFormat) hybridWebChapters else webChapters
        val total = if (isHybridFormat) hybridTotalWebChapters else totalWebChapters
        
        val currVal = current ?: 0
        return if (total != null && showTotal) {
            "$currVal/$total гл."
        } else {
            "$currVal гл."
        }
    }

    fun getRatingDisplay(scale: Int): String {
        val r = rating ?: return ""
        return if (scale == 5) {
            val d = (r + 1) / 2 // Translate 10 scale to 5 scale
            "$d/5 ★"
        } else {
            "$r/10 ★"
        }
    }
}

data class SettingsData(
    val themeMode: Int = 1,
    val shortenNumbers: Boolean = false,
    val showShareButton: Boolean = false,
    val stackedStats: Boolean = false,
    val showCovers: Boolean = false,
    val showWebChapters: Boolean = true,
    val showBookmarks: Boolean = true,
    val bookmarkPosition: Int = 0,
    val enableAdaptationStart: Boolean = false,
    val enableHybrid: Boolean = true,
    val enableRating: Boolean = true,
    val ratingScale: Int = 10,
    val badgeLayoutMode: Int = 0,
    val analyticsShowMode: Int = 0,
    val showWebInStats: Boolean = true,
    val disableAnimations: Boolean = false,
    val cardSpacing: Float = 2.0f,
    val titleFontSize: Float = 14.0f,
    val libraryTitleFontSize: Float = 28.0f,
    val filterSpacing: Float = 0.0f,
    val colorAccent: String = "#FF9F0A",
    val colorFormatHybrid: String = "#FF9F0A",
    val colorFormatSeries: String = "#A78BFA",
    val colorFormatWeb: String = "#FBBF24",
    val colorFormatSingle: String = "#FF9F0A",
    val colorStatusPlanned: String = "#60A5FA",
    val colorStatusReading: String = "#34D399",
    val colorStatusPaused: String = "#FBBF24",
    val colorStatusCompleted: String = "#A78BFA",
    val colorStatusDropped: String = "#F87171",
    val language: String = "ru"
)

object JsonParser {
    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val bookListType = Types.newParameterizedType(List::class.java, Book::class.java)
    private val bookListAdapter = moshi.adapter<List<Book>>(bookListType)
    private val settingsAdapter = moshi.adapter(SettingsData::class.java)

    fun booksToJson(books: List<Book>): String {
        return bookListAdapter.toJson(books)
    }

    fun jsonToBooks(json: String?): List<Book> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            bookListAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun settingsToJson(settings: SettingsData): String {
        return settingsAdapter.toJson(settings)
    }

    fun jsonToSettings(json: String?): SettingsData? {
        if (json.isNullOrBlank()) return null
        return try {
            settingsAdapter.fromJson(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
