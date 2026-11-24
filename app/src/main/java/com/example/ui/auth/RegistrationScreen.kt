package com.example.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.EditorialCatalog
import com.example.data.HijrahPhase
import com.example.data.UserProfile
import com.example.ui.components.HijrahDarkCard
import com.example.ui.components.HijrahElevatedCard
import com.example.ui.components.HijrahPrimaryButton
import com.example.ui.components.LanguagePillBar
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.i18n.localizedSubtitle
import com.example.ui.i18n.localizedTitle
import com.example.ui.theme.HijrahArabicStyles
import com.example.ui.theme.HijrahBorderGold
import com.example.ui.theme.HijrahBorderSubtle
import com.example.ui.theme.HijrahBrown
import com.example.ui.theme.HijrahDarkBrown
import com.example.ui.theme.HijrahErrorRust
import com.example.ui.theme.HijrahGold
import com.example.ui.theme.HijrahRadii
import com.example.ui.theme.HijrahRust
import com.example.ui.theme.HijrahSurfaceTinted
import com.example.ui.theme.HijrahTextMuted
import com.example.ui.theme.HijrahTextSecondary
import com.example.ui.theme.HijrahWhite

@Composable
fun RegistrationScreen(
    defaultAlias: String,
    existingProfile: UserProfile?,
    isSaving: Boolean,
    onSubmit: (
        displayAlias: String,
        originCountry: String,
        currentCity: String,
        phase: HijrahPhase,
        focusCountry: String,
        focusCity: String
    ) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var displayAlias by remember(existingProfile, defaultAlias) {
        mutableStateOf(
            existingProfile?.displayAlias?.takeIf { it.isNotBlank() } ?: defaultAlias
        )
    }
    var originCountry by remember(existingProfile) {
        mutableStateOf(existingProfile?.originCountry.orEmpty())
    }
    var currentCity by remember(existingProfile) {
        mutableStateOf(existingProfile?.currentCity.orEmpty())
    }
    var selectedPhase by remember(existingProfile) {
        mutableStateOf(existingProfile?.phaseEnum() ?: HijrahPhase.ORIENTATIE)
    }
    var focusCountry by remember(existingProfile) {
        mutableStateOf(
            existingProfile?.focusCountry?.takeIf { it.isNotBlank() } ?: "Nog aan het verkennen"
        )
    }
    var focusCity by remember(existingProfile) {
        mutableStateOf(existingProfile?.focusCity.orEmpty())
    }
    var validationError by remember { mutableStateOf<String?>(null) }
    val strings = LocalAppStrings.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HijrahWhite)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .testTag("registration_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LanguagePillBar()

            // Header row with emblem, title & sign-out option
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(HijrahRadii.Chip)
                            .background(HijrahDarkBrown)
                            .border(1.dp, HijrahBorderGold, HijrahRadii.Chip),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "هـ",
                            style = HijrahArabicStyles.KufiAccent,
                            color = HijrahGold
                        )
                    }
                    Column {
                        Text(
                            text = strings.regTitle,
                            style = MaterialTheme.typography.headlineLarge,
                            color = HijrahDarkBrown
                        )
                        Text(
                            text = "إنشاء الملف الشخصي",
                            style = HijrahArabicStyles.KufiAccent,
                            color = HijrahBrown
                        )
                    }
                }

                IconButton(
                    onClick = onSignOut,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("registration_sign_out_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Logout,
                        contentDescription = strings.regSignOutCd,
                        tint = HijrahBrown
                    )
                }
            }

            HijrahDarkCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(18.dp)
            ) {
                Text(
                    text = strings.regWelcomeBadge,
                    style = MaterialTheme.typography.labelSmall,
                    color = HijrahGold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.regWelcomeBody,
                    style = MaterialTheme.typography.bodyMedium,
                    color = HijrahWhite
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.regWelcomePrivacy,
                    style = MaterialTheme.typography.bodySmall,
                    color = HijrahWhite.copy(alpha = 0.78f)
                )
            }

            // Card 1: Personal basis + Origin country & Current city
            HijrahElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(18.dp)
            ) {
                Text(
                    text = strings.regBasicInfo,
                    style = MaterialTheme.typography.titleMedium,
                    color = HijrahDarkBrown
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = displayAlias,
                    onValueChange = {
                        displayAlias = it.take(40)
                        validationError = null
                    },
                    label = { Text(strings.regAliasLabel) },
                    leadingIcon = {
                        Icon(Icons.Rounded.Person, contentDescription = null, tint = HijrahBrown)
                    },
                    singleLine = true,
                    shape = HijrahRadii.Input,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HijrahRust,
                        unfocusedBorderColor = HijrahBorderSubtle
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("registration_alias_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = strings.regOriginTitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = HijrahDarkBrown
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EditorialCatalog.originCountrySuggestions.forEach { country ->
                        val selected = originCountry.equals(country, ignoreCase = true)
                        Surface(
                            shape = HijrahRadii.Chip,
                            color = if (selected) HijrahRust else HijrahSurfaceTinted,
                            border = BorderStroke(1.dp, if (selected) HijrahRust else HijrahBorderSubtle),
                            modifier = Modifier
                                .clip(HijrahRadii.Chip)
                                .clickable {
                                    originCountry = if (country == "Anders") "" else country
                                    validationError = null
                                }
                        ) {
                            Text(
                                text = country,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selected) HijrahWhite else HijrahDarkBrown,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = originCountry,
                    onValueChange = {
                        originCountry = it.take(60)
                        validationError = null
                    },
                    label = { Text(strings.regOriginLabel) },
                    leadingIcon = {
                        Icon(Icons.Rounded.Public, contentDescription = null, tint = HijrahBrown)
                    },
                    singleLine = true,
                    shape = HijrahRadii.Input,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HijrahRust,
                        unfocusedBorderColor = HijrahBorderSubtle
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("registration_origin_country_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = currentCity,
                    onValueChange = {
                        currentCity = it.take(60)
                        validationError = null
                    },
                    label = { Text(strings.regCurrentCityLabel) },
                    leadingIcon = {
                        Icon(Icons.Rounded.Home, contentDescription = null, tint = HijrahBrown)
                    },
                    singleLine = true,
                    shape = HijrahRadii.Input,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HijrahRust,
                        unfocusedBorderColor = HijrahBorderSubtle
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("registration_current_city_input")
                )
            }

            // Card 2: Trajectory phase & orientation target
            HijrahElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(18.dp)
            ) {
                Text(
                    text = strings.regCurrentPhaseTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = HijrahDarkBrown
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = strings.regCurrentPhaseSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = HijrahTextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HijrahPhase.entries.forEach { phase ->
                        val selected = phase == selectedPhase
                        Surface(
                            shape = HijrahRadii.Input,
                            color = if (selected) HijrahDarkBrown else HijrahWhite,
                            border = BorderStroke(1.dp, if (selected) HijrahBorderGold else HijrahBorderSubtle),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(HijrahRadii.Input)
                                .clickable { selectedPhase = phase }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = phase.localizedTitle(strings),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = if (selected) HijrahWhite else HijrahDarkBrown
                                    )
                                    Text(
                                        text = phase.localizedSubtitle(strings),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (selected) HijrahWhite.copy(alpha = 0.8f) else HijrahTextMuted
                                    )
                                }
                                if (selected) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = HijrahGold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = strings.regFocusCountryLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = HijrahDarkBrown
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EditorialCatalog.focusCountrySuggestions.forEach { country ->
                        val selected = focusCountry == country
                        Surface(
                            shape = HijrahRadii.Chip,
                            color = if (selected) HijrahRust else HijrahSurfaceTinted,
                            border = BorderStroke(1.dp, if (selected) HijrahRust else HijrahBorderSubtle),
                            modifier = Modifier
                                .clip(HijrahRadii.Chip)
                                .clickable { focusCountry = country }
                        ) {
                            Text(
                                text = country,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selected) HijrahWhite else HijrahDarkBrown,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = focusCity,
                    onValueChange = { focusCity = it.take(60) },
                    label = { Text(strings.regFocusCityLabel) },
                    singleLine = true,
                    shape = HijrahRadii.Input,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HijrahRust,
                        unfocusedBorderColor = HijrahBorderSubtle
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (validationError != null) {
                Surface(
                    shape = HijrahRadii.Input,
                    color = HijrahWhite,
                    border = BorderStroke(1.dp, HijrahErrorRust),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("registration_error_banner")
                ) {
                    Text(
                        text = validationError ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = HijrahErrorRust,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            if (isSaving) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        color = HijrahRust,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                    Text(
                        text = "  ${strings.regSaving}",
                        style = MaterialTheme.typography.labelLarge,
                        color = HijrahDarkBrown
                    )
                }
            } else {
                HijrahPrimaryButton(
                    text = strings.regSubmitButton,
                    onClick = {
                        val cleanAlias = displayAlias.trim()
                        val cleanOrigin = originCountry.trim()
                        val cleanCity = currentCity.trim()
                        when {
                            cleanAlias.length < 2 ->
                                validationError = strings.regErrAlias
                            cleanOrigin.length < 2 ->
                                validationError = strings.regErrOrigin
                            cleanCity.length < 2 ->
                                validationError = strings.regErrCity
                            else -> onSubmit(
                                cleanAlias,
                                cleanOrigin,
                                cleanCity,
                                selectedPhase,
                                focusCountry,
                                focusCity.trim()
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("registration_submit_button")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
