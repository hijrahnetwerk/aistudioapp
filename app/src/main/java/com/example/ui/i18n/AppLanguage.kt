package com.example.ui.i18n

import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class AppLanguage(
    val code: String,
    val nativeLabel: String,
    val shortBadge: String,
    val isRtl: Boolean
) {
    DUTCH(
        code = "nl",
        nativeLabel = "Nederlands",
        shortBadge = "NL",
        isRtl = false
    ),
    ARABIC(
        code = "ar",
        nativeLabel = "العربية",
        shortBadge = "AR",
        isRtl = true
    ),
    ENGLISH(
        code = "en",
        nativeLabel = "English",
        shortBadge = "EN",
        isRtl = false
    );

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: DUTCH
    }
}

private val Context.hijrahLanguageDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "hijrah_language_prefs"
)

class LanguagePreferenceStore(private val context: Context) {
    private val languageKey = stringPreferencesKey("selected_app_language")

    val languageFlow: Flow<AppLanguage> = context.hijrahLanguageDataStore.data.map { prefs ->
        AppLanguage.fromCode(prefs[languageKey])
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.hijrahLanguageDataStore.edit { prefs ->
            prefs[languageKey] = language.code
        }
    }
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.DUTCH }
val LocalAppStrings = compositionLocalOf { NlHijrahStrings }
val LocalOnSelectLanguage = compositionLocalOf<(AppLanguage) -> Unit> { {} }

fun stringsForLanguage(language: AppLanguage): HijrahStrings = when (language) {
    AppLanguage.DUTCH -> NlHijrahStrings
    AppLanguage.ARABIC -> ArHijrahStrings
    AppLanguage.ENGLISH -> EnHijrahStrings
}
