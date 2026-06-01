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

object JsonParser {
    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val bookListType = Types.newParameterizedType(List::class.java, Book::class.java)
    private val bookListAdapter = moshi.adapter<List<Book>>(bookListType)

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
}
