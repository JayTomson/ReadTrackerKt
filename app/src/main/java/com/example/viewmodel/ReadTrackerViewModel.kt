package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.Book
import com.example.model.JsonParser
import com.example.model.PreferencesManager
import com.example.model.SettingsData
import com.example.ui.Locales
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReadTrackerViewModel(application: Application) : AndroidViewModel(application) {
    private val prefsManager = PreferencesManager(application)

    // Books list
    val books: StateFlow<List<Book>> = prefsManager.books

    // Settings StateFlows
    val themeMode: StateFlow<Int> = prefsManager.themeMode
    val shortenNumbers: StateFlow<Boolean> = prefsManager.shortenNumbers
    val showShareButton: StateFlow<Boolean> = prefsManager.showShareButton
    val stackedStats: StateFlow<Boolean> = prefsManager.stackedStats
    val showCovers: StateFlow<Boolean> = prefsManager.showCovers
    val showWebChapters: StateFlow<Boolean> = prefsManager.showWebChapters
    val showBookmarks: StateFlow<Boolean> = prefsManager.showBookmarks
    val bookmarkPosition: StateFlow<Int> = prefsManager.bookmarkPosition
    val enableAdaptationStart: StateFlow<Boolean> = prefsManager.enableAdaptationStart
    val enableHybrid: StateFlow<Boolean> = prefsManager.enableHybrid
    val enableRating: StateFlow<Boolean> = prefsManager.enableRating
    val ratingScale: StateFlow<Int> = prefsManager.ratingScale
    val badgeLayoutMode: StateFlow<Int> = prefsManager.badgeLayoutMode
    val analyticsShowMode: StateFlow<Int> = prefsManager.analyticsShowMode
    val showWebInStats: StateFlow<Boolean> = prefsManager.showWebInStats
    val savedTabIndex: StateFlow<Int> = prefsManager.savedTabIndex
    val disableAnimations: StateFlow<Boolean> = prefsManager.disableAnimations
    val cardSpacing: StateFlow<Float> = prefsManager.cardSpacing
    val titleFontSize: StateFlow<Float> = prefsManager.titleFontSize
    val libraryTitleFontSize: StateFlow<Float> = prefsManager.libraryTitleFontSize
    val filterSpacing: StateFlow<Float> = prefsManager.filterSpacing
    val language: StateFlow<String> = prefsManager.language

    // Custom colors
    val colorAccent: StateFlow<String> = prefsManager.colorAccent
    val colorFormatHybrid: StateFlow<String> = prefsManager.colorFormatHybrid
    val colorFormatSeries: StateFlow<String> = prefsManager.colorFormatSeries
    val colorFormatWeb: StateFlow<String> = prefsManager.colorFormatWeb
    val colorFormatSingle: StateFlow<String> = prefsManager.colorFormatSingle
    val colorStatusPlanned: StateFlow<String> = prefsManager.colorStatusPlanned
    val colorStatusReading: StateFlow<String> = prefsManager.colorStatusReading
    val colorStatusPaused: StateFlow<String> = prefsManager.colorStatusPaused
    val colorStatusCompleted: StateFlow<String> = prefsManager.colorStatusCompleted
    val colorStatusDropped: StateFlow<String> = prefsManager.colorStatusDropped

    // Notification State
    private val _toastMessage = MutableStateFlow<Pair<String, Boolean>?>(null) // Msg to isSuccess
    val toastMessage: StateFlow<Pair<String, Boolean>?> = _toastMessage.asStateFlow()

    // Temporary list of books parsed during import to display in validation dialog
    private val _pendingImportBooks = MutableStateFlow<List<Book>?>(null)
    val pendingImportBooks: StateFlow<List<Book>?> = _pendingImportBooks.asStateFlow()

    private val _pendingImportSettings = MutableStateFlow<SettingsData?>(null)
    val pendingImportSettings: StateFlow<SettingsData?> = _pendingImportSettings.asStateFlow()

    fun clearToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String, isSuccess: Boolean = true) {
        _toastMessage.value = Pair(msg, isSuccess)
    }

    fun setSavedTabIndex(index: Int) {
        prefsManager.setSavedTabIndex(index)
    }

    // Settings actions
    fun setThemeMode(mode: Int) = prefsManager.setThemeMode(mode)
    fun setShortenNumbers(v: Boolean) = prefsManager.setShortenNumbers(v)
    fun setShowShareButton(v: Boolean) = prefsManager.setShowShareButton(v)
    fun setStackedStats(v: Boolean) = prefsManager.setStackedStats(v)
    fun setShowCovers(v: Boolean) = prefsManager.setShowCovers(v)
    fun setShowWebChapters(v: Boolean) = prefsManager.setShowWebChapters(v)
    fun setShowBookmarks(v: Boolean) = prefsManager.setShowBookmarks(v)
    fun setBookmarkPosition(p: Int) = prefsManager.setBookmarkPosition(p)
    fun setEnableAdaptationStart(v: Boolean) = prefsManager.setEnableAdaptationStart(v)
    fun setEnableHybrid(v: Boolean) = prefsManager.setEnableHybrid(v)
    fun setEnableRating(v: Boolean) = prefsManager.setEnableRating(v)
    fun setRatingScale(s: Int) = prefsManager.setRatingScale(s)
    fun setBadgeLayoutMode(v: Int) = prefsManager.setBadgeLayoutMode(v)
    fun setAnalyticsShowMode(v: Int) = prefsManager.setAnalyticsShowMode(v)
    fun setShowWebInStats(v: Boolean) = prefsManager.setShowWebInStats(v)
    fun setDisableAnimations(v: Boolean) = prefsManager.setDisableAnimations(v)
    fun setCardSpacing(v: Float) = prefsManager.setCardSpacing(v)
    fun setTitleFontSize(v: Float) = prefsManager.setTitleFontSize(v)
    fun setLibraryTitleFontSize(v: Float) = prefsManager.setLibraryTitleFontSize(v)
    fun setFilterSpacing(v: Float) = prefsManager.setFilterSpacing(v)
    fun setLanguage(v: String) = prefsManager.setLanguage(v)

    fun setColorAccent(v: String) = prefsManager.setColorAccent(v)
    fun setColorFormatHybrid(v: String) = prefsManager.setColorFormatHybrid(v)
    fun setColorFormatSeries(v: String) = prefsManager.setColorFormatSeries(v)
    fun setColorFormatWeb(v: String) = prefsManager.setColorFormatWeb(v)
    fun setColorFormatSingle(v: String) = prefsManager.setColorFormatSingle(v)
    fun setColorStatusPlanned(v: String) = prefsManager.setColorStatusPlanned(v)
    fun setColorStatusReading(v: String) = prefsManager.setColorStatusReading(v)
    fun setColorStatusPaused(v: String) = prefsManager.setColorStatusPaused(v)
    fun setColorStatusCompleted(v: String) = prefsManager.setColorStatusCompleted(v)
    fun setColorStatusDropped(v: String) = prefsManager.setColorStatusDropped(v)
    fun resetColorsToDefault() = prefsManager.resetColorsToDefault()

    // Book Actions
    fun addBook(book: Book) {
        val list = books.value.toMutableList()
        list.add(0, book)
        prefsManager.saveBooks(list)
        showToast("Тайтл \"${book.title}\" успешно добавлен")
    }

    fun updateBook(book: Book) {
        val list = books.value.map { if (it.id == book.id) book else it }
        prefsManager.saveBooks(list)
        showToast("Тайтл \"${book.title}\" сохранён")
    }

    fun deleteBook(bookId: String) {
        val book = books.value.find { it.id == bookId }
        val title = book?.title ?: ""
        val list = books.value.filter { it.id != bookId }
        prefsManager.saveBooks(list)
        showToast("Тайтл \"$title\" удалён", isSuccess = false)
    }

    // Export & Import
    fun exportLibrary(context: Context) {
        viewModelScope.launch {
            try {
                val json = JsonParser.booksToJson(books.value)
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val filename = "readtracker_backup_$timestamp.json"
                
                val cacheFile = File(context.cacheDir, filename)
                cacheFile.writeText(json)

                val contentUri: Uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    cacheFile
                )

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/json"
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val lang = prefsManager.language.value
                val chooserTitle = Locales.getString("share_library_chooser", lang)
                val chooser = Intent.createChooser(intent, chooserTitle)
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
                showToast(Locales.getString("library_exported_toast", lang))
            } catch (e: Exception) {
                e.printStackTrace()
                val lang = prefsManager.language.value
                showToast(Locales.getString("export_err_toast", lang) + "${e.message}", isSuccess = false)
            }
        }
    }

    fun exportSettings(context: Context) {
        viewModelScope.launch {
            try {
                val json = JsonParser.settingsToJson(prefsManager.getAllSettings())
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val filename = "readtracker_settings_$timestamp.json"
                
                val cacheFile = File(context.cacheDir, filename)
                cacheFile.writeText(json)

                val contentUri: Uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    cacheFile
                )

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/json"
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val lang = prefsManager.language.value
                val chooserTitle = Locales.getString("share_settings_chooser", lang)
                val chooser = Intent.createChooser(intent, chooserTitle)
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
                showToast(Locales.getString("settings_exported_toast", lang))
            } catch (e: Exception) {
                e.printStackTrace()
                val lang = prefsManager.language.value
                showToast(Locales.getString("export_err_toast", lang) + "${e.message}", isSuccess = false)
            }
        }
    }

    fun handleImportUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.bufferedReader().use { it.readText() }
                }
                if (json.isNullOrBlank()) {
                    showToast("Файл пуст или поврежден", isSuccess = false)
                    return@launch
                }
                val imported = JsonParser.jsonToBooks(json)
                if (imported.isEmpty()) {
                    showToast("Не удалось найти книги в файле", isSuccess = false)
                } else {
                    _pendingImportBooks.value = imported
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Ошибка чтения файла: ${e.message}", isSuccess = false)
            }
        }
    }

    fun handleImportSettingsUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.bufferedReader().use { it.readText() }
                }
                if (json.isNullOrBlank()) {
                    showToast("Файл пуст или поврежден", isSuccess = false)
                    return@launch
                }
                val imported = JsonParser.jsonToSettings(json)
                if (imported == null) {
                    showToast("Не удалось найти настройки в файле", isSuccess = false)
                } else {
                    _pendingImportSettings.value = imported
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Ошибка чтения файла: ${e.message}", isSuccess = false)
            }
        }
    }

    fun confirmImport() {
        val imported = _pendingImportBooks.value ?: return
        prefsManager.saveBooks(imported)
        _pendingImportBooks.value = null
        showToast("Успешно импортировано тайтлов: ${imported.size}")
    }

    fun confirmImportSettings() {
        val imported = _pendingImportSettings.value ?: return
        prefsManager.applySettings(imported)
        _pendingImportSettings.value = null
        showToast("Настройки успешно импортированы")
    }

    fun cancelImport() {
        _pendingImportBooks.value = null
        _pendingImportSettings.value = null
    }
}
